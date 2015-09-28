(ns system.components.app
  (:require
   [com.stuartsierra.component :as component]))

(defrecord App [app app-fn init-fn db]
  component/Lifecycle
  (start [this]
    (when init-fn (init-fn db))
    (assoc this :app (app-fn db)))
  (stop [this]
    this))

(defn new-app [app-fn init-fn]
  (map->App {:app-fn app-fn :init-fn init-fn}))
