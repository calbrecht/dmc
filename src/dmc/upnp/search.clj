(ns dmc.upnp.search
  (:require [vertx.datagram :as udp])

(defn add-header-line [header [k v]]
  (str header (format "%s: %s\r\n" k v)))

(defn new-header [start-line header-map]
  (-> (reduce
       add-header-line
       (str start-line "\r\n") ;initial message is just the start line
       header-map)
      (str "\r\n"))) ; add new line at the end

(defn new-search [what]
  (new-header
   "M-SEARCH * HTTP/1.1"
   {"HOST" "239.255.255.250:1900"
    "MAN" "ssdp:discover"
    "MX" 10   ;Seconds to delay response
    "ST" what ;Search Target
    "USER-AGENT" "linux/100 UPnP/2.0 dmc/0.1.0-SNAPSHOT"
    "CPFN.UPNP.ORG" "dmc clojure" ;Friendly name
    "CPUUID.UPNP.ORG" "bbbbbbbb-f30b-cb3a-cce8-6c881465f3d8"}))

(defn search
  ([discoverer] (search discoverer "ssdp:all"))
  ([discoverer what]
   (udp/send
    @(:socket discoverer)
    (new-search what)
    "239.255.255.250"
    1900)))
