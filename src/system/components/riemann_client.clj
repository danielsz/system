(ns system.components.riemann-client
  (:require [com.stuartsierra.component :as component]
            [riemann.client :as r]))

(defrecord RiemannClient [host port transport]
  component/Lifecycle
  (start [component]
    (let [client (case transport
                     :tcp (r/tcp-client {:host host :port port})
                     :udp (r/udp-client {:host host :port port}))]
      (assoc component :client client)))
  (stop [component]
    (if-let [client (:client component)]
      (assoc component :client (r/close! client))
      component)))

(defn new-riemann-client [& {:keys [host port transport] :or {host "127.0.0.1" port 5555 transport :tcp}}]
  (map->RiemannClient {:host host :port port :transport transport}))
