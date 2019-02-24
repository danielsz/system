(ns system.components.scheduled-executor-service
  (:require
   [com.stuartsierra.component :as component])
  (:import [java.util.concurrent ScheduledThreadPoolExecutor]))

(defrecord Scheduler [n-threads xs]
  component/Lifecycle
  (start [component]
    (let [s (ScheduledThreadPoolExecutor. n-threads)]
      (doseq [x xs]
        (case (:method x)
          :fixed-delay (.scheduleWithFixedDelay ^ScheduledThreadPoolExecutor s ((:f x) component) (:initial-delay x) (:period x) (:unit x))
          :fixed-rate (.scheduleAtFixedRate ^ScheduledThreadPoolExecutor s ((:f x) component) (:initial-delay x) (:period x) (:unit x))))
      (assoc component :scheduler s)))
  (stop [component]
    (.shutdown (:scheduler component))
    component))

(defn new-scheduler [& {:keys [n-threads xs] :or {n-threads (.availableProcessors (Runtime/getRuntime))}}]
  (map->Scheduler {:n-threads n-threads :xs xs}))
