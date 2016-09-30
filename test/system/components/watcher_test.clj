(ns system.components.watcher-test
  (:require [system.components.watcher :refer :all]
            (system.monitoring watcher
                               [core :refer [started? stopped?]])
            [com.stuartsierra.component :as component]            
            [clojure.test :refer [testing deftest is]]))

(deftest watcher-lifecycle
  (is [:create "Hello"]

      (let [box (atom nil)
            result (promise)
            wtch   (new-watcher ["."]
                                (fn [kind file]
                                  (component/stop @box)
                                  (deliver result [kind (slurp file)])
                                  (.delete file))
                                {:filter [".watcher"]
                                 :recursive false})
            _  (reset! box (component/start wtch))]
        (spit "test.watcher" "Hello")
        @result)))

(deftest start-watcher-idempotent
  (let [watcher (new-watcher ["."]
                             (constantly nil)
                             {})
        started (component/start watcher)]
    (is (identical? started (component/start started)))
    (component/stop watcher)))

(deftest stop-watcher-idempotent
  (let [watcher (new-watcher ["."]
                             (constantly nil)
                             {})
        started (component/start watcher)
        stopped (component/stop started)]
    (is (identical? stopped (component/stop stopped)))))

(deftest watcher-monitoring
  (let [watcher (new-watcher ["."]
                             (constantly nil)
                             {})]
    (is (started? (component/start watcher)))
    (is (stopped? (component/stop watcher)))))

