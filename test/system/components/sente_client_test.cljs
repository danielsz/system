(ns system.components.sente-client-test
  (:require [system.components.sente :refer [new-channel-socket-client]]
            [com.stuartsierra.component :as component]
            [taoensso.sente :as sente]
            [cljs.test :refer-macros [deftest testing is]]))

(def channel-sockets (atom (new-channel-socket-client (fn []) "/chsk")))

(deftest channel-sockets-lifecycle
  (swap! channel-sockets component/start)
  (is (ifn? (:chsk-send! @channel-sockets)))
  (is (not (nil? (:chsk @channel-sockets))))
  (is (not (nil? (:ch-chsk @channel-sockets))))
  (is (not (nil? (:chsk-state @channel-sockets))))
  (swap! channel-sockets component/stop))

