(ns system.components.eclipsestore
  (:require [com.stuartsierra.component :as component])
  (:import [org.eclipse.store.storage.embedded.types EmbeddedStorage]))

(defrecord EclipseStore []
  component/Lifecycle
  (start [component]
    (let [storage-manager (EmbeddedStorage/start)
          data (if (.root storage-manager)
               (.root storage-manager)
               {})
          data-in-atom (atom data)]
      (add-watch data-in-atom :watcher
               (fn [key atom old-state new-state]
                 (.setRoot storage-manager new-state)
                 (.storeRoot storage-manager)))
      (assoc component :db data-in-atom :storage-manager storage-manager)))
  (stop [component]
    (remove-watch (:db component) :watcher)
    (.shutdown (:storage-manager component))
    (dissoc component :db)))

(defn new-eclipsestore [& {:keys []}]
  (map->EclipseStore {}))
