(ns system.components.repl-server
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]))

(defrecord ReplServer [server port bind]
  component/Lifecycle
  (start [component]
    (assoc component :server (start-server :port port :bind bind)))
  (stop [component]
    (when server
      (stop-server server)
      component)))

(defn new-repl-server
  ([port]
   (new-repl-server port "localhost"))
  ([port bind]
  (map->ReplServer {:port port :bind bind}) ))

