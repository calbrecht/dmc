(ns dmc.upnp
  (:require [vertx.embed :as vertx]
            [vertx.datagram :as udp]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]))

(defn init []
  (vertx/set-vertx! (vertx/vertx)))

(defonce socket (atom nil))

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

(defn on-notify [packet]
  (pprint {:msg "On Notify"
           :packet packet}))

(defn on-packet [packet]
  (let [parsed {:host (-> packet :sender :host)
                :port (-> packet :sender :port)
                :packet (parse-packet-data (:data packet))}
        start-line (-> parsed :packet :start-line)]
    (case start-line
      "NOTIFY * HTTP/1.1" (on-notify parsed)
      nil)))

(defn stop []
  (when @socket
    (udp/close @socket)))
(defn restart []
  (stop)
  (reset! socket (udp/socket))
  (-> @socket
      (udp/listen 1900)
      (udp/on-data #(on-packet %))
      (udp/join-multicast-group "239.255.255.250"
                                "enp0s25"
                                (fn [err sock]
                                  (if err
                                    (println "err" err)
                                    (println "join is geil"))))))
