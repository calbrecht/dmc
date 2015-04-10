(ns dmc.resources
  (:require [vertx.buffer :as buf]))


(def demo-packet-data-text
  "NOTIFY * HTTP/1.1\r\nHOST:239.255.255.250:1900\r\nNT:uuid:4d696e69-444c-164e-9d41-3c970ea310e5\r\nUSN:uuid:4d696e69-444c-164e-9d41-3c970ea310e5\r\nNTS:ssdp:byebye\r\n\r\n")

(def demo-packet
  {:sender {:host "172.19.0.230"
            :port 44518}
   :data (buf/as-buffer demo-packet-data-text)})

(def demo-output-packet
  {:sender {:host "172.19.0.230"
            :port 44518}
   :start-line {:method "NOTIFY"
                :path "*"
                :protocol "HTTP/1.1"}
   :header {"host" "239.255.255.250:1900"
            "nt" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5"
            "usn" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5"
            "nts" "ssdp:byebye"}})

(def demo-discovery-packet-rootdevice
  {:sender {:host "172.19.0.230"
            :port 44518}
   :start-line {:method "NOTIFY"
                :path "*"
                :protocol "HTTP/1.1"}
   :header {"host" "239.255.255.250:1900"
            "nt" "upnp:rootdevice"
            "usn" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5::upnp:rootdevice"}})

(def demo-output-discovery-packet-rootdevice
  {:sender {:host "172.19.0.230"
                :port 44518}
       :start-line {:method "NOTIFY"
                    :path "*"
                    :protocol "HTTP/1.1"}
       :type :discovery-rootdevice
       :data {:uuid "4d696e69-444c-164e-9d41-3c970ea310e5"}
       :header {"host" "239.255.255.250:1900"
                "nt" "upnp:rootdevice"
                "usn" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5::upnp:rootdevice"}})

(def demo-discovery-packet-device
  (-> demo-discovery-packet-rootdevice
      (assoc-in [:header "nt"] "uuid:4d696e69-444c-164e-9d41-3c970ea310e5")
      (assoc-in [:header "usn"] "uuid:4d696e69-444c-164e-9d41-3c970ea310e5")))

(def demo-output-discovery-packet-device
  {:sender {:host "172.19.0.230"
            :port 44518}
   :start-line {:method "NOTIFY"
                :path "*"
                :protocol "HTTP/1.1"}
   :type :discovery-device
   :data {:uuid "4d696e69-444c-164e-9d41-3c970ea310e5"}
   :header {"host" "239.255.255.250:1900"
            "nt" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5"
            "usn" "uuid:4d696e69-444c-164e-9d41-3c970ea310e5"}})

(def demo-discovery-packet-device-with-urn
  (-> demo-discovery-packet-rootdevice
      (assoc-in [:header "nt"] "urn:schemas-upnp-org:device:Basic:1")
      (assoc-in [:header "usn"] "uuid:72E383C01A19533B8A4B3559C53293FE9C4E2020D1DF::urn:schemas-upnp-org:device:Basic:1")))

(def demo-output-discovery-packet-device-with-urn
  {:sender {:host "172.19.0.230"
            :port 44518}
   :start-line {:method "NOTIFY"
                :path "*"
                :protocol "HTTP/1.1"}
   :type :discovery-device-type
   :data {:uuid "72E383C01A19533B8A4B3559C53293FE9C4E2020D1DF"
          :urn "schemas-upnp-org"
          :device "Basic"
          :version "1"}
   :header {"host" "239.255.255.250:1900"
            "nt" "urn:schemas-upnp-org:device:Basic:1"
            "usn" "uuid:72E383C01A19533B8A4B3559C53293FE9C4E2020D1DF::urn:schemas-upnp-org:device:Basic:1"}})

(def demo-discovery-packet-service
  (-> demo-discovery-packet-rootdevice
      (assoc-in [:header "nt"] "urn:schemas-upnp-org:service:ConnectionManager:1")
      (assoc-in [:header "usn"] "uuid:6c67d778-5b64-439e-afb7-62b5d1320233::urn:schemas-upnp-org:service:ConnectionManager:1")))

(def demo-output-discovery-packet-service
  {:sender {:host "172.19.0.230"
            :port 44518}
   :start-line {:method "NOTIFY"
                :path "*"
                :protocol "HTTP/1.1"}
   :type :discovery-service-type
   :data {:uuid "6c67d778-5b64-439e-afb7-62b5d1320233"
          :urn "schemas-upnp-org"
          :device "ConnectionManager"
          :version "1"}
   :header {"host" "239.255.255.250:1900"
            "nt" "urn:schemas-upnp-org:service:ConnectionManager:1"
            "usn" "uuid:6c67d778-5b64-439e-afb7-62b5d1320233::urn:schemas-upnp-org:service:ConnectionManager:1"}})

(def demo-discovery-packet-rootdevice-with-wrong-method
  (-> demo-discovery-packet-rootdevice
      (assoc-in [:start-line :method] "M-SEARCH")))
