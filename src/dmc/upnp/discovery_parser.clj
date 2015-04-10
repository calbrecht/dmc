(ns dmc.upnp.discovery-parser
  (:require [clojure.string :as string]
            [schema.core :as schema]
            [clojure.core.match :refer [match]]
            [clojure.tools.logging :as log]))

(defn parse-urn [data]
  (let [[_ domain resource-type resource-name version] (string/split data #":")]
    {:type :urn
     :domain domain
     :resource-type resource-type
     :resource-name resource-name
     :version version}))

(schema/defn ^:always-validate parse-nt [nt :- schema/Str]
  (if (= nt "upnp:rootdevice")
    {:type :rootdevice}
    (let [[type type-rest] (string/split nt #":" 2)]
      (case type
            "urn" (parse-urn nt)
            "uuid" {:type :uuid}
            nil))))

(schema/defn ^:always-validate parse-usn [usn :- schema/Str]
  (let [[_ uuid _ r] (string/split usn #":" 4)]
    (if-not r
      {:type :uuid
       :uuid uuid}
      (merge {:uuid uuid}
             (parse-urn r)))))

(def schema-packet-base {:start-line {:method (schema/eq "NOTIFY")
                                      schema/Any schema/Any}
                         :header {(schema/required-key "nt") schema/Str
                                  (schema/required-key "usn") schema/Str
                                  schema/Str schema/Str}
                         schema/Any schema/Any})

(def schema-output-without-urn (merge schema-packet-base {:type (schema/enum :discovery-rootdevice
                                                                             :discovery-device)
                                                          :data {:uuid schema/Str}}))
(def schema-output-with-urn (merge schema-packet-base {:type (schema/enum :discovery-device-type
                                                                          :discovery-service-type)
                                                       :data {:uuid schema/Str
                                                              :urn schema/Str
                                                              :device schema/Str
                                                              :version schema/Str}}))
(def schema-output (schema/either schema-output-with-urn
                                  schema-output-without-urn))

(schema/defn ^:always-validate parse-discovery
  :- schema-output
  [packet :- schema-packet-base]
  (let [nt (parse-nt (get-in packet [:header "nt"]))
        usn (parse-usn (get-in packet [:header "usn"]))]
    (match [(:type nt) (:resource-type usn)]
           [:rootdevice _] (merge packet {:type :discovery-rootdevice
                                          :data {:uuid (:uuid usn)}})
           [:uuid _]  (merge packet {:type :discovery-device
                                     :data {:uuid (:uuid usn)}})
           [:urn "device"] (merge packet {:type :discovery-device-type
                                          :data {:uuid (:uuid usn)
                                                 :urn (:domain usn)
                                                 :device (:resource-name usn)
                                                 :version (:version usn)}})
           [:urn "service"] (merge packet {:type :discovery-service-type
                                           :data {:uuid (:uuid usn)
                                                  :urn (:domain usn)
                                                  :device (:resource-name usn)
                                                  :version (:version usn)}}))))

(def packet-filter (filter #(= "NOTIFY" (get-in % [:start-line :method]))))

(defn parse-discoveries-t [step]
  (fn
    ([] (step))
    ([result] (step result))
    ([result input]
     (try
       (step result (parse-discovery input))
       (catch Throwable t
         (log/warn "error" t)
         (log/warn "on packet" input)
         (step result))))))

(def parse-discoveries (comp packet-filter
                             parse-discoveries-t))
