(ns system.components.durable-queue-test
  (:require [system.components.durable-queue :refer [new-durable-queue]]
            [com.stuartsierra.component :as component]
            [durable-queue :as q]
            [clojure.test :refer [deftest is]]))

(def dq (component/start (new-durable-queue "/tmp")))

(deftest durable-queue
  (let [queue (:queue dq)]
    (q/put! queue :foo "a task")
    (is (= @(q/take! queue :foo) "a task"))))
