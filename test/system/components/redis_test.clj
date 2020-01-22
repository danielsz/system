(ns system.components.redis-test
  (:require [system.components.redis :as r]
            [com.stuartsierra.component :as component]
            [taoensso.carmine :as car :refer [wcar]]
            [clojure.test :refer [deftest is testing]]))

(deftest ^:dependency redis
  (testing "Test the core API"
    (let [db (component/start (r/new-redis))
          conn (:server-conn db)
          k (name (gensym))]
      (is (= 1 (wcar conn (car/sadd k "bar"))))
      (is (= 0 (wcar conn (car/sadd k "bar"))))
      (wcar conn (car/srem k "bar"))
      (component/stop db))))
