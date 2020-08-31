(ns system.components.etsy
  (:require [com.stuartsierra.component :as component]
            [etsy.core :refer [make-client]]))

(defrecord Etsy [token secret throttle-rate]
  component/Lifecycle
  (start [component]
    (let [client (if throttle-rate
                   (make-client token secret throttle-rate)
                   (make-client token secret))]
      (assoc component :client client)))
  (stop [component]
    (assoc component :client nil)))


(defn new-etsy-client
  [token secret & {:keys [throttle-rate]}]
  (map->Etsy {:token token :secret secret :throttle-rate throttle-rate} ))
