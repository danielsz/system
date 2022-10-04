(ns system.components.poco
  (:require [com.stuartsierra.component :as component]))

(defrecord Poco [db]
  component/Lifecycle
  (start [component]
    component)
  (stop [component]
    (assoc component :db nil)))

(defn new-poco [xs]
  (map->Poco {:db xs}))

