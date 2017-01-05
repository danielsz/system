(ns system.components.konserve
  (:require [com.stuartsierra.component :as component]
            [konserve.filestore :refer [new-fs-store]]
            [konserve.memory :refer [new-mem-store]]))

(defrecord Konserve [type opts]
  component/Lifecycle
  (start [component]
    (let [store (case type
                  :filestore (new-fs-store (:path opts))
                  :memstore (new-mem-store))]
      (assoc component :store store)))
  (stop [component]
    (dissoc component :store)))

(defn new-konserve
  ([]
   (map->Konserve {:type :memstore :opts {}}))
  ([path]
   (map->Konserve {:type :filestore :opts {:path path}})))
