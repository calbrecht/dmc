(ns dmc.upnp.header)

(def method-search "M-SEARCH * HTTP/1.1")

(defn add-eol [line]
  (str line "\r\n"))

(defn str-method [method]
  (add-eol (str method)))

(defn append-header-line [header [k v]]
  (add-eol (str header (format "%s: %s" k v))))

(defn create [method header-map]
  (-> (reduce
        append-header-line
        (str-method method) ;initial message is just the start line
        header-map)
      (add-eol))) ; add end of line for one blank line
