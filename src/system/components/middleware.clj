(ns system.components.middleware
  (:require [com.stuartsierra.component :as component]))

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

(defrecord Middleware [middleware]
  component/Lifecycle
  (start [component]
    (let [wrap-mw (compose-middleware middleware)]
      (assoc component :wrap-wm wrap-mw)))
  (stop [component]
    (dissoc component :wrap-wm)))

(defn new-middleware
  ([middleware] (->Middleware middleware)))
