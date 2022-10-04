(ns system.components.durable-queue-test
  (:require [system.components.durable-queue :refer [new-durable-queue]]
            [com.stuartsierra.component :as component]
            [durable-queue :as q]
            [clojure.test :refer [deftest is]]))

(deftest durable-queue
  (let [dq (component/start (new-durable-queue "/tmp"))
        queue (:queue dq)]
    (q/put! queue :foo "a task")
    (is (= @(q/take! queue :foo) "a task"))
    (component/stop dq)))

(deftest durable-queue-with-workers
  (let [counter (atom 0)
        dq (component/start (new-durable-queue "/tmp" :xs [{:f (fn [queue component]
                                                                 (let [v (q/take! queue :foo)]
                                                                   (swap! counter inc)
                                                                   (println @v)
                                                                   (q/complete! v)))}]))
        queue (:queue dq)]
    (q/put! queue :foo "a task")
    (is (= @counter 1))
    (component/stop dq)))
