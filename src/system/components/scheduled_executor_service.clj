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
          :fixed-rate (.scheduleAtFixedRate ^ScheduledThreadPoolExecutor s ((:f x) component) (:initial-delay x) (:period x) (:unit x))
          :one-off (.schedule ^ScheduledThreadPoolExecutor s ((:f x) (assoc component :s s)) (:initial-delay x) (:unit x))))
      (assoc component :scheduler s)))
  (stop [component]
    (when-let [scheduler (:scheduler component)]
      (.shutdown scheduler))
    component))

(defn new-scheduler [& {:keys [n-threads xs] :or {n-threads (.availableProcessors (Runtime/getRuntime))}}]
  (map->Scheduler {:n-threads n-threads :xs xs}))
