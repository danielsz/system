(ns system.components.watcher
  (:require [com.stuartsierra.component :as component]
            [hara.io.watch :as watch]
            [system.monitoring watcher
             [core :refer [started? stopped?]]])
  (:import hara.io.watch.Watcher))

(extend-protocol component/Lifecycle
  Watcher
  (component/start [watcher]
    (if (stopped? watcher)
      (watch/start-watcher watcher)
      watcher))
  (component/stop [watcher]
    (if (started? watcher)
      (watch/stop-watcher watcher)
      watcher)))

(defn new-watcher [paths callback & [config]]
  (watch/watcher paths callback config))
