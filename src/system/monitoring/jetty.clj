(ns system.monitoring.jetty
  (:require system.components.jetty
            [system.monitoring.monitoring :as m])
  (:import [system.components.jetty WebServer]))

(extend-type WebServer
  m/Monitoring
  (status [component]
    (if (.isStopped (:server component)) :down :running)))
