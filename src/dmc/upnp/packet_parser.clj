(ns dmc.upnp.packet-parser
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [schema.core :as schema]))

(defn map-keys-to-lower-case [m]
  (reduce
   (fn [agg [k v]]
     (if (string? k)
       (assoc agg (string/lower-case k) v)
       (assoc agg k v)))
   {}
   m))

(defn parse-start-line [start-line]
  (let [[_ method path protocol] (re-matches #"(\S+)\s+(\S+)\s+(\S+)" start-line)]
    {:method method
     :path path
     :protocol protocol}))

(defn parse-packet-data [packet]
  (as-> packet data
    (str data)
    (string/split data #"\r\n")
    (reduce
     (fn [agg line]
       (let [[key value] (string/split line #":" 2)]
         (if (and key value)
           (assoc-in agg [:header key] (string/trim value))
           agg)))
     {:start-line (parse-start-line (first data))
      :header {}}
     (rest data))))

(schema/defn ^:always-validate
  parse-packet
  :- {:sender {:host schema/Str
               :port (schema/either schema/Str schema/Int)}
      :header {schema/Str schema/Str}
      :start-line {:method schema/Str
                   :path schema/Str
                   :protocol schema/Str}}

  [packet :- {:sender {:host schema/Str
                       :port (schema/either schema/Str schema/Int)
                       schema/Any schema/Any}
              :data schema/Any
              schema/Any schema/Any}]

  (let [{start-line :start-line header :header} (parse-packet-data (:data packet))]
    {:sender {:host (get-in packet [:sender :host])
              :port (get-in packet [:sender :port])}
     :start-line start-line
     :header (map-keys-to-lower-case header)}))

(defn parse-packets [step]
  (fn
    ([] (step))
    ([result] (step result))
    ([result input]
     (try
       (step result (parse-packet input))
       (catch Throwable t
         (log/warn "error" t)
         (log/warn "on packet" input)
         (step result))))))
