(ns system.components.poco
  (:require [com.stuartsierra.component :as component]))

(defrecord Poco [xs]
  component/Lifecycle
  (start [component]
    (conj component xs))
  (stop [component]
    (assoc component :xs nil))) 

(defn new-poco [xs]
  {:pre [(map? xs)]}
  (map->Poco {:xs xs}))

