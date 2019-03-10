(ns system.components.durable-queue
  (:require [com.stuartsierra.component :as component]
            [durable-queue :as q]))

(defrecord DurableQueue [path opts xs]
  component/Lifecycle
  (start [component]
    (let [queues (q/queues path opts)
          guard (volatile! true)]
      (doseq [x xs]
        (.start (Thread. (fn [] (while @guard
                                 ((:f x) queues component))))))
      (assoc component :queue queues :guard guard)))
  (stop [component]
    (vswap! (:guard component) not)
    (dissoc component :queue :guard)))

(defn new-durable-queue
  "`path' is a directory in the filesystem.
  `opts' is the options map supported by durable queues, as documented at
  https://github.com/Factual/durable-queue#configuring-the-queues"
  [path & {:keys [opts xs] :or {opts {}}}]
  (map->DurableQueue {:path path :opts opts :xs xs}))

