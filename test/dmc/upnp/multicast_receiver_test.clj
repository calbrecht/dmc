(ns dmc.upnp.multicast-receiver-test
  (:require [dmc.upnp.multicast-receiver :as receiver]
            [midje.sweet :refer :all]))

(fact "it should annotate raw packets"
      (into [] receiver/raw-annotator [{} nil])
      =>
      [{:type :raw}])
