(ns dmc.core
  (:require [vertx.embed :as vertx])
  (:gen-class))

(defn -main [& args]
  (vertx/set-vertx! (vertx/vertx)))
