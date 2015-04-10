(ns dmc.vertx
  (:require [com.stuartsierra.component :as component]
            [vertx.embed :as vertx]))

(defrecord Vertx []
  component/Lifecycle
  (start [this]
    (let [vert (vertx/vertx)]
      (vertx/set-vertx! vert)
      (assoc this :vertx vert)))
  (stop [this]
    (do
      (.stop (:vertx this))
      (dissoc this :vertx))))

(defn new-vertx []
  (->Vertx))
