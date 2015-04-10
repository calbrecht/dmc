(ns dmc.upnp.discovery-parser-test
  (:require [dmc.upnp.discovery-parser :as parser]
            [dmc.resources :as res]
            [midje.sweet :refer :all]))

(fact "it should parse rootdevice discoveries"
      (parser/parse-discovery res/demo-discovery-packet-rootdevice)
      =>
      res/demo-output-discovery-packet-rootdevice)

(fact "it should parse root device discoveries"
      (parser/parse-discovery res/demo-discovery-packet-device)
      =>
      res/demo-output-discovery-packet-device)

(fact "it should parse root device discoveries device with urn"
      (parser/parse-discovery res/demo-discovery-packet-device-with-urn)
      =>
      res/demo-output-discovery-packet-device-with-urn)


(fact "it should parse root device discoveries service"
      (parser/parse-discovery res/demo-discovery-packet-service)
      =>
      res/demo-output-discovery-packet-service)

(fact "it should work as a transducer"
      (into []
            parser/parse-discoveries
            [res/demo-discovery-packet-device
             res/demo-discovery-packet-device-with-urn
             {:foo "bar"}
             {:header {"nt" "foo"
                       "usn" "foo"}}
             res/demo-discovery-packet-rootdevice
             res/demo-discovery-packet-service])
      =>
      [res/demo-output-discovery-packet-device
       res/demo-output-discovery-packet-device-with-urn
       res/demo-output-discovery-packet-rootdevice
       res/demo-output-discovery-packet-service])

(fact "it should return nil on error"
      (parser/parse-discovery {:header {"nt" "foo"
                                        "usn" "foo"}})
      =>
      (throws Exception))

(fact "it should throw without a NOTIFY method"
      (parser/parse-discovery res/demo-discovery-packet-rootdevice-with-wrong-method)
      =>
      (throws Throwable))
