(ns system.components.scheduled-executor-service-test
  (:require
   [system.components.scheduled-executor-service :refer [new-scheduler]]
   system.monitoring.scheduled-executor-service
   [com.stuartsierra.component :as component]
   [system.monitoring.monitoring :as monitoring]
   [clojure.test :refer [deftest is]]))

(def scheduler (new-scheduler (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(deftest scheduled-executor-service-test
  (alter-var-root #'scheduler component/start)
  (is (= java.util.concurrent.ScheduledThreadPoolExecutor (type (:scheduler scheduler))) "the scheduler is running")
  (alter-var-root #'scheduler component/stop)
  (is (.isShutdown (:scheduler scheduler)) "the scheduler is stopped"))

(deftest scheduler-monitoring-status
  (alter-var-root #'scheduler component/start)
  (is (= (monitoring/status scheduler) :running))
  (alter-var-root #'scheduler component/stop)
  (is (= (monitoring/status scheduler) :down)))
