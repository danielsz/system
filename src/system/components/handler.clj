(ns system.components.handler
  (:require [com.stuartsierra.component :as component]
             [compojure.core :as compojure]))

(defrecord Handler []
  component/Lifecycle
  (start [component]
    (let [routes (keep :routes (vals component))
          wrap-mw (get-in component [:middleware :wrap-wm] identity)
          handler (wrap-mw (apply compojure/routes routes))]
      (assoc component :handler handler)))
  (stop [component]
    (dissoc component :handler)))

(defn new-handler
  ([] (->Handler)))
