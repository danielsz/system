(ns system.components.sente-test
  (:require [system.components.sente :refer [new-channel-socket-server]]
            [com.stuartsierra.component :as component]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [http-kit-adapter]]
            [clojure.test :refer [deftest testing is]]))

(def channel-sockets (new-channel-socket-server (fn []) http-kit-adapter))

(deftest channel-sockets-lifecycle
  (alter-var-root #'channel-sockets component/start)
  (is (ifn? (:chsk-send! channel-sockets)))
  (is (map? @(:connected-uids channel-sockets)))
  (alter-var-root #'channel-sockets component/stop))
