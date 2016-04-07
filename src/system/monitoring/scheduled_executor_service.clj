(ns system.monitoring.scheduled-executor-service
  (:require system.components.scheduled-executor-service
            [system.monitoring.monitoring :as m])
  (:import [system.components.scheduled_executor_service Scheduler]))

(extend-type Scheduler
  m/Monitoring
  (status [component]
    (if (.isShutdown (:scheduler component)) :down :running)))
