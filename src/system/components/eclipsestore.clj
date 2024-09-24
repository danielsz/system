(ns system.components.eclipsestore
  (:require [com.stuartsierra.component :as component])
  (:import [org.eclipse.store.storage.embedded.types EmbeddedStorage]))

(def storage-manager (EmbeddedStorage/start))

(defrecord EclipseStore []
  component/Lifecycle
  (start [component]
    (let [data (if (.root storage-manager)
               (atom (.root storage-manager))
               (atom {}))]
      (add-watch data :watcher
               (fn [key atom old-state new-state]
                 (.setRoot storage-manager new-state)
                 (.storeRoot storage-manager)))
      (assoc component :db data)))
  (stop [component]
    (remove-watch (:db component) :watcher)
    (.shutdown storage-manager)
    (assoc component :db nil)))

(defn new-eclipsestore [& {:keys []}]
  (map->EclipseStore {}))
