(ns system.components.endpoint
  (:require [com.stuartsierra.component :as component]))

(defrecord Endpoint [routes-fn]
  component/Lifecycle
  (start [component]
    (assoc component :routes (routes-fn component)))
  (stop [component]
    (dissoc component :routes)))

(defn new-endpoint
  "Creates an endpoint. An endpoint is a closure over Compojure routes
  with the component map accessible in the enclosing scope."
  [routes-fn]
  (->Endpoint routes-fn))
