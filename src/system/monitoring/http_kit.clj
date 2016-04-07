(ns system.monitoring.http-kit
  (:require system.components.http-kit
            [system.monitoring.monitoring :as m])
  (:import [system.components.http_kit WebServer]))

(extend-type WebServer
  m/Monitoring
  (status [component]
    (if (:server component) :running :down)))
