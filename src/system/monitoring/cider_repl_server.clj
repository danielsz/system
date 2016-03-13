(ns system.monitoring.cider-repl-server
  (:require system.components.cider-repl-server
            [system.monitoring.monitoring :as m])
  (:import [system.components.cider_repl_server CiderReplServer]))

(extend-type CiderReplServer
  m/Monitoring
  (status [component]
    (if (:server component) :running :down)))
