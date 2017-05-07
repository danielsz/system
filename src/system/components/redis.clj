(ns system.components.redis
  (:require [com.stuartsierra.component :as component]))

(defrecord Redis [pool spec]
  component/Lifecycle
  (start [component]
    (let [server-conn {:pool pool :spec spec}]
      (assoc component :server-conn server-conn)))
  (stop [component]
    (assoc component :server-conn nil)))

(defn new-redis [& {:keys [pool spec] :or {pool {} spec {}}}]
  (map->Redis {:pool pool :spec spec}))
