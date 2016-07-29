(ns system.components.hara-io-scheduler-test
  (:require
   [com.stuartsierra.component :as component]
   [hara.io.scheduler :as sch]
   [hara.io.scheduler.clock :refer [clock-started?]]
   [system.components.hara-io-scheduler :refer [new-scheduler]]
   [clojure.test :refer [deftest is]]))

(def scheduler (new-scheduler (sch/scheduler {})))

(deftest scheduled-executor-service-test
  (alter-var-root #'scheduler component/start)
  (is (= (clock-started? (get-in scheduler [:scheduler :clock])) true))
  (alter-var-root #'scheduler component/stop)
  (is (= system.components.hara_io_scheduler.Scheduler (type scheduler)))
  (is (nil? (:scheduler scheduler))))
