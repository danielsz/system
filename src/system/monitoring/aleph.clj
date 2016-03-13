(ns system.monitoring.aleph
  (:require system.components.aleph
            [system.monitoring.monitoring :as m])
  (:import [system.components.aleph WebServer]))

(extend-type WebServer
  m/Monitoring
  (status [component]
    (if (:server component) :running :down)))
