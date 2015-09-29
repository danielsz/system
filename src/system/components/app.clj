(ns system.components.app
  (:require
   [com.stuartsierra.component :as component]))

(defrecord App [app app-fn init-fn destroy-fn db]
  component/Lifecycle
  (start [this]
    (when init-fn (init-fn db))
    (assoc this :app (app-fn db)))
  (stop [this]
    (when destroy-fn (destroy-fn db))
    this))

(defn new-app
  ([app-fn]
    (map->App {:app-fn app-fn}))
  ([app-fn init-fn]
    (map->App {:app-fn app-fn :init-fn init-fn}))
  ([app-fn init-fn destroy-fn]
    (map->App {:app-fn app-fn :init-fn init-fn :destroy-fn destroy-fn})))
