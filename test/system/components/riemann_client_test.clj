(ns system.components.riemann-client-test
  (:require
   [com.stuartsierra.component :as component]
   [system.components.riemann-client :refer [new-riemann-client]]
   (system.monitoring riemann-client
                      [core :refer [started? stopped?]])
   [clojure.test :refer [testing deftest is]]))


(deftest ^:dependency riemann-client
  (let [client (component/start (new-riemann-client))]
    (is (started? client))
    (is (not (stopped? client)))
    (component/stop client)))

