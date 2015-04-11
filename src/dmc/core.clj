(ns dmc.core
  (:require [com.stuartsierra.component :as component]
            [clojure.repl :as repl]
            [dmc.upnp.multicast-receiver]
            [clojure.core.async :as as]
            [dmc.vertx]
            [dmc.config :as config]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log]
            [clj-logging-config.log4j :as log4j])
  (:gen-class))

(log4j/set-logger!)

(defn new-system [config]
  (component/system-map
   :config config
   :vertx (dmc.vertx/new-vertx)
   :multicast-receiver (component/using
                        (dmc.upnp.multicast-receiver/new-multicast-receiver)
                        [:vertx :config])))

(defn try-and-print [f & args]
  (try (apply f args)
       (catch Throwable t
         (repl/pst t))))

(defn start-system [system]
  (component/start-system system))

(defn -main [& args]
  (let [system (->> (config/get-config)
                    (try-and-print new-system)
                    (try-and-print start-system))
        chan (as/chan)
        publication (get-in system [:multicast-receiver :publication])]
    (doseq [topic [:discovery-rootdevice
                   :discovery-device-type
                   :discovery-device
                   :discovery-service-type]]
      (log/info "subscribe to" topic)
      (as/sub publication topic chan))
    (loop []
      (let [packet (as/<!! chan)]
        (when packet
          (pprint packet)
          (recur))))))

