(ns system.mount.aleph
  (:require [system.mount :refer [defstate config]]
            [aleph.http :refer [start-server]]))

(defstate web-server
  :start (let [handler (get-in config [:aleph :handler])
               handler (cond-> handler (symbol? handler) (resolve handler))]
           (start-server handler (get-in config [:aleph :options])))
  :stop (.close web-server))
