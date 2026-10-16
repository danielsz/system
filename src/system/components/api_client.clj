(ns system.components.api-client
  (:require [com.stuartsierra.component :as component]))

(defrecord APIClient [client]
  component/Lifecycle
  (start [component]
    (assoc component :client (client component)))
  (stop [component]
    (dissoc component :client)))

(defn new-api-client [& {:keys [client]}]
  (map->APIClient {:client client}))
