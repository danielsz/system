(ns system.components.core-async-pubsub
  (:require [com.stuartsierra.component :as component]
            [clojure.core.async :refer [pub unsub-all close!]]))

(defrecord PubSub [channel-fn topic-fn buf-fn]
  component/Lifecycle
  (start [component]
    (let [c (channel-fn component)
          publication (if buf-fn
                        (pub c topic-fn buf-fn)
                        (pub c topic-fn))]
      (assoc component :publication publication :channel c)))
  (stop [component]
    (unsub-all (:publication component))
    (close! (:channel component))
    (dissoc component :publication :channel)))


(defn new-pubsub
  "'channel-fn` is a channel returning function. The funtion receives
  the component as argument, so that you are free to implement
  application-level logic with dependencies in scope. The function is
  responsible to create the channel and to return it. It is writing to
  it in go/thread constructs.
  
  'topic-fn` is the same as in the signature of core.async's
  'sub`.  
  
  Optional 'buf-fn` is the same as in the signature of
  core.async's 'sub`

  Please refer to the corresponding test to see a complete example."
  ([channel-fn topic-fn]
   (new-pubsub channel-fn topic-fn nil))
  ([channel-fn topic-fn buf-fn]
   (map->PubSub {:channel-fn channel-fn :topic-fn topic-fn :buf-fn buf-fn})))
