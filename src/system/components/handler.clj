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

(defn api-endpoints? [component]
  (contains? component :api-handler))

(defrecord Handler [default-handler options]
  component/Lifecycle
  (start [component]
    (let [options (if-let [middleware (:middleware options)]
                    (assoc options :middleware (postwalk-replace {:component component} middleware))
                    options)
          routes (map :routes (vals (endpoints component)))
          routers (apply merge-routers routes)
          handler (if (api-endpoints? component)
                    (let [site-router (ring/router (r/routes routers) {:data options})
                          api-router (for [api-handler (:api-handler component)
                                           :let [api-routes ((:api-route api-handler) component)
                                                 api-middleware (:api-middleware api-handler)
                                                 api-prefix (get api-handler :api-prefix "/api")
                                                 api-router (cond
                                                              (vector? api-routes) (ring/router (conj [api-prefix {:middleware api-middleware}] api-routes))
                                                              (satisfies? Router api-routes) api-routes)]]
                                       (r/routes api-router))
                          routers (ring/router (into (r/routes site-router) api-router))]
                      (ring/ring-handler routers (default-handler component) (dissoc options :middleware)))
                    (ring/ring-handler routers (default-handler component) options))]
      (assoc component :handler handler :debug-routes (r/routes (ring/get-router handler)) :debug-options (r/options (ring/get-router handler)))))
  (stop [component]
    (dissoc component :handler :debug-routes :debug-options)))

(defn new-handler
  [& {:keys [default-handler options]}]
  (map->Handler {:default-handler default-handler :options options}))

(defn new-api-handler
  [& {:as m}]
  (cond
    (map? m) [m]
    (vector? m) (into [] (for [{:keys [api-route api-prefix api-middleware]} m]
                           {:api-route api-route :api-prefix api-prefix :api-middleware api-middleware}))))




