(ns dmc.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn get-config
  ([] (get-config (io/resource "settings.edn")))
  ([filename]
   (merge (edn/read-string (slurp (io/resource "settings.edn.dist")))
          (edn/read-string (slurp filename)))))
