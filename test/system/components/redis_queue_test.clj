(ns system.components.redis-queue-test
  (:require [system.components.redis-queue :as sut]
            [com.stuartsierra.component :as component]
            [taoensso.carmine :as car]
            [taoensso.carmine.message-queue :as car-mq]
            [clojure.test :refer [deftest is testing]]))

(def counter (atom 0))

(deftest ^:dependency redis-queue
  (testing "Testing the core functionality"
    (let [queue (component/start (sut/new-redis-queue :xs [{:f (fn [component]
                                                                 (fn [{:keys [message attempt]}]
                                                                   (is (= (:iter message) @counter))
                                                                   (swap! counter inc)
                                                                   (println "Received" (:event message))
                                                                   {:status :success}))
                                                            :q :testing}]))]
      (dotimes [n 10] (car/wcar (:conn queue) (car-mq/enqueue :testing {:event (name (gensym)) :iter n})))
      (Thread/sleep 30000)
      (is (= 10 @counter))
      (component/stop queue))))
