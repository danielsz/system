(ns system.mount.immutant-web
  (:require [system.mount :refer [defstate config]]
            [immutant.web :refer [run stop]]))

(defstate web-server
  :start (let [handler (get-in config [:immutant-web :handler])
               handler (cond-> handler (symbol? handler) (resolve handler))]
           (run handler (get-in config [:immutant-web :options])))
  :stop (stop web-server))
