(ns system.mount.http-kit
  (:require [system.mount :refer [defstate config]]
            [org.httpkit.server :refer [run-server]]))

(defstate web-server
  :start (let [handler (get-in config [:http-kit :handler])
               handler (cond-> handler (symbol? handler) (resolve handler))]
           (run-server handler (get-in config [:http-kit :options])))
  :stop (web-server))
