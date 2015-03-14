(ns dmc.config
  (:require [clojure.edn :as edn]))

(defn get-config
  ([] (get-config "settings.edn"))
  ([filename]
   (merge (edn/read-string (slurp "settings.dist.edn"))
          (edn/read-string (slurp filename)))))
