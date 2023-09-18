(ns system.components.endpoint
  (:require [com.stuartsierra.component :as component]
            [reitit.ring :as ring]
            [reitit.core :refer [Router]]))

(defrecord Endpoint [routes middleware]
  component/Lifecycle
  (start [component]
    (assoc component :routes (cond
                               (vector? routes) (if (not-empty middleware)
                                                  (ring/router routes {:data {:middleware middleware}})
                                                  (ring/router routes))
                               (and (ifn? routes) (satisfies? Router (routes component))) (routes component))))
  (stop [component]
    (dissoc component :routes)))

(defn new-endpoint
  "Creates an endpoint. If argument is a vector of route data, will
  create reitit routes with optional middleware. If argument is a
  function, it will check for presence of Reitit route protocol and
  assume that the endpoint is a closure over Reitit routes with
  component dependencies in scope."
  [& {:keys [routes middleware]}]
  (map->Endpoint {:routes routes :middleware middleware}))
