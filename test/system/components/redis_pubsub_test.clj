(ns system.components.redis-pubsub-test
  (:require [system.components.redis-pubsub :as sut]
            [taoensso.carmine :as car :refer (wcar)]
            [clojure.test :refer [deftest is]]
            [com.stuartsierra.component :as component]))

(deftest ^:dependency redis-pubsub
  (let [topic "*"
        msg (atom [])
        handler (fn [[_ _ _ message]]
                  (when message (swap! msg conj message)))
        pubsub (component/start
                (sut/new-redis-pubsub topic handler))]
    (car/wcar (:conn pubsub) (car/publish "foobar" "Hello to foobar!"))
    (is (= @msg ["Hello to foobar!"]))
    (component/stop pubsub)))
