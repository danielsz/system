(ns system.components.repl-server
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]))

(defrecord ReplServer [port server]
  component/Lifecycle
  (start [component]
    (assoc component :server (start-server :port port)))
  (stop [component]
    (when server
      (stop-server server)
      (assoc component :server nil))))

(defn new-repl-server [port]
  (map->ReplServer {:port port}))
