(ns dmc.upnp.packet-pipeline
  (:require [clojure.core.async :as as]
            [clojure.tools.logging :as log]
            [dmc.upnp.discovery-parser :as discovery-parser]
            [dmc.upnp.packet-parser :as packet-parser]))

(def buffer-size 1024)

(defn annotate-raw [input]
  (when (map? input)
    (assoc input :type :raw)))

(defn wrap-nil-on-exception [f]
  (fn [& args]
    (try
      (apply f args)
      (catch Throwable t
        nil))))

(defn parse-all [step]
  (let [parsers (map wrap-nil-on-exception [discovery-parser/parse-discovery
                                            annotate-raw])]
    (fn
      ([] (step))
      ([result] (step result))
      ([result input]
       (try
         (if-let [results (->> (map #(% input) parsers)
                               (remove nil?))]
           (doseq [r results]
             (step result r))
           (step result))
         (catch Throwable t
           (log/info "error" t)
           (step result)))))))

(defn new-packet-processing-pipeline []
  (let [parsing-transducer (comp
                            packet-parser/parse-packets
                            parse-all)]
    (as/chan (as/sliding-buffer buffer-size)
             parsing-transducer)))
