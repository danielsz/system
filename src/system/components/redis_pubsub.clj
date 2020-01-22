(ns system.components.redis-pubsub
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine :as car]))

(defrecord RedisPubSub [host port topic handler options]
  component/Lifecycle
  (start [component]
    (if (:listener component)
      component
      (let [conn {:pool {} :spec {:host host :port port}}
            handler (if (:wrap-component? options)
                      (handler component)
                      handler)
            listener (car/with-new-pubsub-listener (:spec conn)
                       {topic handler}
                       (car/psubscribe topic))]
      (assoc component :listener listener))))
  (stop [component]
    (if-let [listener (:listener component)]
      (do (car/close-listener listener)
          (dissoc component :listener))
      component)))

(defn new-redis-pubsub
  ([topic handler]
   (new-redis-pubsub "127.0.0.1" 6379 topic handler {}))
  ([topic handler options]
   (new-redis-pubsub "127.0.0.1" 6379 topic handler options))
  ([host port topic handler options]
   (map->RedisPubSub {:host host :port port :topic topic :handler handler :options options})))
