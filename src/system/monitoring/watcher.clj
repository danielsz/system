(ns system.monitoring.watcher
  (:require [system.monitoring.core :as c])
  (:import hara.io.watch.Watcher))

(extend-protocol c/Monitoring
  Watcher
  (started? [component]
    (contains? component :running))
  (stopped? [component]
    (not (contains? component :running))))
