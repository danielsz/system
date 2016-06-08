(ns system.mount.jetty
  (:require [system.mount :refer [defstate config]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defstate web-server
  :start (let [handler (get-in config [:jetty :handler])
               handler (cond-> handler (symbol? handler) (resolve handler))]
           (run-jetty handler (get-in config [:jetty :options])))
  :stop (.stop web-server))
