(ns system.components.core-async-pipe
  (:require [clojure.core.async :as async]
            [com.stuartsierra.component :as component]))


(defn worker
  [f to]
  (async/thread
    (loop []
      (let [v (async/<!! to)]
        (async/<!! (async/timeout 1000))
        (try
          (f v)
          (catch Throwable t
            (println t))))
      (recur))))

(defrecord Pipe [handler options]
    component/Lifecycle
    (start [component]
      (let [from (if (contains? options :from)
                     (:from options)
                     (async/chan))            
            to (if (contains? options :to)
                     (:to options)
                     (async/chan (async/dropping-buffer 10)))          
            worker (if (contains? options :worker)
                     (:worker options)
                     worker)
            f (if (:wrap-component? options)
                (handler component)
                handler)]
        (assoc component :from from :to to :pipe (async/pipe from to) :worker (worker f to))))
    (stop [component]
      (async/close! (:from component))
      (async/close! (:to component))
      (async/close! (:pipe component))
      (async/close! (:worker component))
      (dissoc component :from :to :pipe :worker)))

(defn new-pipe
  "The default implementation is geared towards throttling the
  throughput of a stream. Provide your own implementation along the
  options map."
  ([handler]
   (new-pipe handler {}))
  ([handler options]
   (map->Pipe {:handler handler :options options})))

