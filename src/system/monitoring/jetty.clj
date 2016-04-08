(ns system.monitoring.jetty
  (:require [system.monitoring.core :as c])
  (:import [system.components.jetty WebServer]))

(extend-type WebServer
  c/Monitoring
  (started? [component]
    (.isStarted (:server component)))
  (stopped? [component]
    (.isStopped (:server component))))
