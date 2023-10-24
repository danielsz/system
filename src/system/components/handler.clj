(ns system.components.handler
  (:require [com.stuartsierra.component :as component]
            [reitit.core :as r :refer [Router]]
            [reitit.ring :as ring]
            [clojure.walk :refer [postwalk-replace]]))

(defn merge-routers [& routers]
  (ring/router
    (apply merge (map r/routes routers))
    (apply merge (map r/options routers))))

(defn endpoints
  "Find all endpoints this component depends on, returns map entries of the form
  [name component]. An endpoint is a component that define a `:routes` key."
  [component]
  (filter (comp :routes val) component))

(defn api-endpoint? [component]
  (contains? component :api-handler))

(defrecord Handler [default-handler options]
  component/Lifecycle
  (start [component]
    (let [options (if-let [middleware (:middleware options)]
                    (assoc options :middleware (postwalk-replace {:component component} middleware))
                    options)
          routes (map :routes (vals (endpoints component)))
          routers (apply merge-routers routes)
          handler (if (api-endpoint? component)
                    (let [site-router (ring/router (r/routes routers) {:data options})
                          api-handler (:api-handler component)
                          api-routes (:api-route api-handler)
                          api-middleware (:api-middleware api-handler)
                          api-prefix (get api-handler :api-prefix "/api")
                          api-router (cond
                                       (vector? api-routes) (ring/router (conj [api-prefix {:middleware api-middleware}] api-routes))
                                       (satisfies? Router api-routes) api-routes)
                          routers (ring/router (into (r/routes site-router) (r/routes api-router)))]
                      (ring/ring-handler routers (default-handler component) (dissoc options :middleware)))
                    (ring/ring-handler routers (default-handler component) options))]
      (assoc component :handler handler :debug-routes (r/routes (ring/get-router handler)) :debug-options (r/options (ring/get-router handler)))))
  (stop [component]
    (dissoc component :handler :debug-routes :debug-options)))

(defn new-handler
  [& {:keys [default-handler options]}]
  (map->Handler {:default-handler default-handler :options options}))

(defrecord APIHandler [api-route api-prefix api-middleware]
  component/Lifecycle
  (start [component]
    (assoc component :api-route (api-route component)))
  (stop [component]
    (dissoc component :api-route :api-prefix :api-middleware)))

(defn new-api-handler
  [& {:keys [api-route api-prefix api-middleware]}]
  (map->APIHandler {:api-route api-route :api-prefix api-prefix :api-middleware api-middleware}))
