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
    (component/stop client)
    (is (stopped? client))))

(deftest ^:dependency riemann-send-fn
  (let [client (component/start (new-riemann-client))
        send-fn (:send-fn client)]
    (Thread/sleep 1000)
    (is (started? client))
    (is (= clojure.lang.Agent (type (send-fn {:service "foo" :state "ok"}))))
    (Thread/sleep 1000)
    (is (= io.riemann.riemann.client.MapPromise (type (:promise @(send-fn {:service "foo" :state "ok"})))))
    (is (= riemann.codec.Msg (type (deref (:promise @(send-fn {:service "foo" :state "ok"}))))))
    (Thread/sleep 1000)
    (component/stop client)))
