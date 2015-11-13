(ns system.components.scheduled-executor-service-test
  (:require [clojure.test :refer [deftest is]]
            [com.stuartsierra.component :as component]
            [system.components.scheduled-executor-service :refer [new-scheduler]]))

(def scheduler (new-scheduler (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(deftest scheduled-executor-service-test
  (alter-var-root #'scheduler component/start)
  (is (= java.util.concurrent.ScheduledThreadPoolExecutor (type (:scheduler scheduler))) "the scheduler is running")
  (alter-var-root #'scheduler component/stop)
  (is (.isShutdown (:scheduler scheduler)) "the scheduler is stopped"))
