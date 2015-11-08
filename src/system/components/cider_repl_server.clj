(ns system.components.cider-repl-server
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [cider.nrepl :refer (cider-nrepl-handler)]))

(defrecord CiderReplServer [port handler server]
  component/Lifecycle
  (start [component]
    (assoc component :server (start-server :port port :handler cider-nrepl-handler)))
  (stop [component]
    (when server
      (stop-server server)
      component)))

(defn new-cider-repl-server [port]
  (map->CiderReplServer {:port port}))
