(ns system.components.durable-queue
  (:require [com.stuartsierra.component :as component]
            [durable-queue :as q]))

(defrecord DurableQueue [path opts]
  component/Lifecycle
  (start [component]
    (assoc component :queue (q/queues path opts)))
  (stop [component]
    (dissoc component :queue)))

(defn new-durable-queue
  "`path' is a directory in the filesystem.

  `opts' is the options map supported by durable queues, as documented at
  https://github.com/Factual/durable-queue#configuring-the-queues"
  ([path]
   (new-durable-queue path {}))
  ([path opts]
   (map->DurableQueue {:path path :opts opts})))
