(ns system.monitoring.mongo
  (:require system.components.mongo
            [system.monitoring.monitoring :as m])
  (:import [system.components.mongo Mongo]))

(extend-type Mongo
  m/Monitoring
  (status [component]
    (if (and (:db component) (:conn component)) :running :down)))
