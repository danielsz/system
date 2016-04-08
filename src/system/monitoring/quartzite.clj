(ns system.monitoring.quartzite
  (:require [system.monitoring.core :as c]
            [clojurewerkz.quartzite.scheduler :as s])
  (:import [system.components.quartzite Scheduler]))

(extend-type Scheduler
  c/Monitoring
  (started? [component]
    (s/started? (:scheduler component)))
  (stopped? [component]
    (s/shutdown? (:scheduler component))))
