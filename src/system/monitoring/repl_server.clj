(ns system.monitoring.repl-server
  (:require system.components.repl-server
            [system.monitoring.monitoring :as m])
  (:import [system.components.repl_server ReplServer]))

(extend-type ReplServer
  m/Monitoring
  (status [component]
    (if (:server component) :running :down)))
