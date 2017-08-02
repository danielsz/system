(ns system.components.benjamin
  (:require [com.stuartsierra.component :as component]
            [benjamin.configuration :as config :refer [set-config!]]))

(defrecord Logbook [success-fn persistence-fn logbook-fn events wrap-component? allow-undeclared-events?]
  component/Lifecycle
  (start [component]
    (if wrap-component?
      (set-config! :persistence-fn (persistence-fn component))
      (set-config! :persistence-fn persistence-fn))
    (when success-fn (set-config! :success-fn success-fn))
    (when logbook-fn (set-config! :logbook-fn logbook-fn))
    (when events (set-config! :events events))
    (when allow-undeclared-events? (set-config! :allow-undeclared-events? allow-undeclared-events?))
    component)
  (stop [component]
    (config/reset!)
    component))

(defn new-logbook [& {:keys [success-fn persistence-fn logbook-fn events wrap-component? allow-undeclared-events?] :as options}]
  (map->Logbook options))
