(ns system.components.carmine-test
  (:require [system.components.carmine :as sut]
            [taoensso.carmine :as car :refer (wcar)]
            [clojure.test :refer [deftest is]]
            [com.stuartsierra.component :as component]))

(deftest ^:dependency pubsub
  (let [topic "*"
        msg (atom [])
        handler (fn [[_ _ _ message]]
                  (when message (swap! msg conj message)))
        pubsub (component/start
                (sut/new-pubsub topic handler))]
    (car/wcar (:conn pubsub) (car/publish "foobar" "Hello to foobar!"))
    (is (= @msg ["Hello to foobar!"]))
    (component/stop pubsub)))
