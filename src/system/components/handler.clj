(ns system.components.handler
  (:require [com.stuartsierra.component :as component]
            [lang-utils.core :refer [contains+? ∘]]
            [compojure.core :as compojure]))

(defn- endpoints
  "Find all endpoints this component depends on, returns map entries of the form
  [name component]. An endpoint is a component that define a `:routes` key."
  [component]
  (filter (∘ :routes val) component))

(defn- with-middleware
  "Returns all endpoints that include middleware. With `flag` being false:
  returns all enpoints that *don't* include middleware. Works on [name
  component] map entry pairs."
  ([endpoints]
   (with-middleware endpoints true))
  ([endpoints flag]
   (let [f (if flag
             (fn [[k v]] (contains+? v :middleware))
             (fn [[k v]] (not (contains+? v :middleware))))]
     (filter f endpoints))))

(defn- middleware-key
  "Given the endpoint map-entry this returns the key of the
  middleware dependency if any, or an empty map."
 [endpoint]
  (reduce-kv (fn [_ k v] (if (contains+? v :middleware) (reduced k) _)) {} (val endpoint)))

(defrecord Handler []
  component/Lifecycle
  (start [component]
    (let [endpoints-with-middleware (partition-by middleware-key ((∘ with-middleware endpoints) component))
          handlers (for [endpoints endpoints-with-middleware
                         :let [mw-key (middleware-key (first endpoints))
                               wrap-mw (get-in (val (first endpoints)) [mw-key :wrap-mw])
                               routes (keep :routes (vals endpoints))]]
                     (wrap-mw (apply compojure/routes routes)))
          routes (keep :routes (vals (-> component
                                         endpoints
                                         (with-middleware false))))
          wrap-mw (get-in component [:middleware :wrap-mw] identity)
          handler (wrap-mw (apply compojure/routes (concat routes handlers)))]
      (assoc component :handler handler)))
  (stop [component]
    (dissoc component :handler)))

(defn new-handler
  "Creates a handler component. A handler component combines endpoints and
  middleware into a ring handler function.

  Endpoints should be added as dependencies using `component/using`, the names
  used for endpoints don't matter, they are recognized by their structure.

  Middleware can be added by depending on a middleware component, either
  per-endpoint, or once for the complete handler. Per-endpoint middleware can
  use any name, middleware on the handler must be called `:middleware`.

  The resulting ring handler function is available as `:handler` on the handler
  component.

  Example:

      (component/system-map
         :endpoint-a (new-endpoint some-routes)
         :endpoint-b (-> (new-endpoint other-routes)
                         (component/using [:endpoint-b-middleware]))
         :endpoint-b-middleware (new-middleware {:middleware [,,,]})
         :middleware (new-middleware {:middleware [,,,]})
         :handler (-> (new-handler)
                      (component/using [:endpoint-a :endpoint-b :middleware]))
         :jetty (-> (new-web-server port)
                    (component/using [:handler])))"
  ([] (->Handler)))
