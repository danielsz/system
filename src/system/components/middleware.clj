(ns system.components.middleware
  (:require [com.stuartsierra.component :as component]))

(defn- middleware-fn
  "Middleware are specified in a vector, either as standalone
  functions (when they don't take arguments other than the handler),
  or as vectors where the function come first, and the arguments
  next.

  Example:

  (new-middleware {:middleware [wrap-restful-format
                                [wrap-defaults site]
                                [wrap-not-found (html/not-found)]]})"
  [entry]
  (if (vector? entry)
    #(apply (first entry) % (rest entry))
    entry))

(defn- compose
  "Middleware functions compose. However, pay close attention when writing impure middleware, such that mutate the request.

  Explanation for reverse:
  https://github.com/duct-framework/duct/issues/31#issuecomment-171459482"
  [entries]
  (apply comp (map middleware-fn (reverse entries))))


(defn- sanitize
  "Replaces the keyword `:component` with the actual component."
  [component entry]
  (if (vector? entry)
    (replace {:component component} entry)
    entry))

(defrecord Middleware [middleware]
  component/Lifecycle
  (start [component]
    (let [sanitize (partial sanitize component)
          entries (mapv sanitize (:middleware middleware))
          wrap-mw (compose entries)]
      (assoc component :wrap-mw wrap-mw)))
  (stop [component]
    (dissoc component :wrap-mw)))

(defn new-middleware
  "If you want to inject dependencies in middleware, the convention is to pass the
  keyword `:component` as argument in the vector. 
  
  Example:

  (component/system-map
          :db (new-db)
          :endpoint (-> (new-endpoint other-routes)
                          (component/using [:endpoint-middleware]))
          :endpoint-middleware (component/using (new-middleware {:middleware [wrap-login :component]}) [:db])
          :middleware (new-middleware {:middleware [,,,]})
          :handler (-> (new-handler)
                       (component/using [:endpoint :middleware]))
          :jetty (-> (new-web-server port)
                     (component/using [:handler])))
  
  Now you can write middleware that wraps the component, like so:

  (defn wrap-login [handler {db :db}]
    (fn [request]
      (do-something-with db)
       ...
      (handler request)))))"
  ([middleware] (->Middleware middleware)))
