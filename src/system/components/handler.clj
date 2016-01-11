(ns system.components.handler
  (:require [com.stuartsierra.component :as component]
             [compojure.core :as compojure]))

(defn- middleware-fn [middleware entry]
  (if (vector? entry)
    (let [[f & keys] entry
          arguments  (map #(get middleware %) keys)]
      #(apply f % arguments))
    entry))

(defn- compose-middleware [middleware]
  (let [entries (:middleware middleware)]
    (->> (reverse entries)
         (map #(middleware-fn middleware %))
         (apply comp identity))))

(defrecord Handler [middleware]
  component/Lifecycle
  (start [component]
    (let [routes (keep :routes (vals component))]
      (if middleware
        (let [wrap-mw (compose-middleware middleware)
              handler (wrap-mw (apply compojure/routes routes))]
          (assoc component :handler handler))
        (assoc component :handler (apply compojure/routes routes)))))
  (stop [component]
    (dissoc component :handler)))

(defn new-handler
  ([] (new-handler nil))
  ([middleware]
   (->Handler middleware)))
