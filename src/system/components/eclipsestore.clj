(ns system.components.eclipsestore
  (:require [com.stuartsierra.component :as component])
  (:import [org.eclipse.store.storage.embedded.types EmbeddedStorage]))

(defrecord EclipseStore []
  component/Lifecycle
  (start [component]
    (let [storage-manager (EmbeddedStorage/start)
          root (if (.root storage-manager)
                 (.root storage-manager)
                 {})
          data (atom root)]
      (.setRoot storage-manager root)      
      (add-watch data :watcher
                 (fn [key atom old-state new-state]               
                   (.storeRoot storage-manager)))
      (assoc component :db data :storage-manager storage-manager)))
  (stop [component]    
    (remove-watch (:db component) :watcher)
    (.shutdown (:storage-manager component))
    (dissoc component :db :storage-manager)))

(defn new-eclipsestore [& {:keys []}]
  (map->EclipseStore {}))
