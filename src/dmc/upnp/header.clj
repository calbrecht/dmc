(ns dmc.upnp.header)

(defn add-eol [line]
  (str line "\r\n"))

(defn str-start-line [start-line]
  (add-eol (str start-line)))

(defn append-header-line [header [k v]]
  (add-eol (str header (format "%s: %s" k v))))

(defn create [start-line header-map]
  (-> (reduce
        append-header-line
        (str-start-line start-line) ;initial message is just the start line
        header-map)
      (add-eol))) ; add end of line for one blank line

(defn create-search [options-map]
  (create
   "M-SEARCH * HTTP/1.1"
   {"HOST" "239.255.255.250:1900"
    "MAN" "\"ssdp:discover\""
    "MX" 2   ;Seconds to delay response
    "ST" (:search-target options-map) ;Search Target
    "USER-AGENT" "linux/100 UPnP/2.0 dmc/0.1.0-SNAPSHOT"
    "CPFN.UPNP.ORG" "dmc clojure" ;Friendly name
    "CPUUID.UPNP.ORG" "bbbbbbbb-f30b-cb3a-cce8-6c881465f3d8"}))
