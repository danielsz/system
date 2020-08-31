(ns system.components.etsy
  (:require [com.stuartsierra.component :as component]
            [etsy.core :refer [make-client]]))

(defrecord Etsy [token secret throttled?]
  component/Lifecycle
  (start [component]
    (let [client (make-client token secret :throttled? throttled?)]
      (assoc component :client client)))
  (stop [component]
    (assoc component :client nil)))


(defn new-etsy-client
  [token secret & {:keys [throttled?] :or {throttled? false}}]
  (map->Etsy {:token token :secret secret :throttled? throttled?} ))
