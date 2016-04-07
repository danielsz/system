(ns system.monitoring.datomic
  (:require system.components.datomic
            [system.monitoring.monitoring :as m])
  (:import [system.components.datomic Datomic]))

(extend-type Datomic
  m/Monitoring
  (status [component]
    (if (:conn component) :running :down)))
