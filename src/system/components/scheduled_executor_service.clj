(ns system.components.scheduled-executor-service
  (:require
   [com.stuartsierra.component :as component])
  (:import [java.util.concurrent ScheduledThreadPoolExecutor]))


(defrecord Scheduler [scheduler n-threads]
  component/Lifecycle
  (start [component]
    (let [s (ScheduledThreadPoolExecutor. n-threads)]
      (assoc component :scheduler s)))
  (stop [component]
    (.shutdown scheduler)
    component))
  
(defn new-scheduler
  ([]
   (new-scheduler (.availableProcessors (Runtime/getRuntime))))
  ([n-threads]
   (map->Scheduler {:n-threads n-threads})))
