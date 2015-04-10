(defproject dmc "0.1.0-SNAPSHOT"
  :description "Upnp media controller with web interface"
  :url "https://github.com/calbrecht/dmc"
  :license {:name "Eclipse Public License - v 1.0"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :main dmc.core
  :dependencies [[org.clojure/clojure "1.7.0-alpha6"]
                 [com.stuartsierra/component "0.2.3"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.logging "0.3.1"]
                 [prismatic/schema "0.4.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [clj-logging-config "1.9.12"]
                 [midje "1.6.3"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [io.vertx/lang-clojure "1.0.4"]]
  :profiles {:dev {:plugins [[lein-midje "3.1.3"]]}})
