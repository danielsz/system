(ns system.monitoring.sente
  (:require system.components.sente
            [system.monitoring.monitoring :as m])
  (:import [system.components.sente ChannelSocketServer]))

(extend-type ChannelSocketServer
  m/Monitoring
  (status [component]
    (if (:router component) :running :down)))
