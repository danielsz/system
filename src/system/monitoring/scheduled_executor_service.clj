(ns system.monitoring.scheduled-executor-service
  (:require [system.monitoring.core :as c])
  (:import [system.components.scheduled_executor_service Scheduler]))

(extend-type Scheduler
  c/Monitoring
  (started? [component]
    (not (.isShutdown (:scheduler component))))
  (stopped? [component]
    (.isShutdown (:scheduler component))))
