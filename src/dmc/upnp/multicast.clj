(ns dmc.upnp.multicast
  (:require [dmc.upnp.header :as header]))

(defn create-search [options-map]
  (header/create
   header/method-search
   {"HOST" "239.255.255.250:1900"
    "MAN" "\"ssdp:discover\""
    "MX" 2   ;Seconds to delay response
    "ST" (:search-target options-map) ;Search Target
    "USER-AGENT" "linux/100 UPnP/2.0 dmc/0.1.0-SNAPSHOT"
    "CPFN.UPNP.ORG" "dmc clojure" ;Friendly name
    "CPUUID.UPNP.ORG" "bbbbbbbb-f30b-cb3a-cce8-6c881465f3d8"}))
