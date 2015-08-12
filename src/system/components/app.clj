(ns system.components.app
  (:require
   [com.stuartsierra.component :as component]))

(defn- wrap-with-db [f db]
  (fn [req]
    (f (merge req [:db db]))))

(defn- make-handler [routes db]
  (-> routes
      (wrap-with-db db)))

(defrecord App [app routes wrap-fn init-fn db]
  component/Lifecycle
  (start [this]
    (when init-fn (init-fn db))
    (assoc this :app (wrap-fn (make-handler routes db))))
  (stop [this]
    this))

(defn new-app [routes wrap-fn init-fn]
  (map->App {:routes routes :wrap-fn wrap-fn :init-fn init-fn}))
