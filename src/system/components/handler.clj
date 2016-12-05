(ns system.components.handler
  (:require [com.stuartsierra.component :as component]
            [lang-utils.core :refer [contains+? &]]
            [compojure.core :as compojure]))

(defn- endpoints [component]
  (filter (comp :routes val) component))

(defn- with-middleware
  ([endpoints]
   (with-middleware endpoints true))
  ([endpoints flag]
   (let [f (if flag
             (fn [[k v]] (contains+? v :middleware))
             (fn [[k v]] (not (contains+? v :middleware))))]
     (filter f endpoints))))

(defn- middleware-key [endpoint]
  (reduce-kv (fn [_ k v] (if (contains+? v :middleware) (reduced k) _)) {} (val endpoint)))

(defrecord Handler []
  component/Lifecycle
  (start [component]
    (let [endpoints-with-middleware (partition-by middleware-key ((& with-middleware endpoints) component))
          handlers (for [endpoints endpoints-with-middleware
                         :let [mw-key (middleware-key (first endpoints))
                               wrap-mw (get-in (val (first endpoints)) [mw-key :wrap-mw])
                               routes (keep :routes (vals endpoints))]]
                     (wrap-mw (apply compojure/routes routes)))
          routes (keep :routes (vals (-> component
                                         endpoints
                                         (with-middleware false))))
          wrap-mw (get-in component [:middleware :wrap-mw] identity)
          handler (wrap-mw (apply compojure/routes (concat  handlers routes)))]
      (assoc component :handler handler)))
  (stop [component]
    (dissoc component :handler)))

(defn new-handler
  ([] (->Handler)))
