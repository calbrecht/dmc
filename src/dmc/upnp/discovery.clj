(ns dmc.upnp.discovery
  (:require [vertx.embed :as vertx]
            [vertx.datagram :as udp]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]))

(defn init []
  (vertx/set-vertx! (vertx/vertx)))

(defn map-keys-to-lower-case [m]
  (reduce
   (fn [agg [k v]]
     (if (string? k)
       (assoc agg (string/lower-case k) v)
       (assoc agg k v)))
   {}
   m))

(defn parse-packet-data [packet]
  (as-> packet data
    (str data)
    (string/split data #"\r\n")
    (reduce
     (fn [agg line]
       (let [[key value] (string/split line #":" 2)]
         (if (and key value)
           (assoc agg key (string/trim value))
           agg)))
     {:start-line (first data)}
     (rest data))))

(defn parse-urn-type [data]
  (let [[domain resource-type resource-name version] (string/split data #":")]
    {:type :urn
     :domain domain
     :resource-type resource-type
     :resource-name resource-name
     :version version}))

(defn parse-nt [nt]
  (if (= nt "upnp:rootdevice")
    {:type :rootdevice}
    (let [[type type-rest] (string/split nt #":" 2)]
      (case type
            "urn" (parse-urn-type type-rest)
            "uuid" {:type :uuid}
            nil))))

(defn get-uuid-from-usn [usn]
  (let [[_ uuid] (string/split usn #":" 3)]
    uuid))

(defn parse-notification [packet]
  (merge packet
         {:nt (parse-nt (get packet "nt"))
          :uuid (get-uuid-from-usn (get packet "usn"))}))

(defn resource-type? [t packet]
  (and (= (-> packet :nt :type) :urn)
       (= (-> packet :nt :resource-type) t)))

(defn insert-device [devices packet]
  (assoc devices
         (-> packet :uuid)
         {:services {}
          :resource-name (-> packet :nt :resource-name)
          :version (-> packet :nt :version)}))

(defn insert-service [devices packet]
  (let [uuid (-> packet :uuid)
        resource-name (-> packet :nt :resource-name)]
    (if-not (get devices uuid)
      devices
      (assoc-in devices
                [uuid :services resource-name]
                {:resource-name (-> packet :nt :resource-name)
                 :version (-> packet :nt :version)
                 :location (get packet "location")}))))

(defn insert-service-or-device [devices packet]
  (cond (resource-type? "device" packet) (insert-device devices packet)
        (resource-type? "service" packet) (insert-service devices packet)
        :else devices))

(defn on-notify [devices packet]
  (let [parsed (parse-notification (:packet packet))]
    (swap! devices (fn [old] (insert-service-or-device old parsed)))))

(defn on-packet [devices packet]
  (let [parsed {:host (-> packet :sender :host)
                :port (-> packet :sender :port)
                :packet (-> (parse-packet-data (:data packet))
                            (map-keys-to-lower-case))}
        start-line (-> parsed :packet :start-line)]
    (case start-line
      "NOTIFY * HTTP/1.1" (on-notify devices parsed)
      nil)))

(defn new-socket [] (udp/socket))

(defn new-devices-map [] {})

(defn new-discovery [config]
  (let [devices (atom (new-devices-map))
        socket (-> (new-socket)
                   (udp/listen 1900)
                   (udp/on-data #(on-packet devices %))
                   (udp/join-multicast-group "239.255.255.250"
                                             (:iface config)
                                             (fn [err sock]
                                               (if err
                                                 (println "err" err)
                                                 (println "join is geil")))))]
    {:socket (atom socket)
     :device-map devices}))

(defn stop [discoverer]
  (when @(:socket discoverer)
    (udp/close @(:socket discoverer))
    (reset! (:socket discoverer) nil)))
