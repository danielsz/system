(ns system.components.etsy
  (:require [com.stuartsierra.component :as component]
            [etsy.core :refer [make-client]]))

(defrecord Etsy [token secret client]
  component/Lifecycle
  (start [component]
    (let [client (make-client token secret)]
      (assoc component :client client)))
  (stop [component]
    (assoc component :client nil)))


(defn new-etsy-client
  [token secret]
  (map->Etsy {:token token :secret secret}))
