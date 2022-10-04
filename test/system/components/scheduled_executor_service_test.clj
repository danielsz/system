(ns system.components.scheduled-executor-service-test
  (:require
   [com.stuartsierra.component :as component]
   [system.components.scheduled-executor-service :refer [new-scheduler]]
   (system.monitoring scheduled-executor-service
                      [core :refer [started? stopped?]])
   [clojure.test :refer [deftest is]])
  (:import [java.util.concurrent TimeUnit ScheduledThreadPoolExecutor]))

(def workers [{:f (fn [component] #(println (str "fixed-rate " (:n-threads component)))) :initial-delay 0 :period 1 :unit TimeUnit/MINUTES :method :fixed-rate}
              {:f (fn [component] #(println (str "fixed-delay " (:n-threads component)))) :initial-delay 0 :period 1 :unit TimeUnit/MINUTES :method :fixed-delay}
              {:f (fn [component] #(println (str "one-off " (:n-threads component)))) :initial-delay 0 :unit TimeUnit/MINUTES :method :one-off}])

(def scheduler (new-scheduler :xs workers))
(def scheduler-with-n-threads (new-scheduler :xs workers :n-threads (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(deftest scheduled-executor-service-test
  (alter-var-root #'scheduler component/start)
  (is (= ScheduledThreadPoolExecutor (type (:scheduler scheduler))) "the scheduler is running")
  (alter-var-root #'scheduler component/stop)
  (is (.isShutdown (:scheduler scheduler)) "the scheduler is stopped"))

(deftest scheduled-executor-service-with-threads-test
  (alter-var-root #'scheduler-with-n-threads component/start)
  (is (= ScheduledThreadPoolExecutor (type (:scheduler scheduler-with-n-threads))) "the scheduler is running")
  (alter-var-root #'scheduler-with-n-threads component/stop)
  (is (.isShutdown (:scheduler scheduler-with-n-threads)) "the scheduler is stopped"))

(deftest scheduler-monitoring-status
  (alter-var-root #'scheduler component/start)
  (is (started? scheduler))
  (alter-var-root #'scheduler component/stop)
  (is (stopped? scheduler)))
