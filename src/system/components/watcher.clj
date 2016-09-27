(ns system.components.watcher
  (:require [com.stuartsierra.component :as component]
            [hara.io.watch :as watch])
  (:import hara.io.watch.Watcher))

(extend-protocol component/Lifecycle
  Watcher
  (component/start [watcher]
    (if-not (:running watcher)
      (watch/start-watcher watcher)
      watcher))
  (component/stop [watcher]
    (if (:running watcher)
      (watch/stop-watcher watcher)
      watcher)))

(defn new-watcher [paths callback & [config]]
  (watch/watcher paths callback config))
