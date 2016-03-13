(ns system.monitoring.immutant-web
  (:require system.components.aleph
            [system.monitoring.monitoring :as m])
  (:import [system.components.immutant_web WebServer]))

(extend-type WebServer
  m/Monitoring
  (status [component]
    (if (:server component) :running :down)))
