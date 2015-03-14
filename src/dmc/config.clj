(ns dmc.config
  (:require [clojure.edn :as edn]))

(def settings
  (merge (edn/read-string (slurp "settings.dist.edn"))
         (edn/read-string (slurp "settings.edn"))))