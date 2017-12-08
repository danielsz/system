(ns system.components.riemann-client
  (:require [com.stuartsierra.component :as component]
            [riemann.client :as r]
            [clojure.tools.logging :as log])
  (:import [io.riemann.riemann.client OverloadedException]))

(defrecord RiemannClient [host port transport]
  component/Lifecycle
  (start [component]
    (let [client (case transport
                     :tcp (r/tcp-client {:host host :port port})
                     :udp (r/udp-client {:host host :port port}))
          a (-> (agent {})
                (add-watch :key (fn [_k _r _os ns] (try (deref (:promise ns) 5000 ::timeout)
                                                       (catch Exception e (log/error (.getMessage e)))))))
          f (fn [state client event]
              (let [v (try
                        (r/send-event client event)
                        (catch OverloadedException e (log/error (.getMessage e))))]
                (assoc state :promise v)))]
      (assoc component :client client :send-fn (partial send a f client))))
  (stop [component]
    (if-let [client (:client component)]
      (assoc component :client (r/close! client))
      component)))

(defn new-riemann-client
  "Returns a Riemann client.

  `send-fn` is a function that accepts a Riemann struct, which it will
  send in an agent threadpool (asynchronously).  The promise that the
  Riemann `send-event` returns will be derefed in a watcher (also on
  the threadpool), and will log errors if any occur." 
  [& {:keys [host port transport] :or {host "127.0.0.1" port 5555 transport :tcp}}]
  (map->RiemannClient {:host host :port port :transport transport}))
