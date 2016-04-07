(ns system.monitoring.quartzite
  (:require system.components.quartzite
            [system.monitoring.monitoring :as m]
            clojurewerkz.quartzite.scheduler)
  (:import [system.components.quartzite Scheduler]))

(extend-type Scheduler
  m/Monitoring
  (status [component]
    (if (clojurewerkz.quartzite.scheduler/shutdown? (:scheduler component))
      :down
      :running)))
