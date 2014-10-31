(ns system.components.watcher
  (:require [com.stuartsierra.component :as component]
            [hara.io.watch :as watch])
  (:import hara.io.watch.Watcher))

(extend-protocol component/Lifecycle
  Watcher
  (component/start [watcher]
    (watch/start-watcher watcher))

  (component/stop [watcher]
    (watch/stop-watcher watcher)))

(defn new-watcher [paths callback & [config]]
  (watch/watcher paths callback config))
