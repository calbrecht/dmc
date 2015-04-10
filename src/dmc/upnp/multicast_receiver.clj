(ns dmc.upnp.multicast-receiver
  (:require [dmc.upnp.discovery-parser :as discovery-parser]
            [dmc.upnp.packet-parser :as packet-parser]
            [clojure.tools.logging :as log]
            [clojure.core.async :as as]
            [com.stuartsierra.component :as component]
            [vertx.datagram :as udp]))

(defn new-publication [chan]
  (as/pub chan (fn [packet] (:type packet))))

(defn new-socket
  ([] (new-socket :ipv4))
  ([family] (udp/socket family {:reuse-address true})))

(defn raw-annotator [step]
  (fn
    ([] (step))
    ([result] (step result))
    ([result input] (assoc input :type :raw))))

(defn new-parsing-channel [input-chan]
  (let [input (as/mult input-chan)
        discovery-chan (as/chan 1 discovery-parser/parse-discoveries)
        raw-chan (as/chan 1 raw-annotator)]
    (as/tap input discovery-chan)
    (as/tap input raw-chan)
    (as/merge [discovery-chan raw-chan])))

(defn new-receiver [config]
  (let [output-chan (as/chan (as/sliding-buffer 1024) packet-parser/parse-packets)
        socket (-> (new-socket)
                   (udp/listen 1900)
                   (udp/on-data (fn [data] (as/go (as/>! output-chan data))))
                   (udp/join-multicast-group
                    "239.255.255.250"
                    (:iface config)
                    (fn [err sock]
                      (if err
                        (throw (Exception. "Error joining multicast group" err))
                        (log/info "Joined multicast group")))))]
    {:socket (atom socket)
     :output-chan (new-parsing-channel output-chan)}))

(defn stop [receiver]
  (when-let [chan (:output-chan receiver)]
    (as/close! chan))
  (when @(:socket receiver)
    (udp/close @(:socket receiver))
    (reset! (:socket receiver) nil)))

(defrecord MulticastReceiver [vertx config publication receiver]
    component/Lifecycle
    (start [this]
      (let [{chan :output-chan :as receiver} (new-receiver config)
            publication (new-publication chan)]
        (assoc this
               :receiver receiver
               :publication publication)))
    (stop [this]
      (stop receiver)
      (dissoc this :receiver :publication)))

(defn new-multicast-receiver []
  (map->MulticastReceiver {}))

