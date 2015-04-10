(ns dmc.upnp.packet-parser-test
  (:require [dmc.upnp.packet-parser :as parser]
            [dmc.resources :as res]
            [midje.sweet :refer :all]))


(fact "it should parse remote host"
      (-> (parser/parse-packet res/demo-packet)
          :sender
          :host)
      =>
      "172.19.0.230")

(fact "it should parse remote port"
      (-> (parser/parse-packet res/demo-packet)
          :sender
          :port)
      =>
      44518)

(fact "it should parse the start-line"
      (:start-line (parser/parse-packet res/demo-packet))
      =>
      {:method "NOTIFY"
       :path "*"
       :protocol "HTTP/1.1"})

(fact "it should parse the start-line"
      (:start-line (parser/parse-packet res/demo-packet))
      =>
      {:method "NOTIFY"
       :path "*"
       :protocol "HTTP/1.1"})

(fact "it should parse the headers"
      (:header (parser/parse-packet res/demo-packet))
      =>
      {"host" "239.255.255.250:1900"
       "nt" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5"
       "usn" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5"
       "nts" "ssdp:byebye"})

(fact "it should parse packets"
      (into [] parser/parse-packets
            [res/demo-packet
             res/demo-packet
             nil
             {:foo "bar"}
             res/demo-packet])
      =>
      [res/demo-output-packet res/demo-output-packet res/demo-output-packet])
