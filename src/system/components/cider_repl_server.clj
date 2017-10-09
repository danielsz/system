(ns system.components.cider-repl-server
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [cider.nrepl :refer [cider-nrepl-handler]]))

(defrecord CiderReplServer [port bind]
  component/Lifecycle
  (start [component]
    (assoc component :server (start-server :port port :handler cider-nrepl-handler :bind bind)))
  (stop [{server :server :as component}]
    (when server
      (stop-server server)
      component)))

(defn new-cider-repl-server
  ([port]
   (new-cider-repl-server port "localhost"))
  ([port bind]
   (map->CiderReplServer {:port port :bind bind})))
