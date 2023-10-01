(ns system.components.handler
  (:require [com.stuartsierra.component :as component]
            [reitit.core :as r]
            [reitit.ring :as ring]))

(defn merge-routers [& routers]
  (ring/router
    (apply merge (map r/routes routers))
    (apply merge (map r/options routers))))

(defn endpoints
  "Find all endpoints this component depends on, returns map entries of the form
  [name component]. An endpoint is a component that define a `:routes` key."
  [component]
  (filter (comp :routes val) component))

(defrecord Handler [default-handler options]
  component/Lifecycle
  (start [component]
    (let [routes (map :routes (vals (endpoints component)))
          routers (apply merge-routers routes)
          ;; When assembling the handler, find the APIHandler and append it to routers. (merge-routers routers (:routes APIhandler))
          handler (ring/ring-handler routers (default-handler component) options)]
      (assoc component :handler handler :debug (r/routes routers))))
  (stop [component]
    (dissoc component :handler :debug)))

(defn new-handler
  [& {:keys [default-handler options]}]
  (map->Handler {:default-handler default-handler :options options}))


(defrecord APIHandler [routes]
  component/Lifecycle
  (start [component]
    (assoc component :routes routes))
  (stop [component]
    (dissoc component :routes)))

(defn new-api-handler
  [& {:keys [routes]}]
  (map->APIHandler {:routes routes}))


;; Idea: When assembling the handler, find the APIHandler and append its routes to the existing session handler.
