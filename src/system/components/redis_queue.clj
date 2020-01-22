(ns system.components.redis-queue
  (:require [com.stuartsierra.component :as component]
            [taoensso.carmine.message-queue :as car-mq]))

(defrecord RedisQueue [pool spec xs]
  component/Lifecycle
  (start [component]
    (let [conn {:pool pool :spec spec}
          workers (for [x xs]
                    (car-mq/worker conn (:q x) {:handler ((:f x) component)}))]
      (doall workers)
      (assoc component :workers workers :conn conn)))
  (stop [component]
    (doseq [worker (:workers component)]
      (car-mq/stop worker))
    (assoc component :conn nil :workers nil)))

(defn new-redis-queue [& {:keys [xs pool spec] :or {pool {} spec {}}}]
  (map->RedisQueue {:xs xs :pool pool :spec spec}))

