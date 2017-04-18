(ns system.components.konserve
  (:require [com.stuartsierra.component :as component]
            [konserve.filestore :refer [new-fs-store]]
            [konserve.memory :refer [new-mem-store]]
            [clojure.core.async :as async :refer [<!!]]))

(defrecord Konserve [type path serializer]
  component/Lifecycle
  (start [component]
    (let [store (case type
                  :filestore (if serializer
                               (<!! (new-fs-store path :serializer serializer))
                               (<!! (new-fs-store path)))
                  :memstore (<!! (new-mem-store)))]
      (assoc component :store store)))
  (stop [component]
    (dissoc component :store)))

(defn new-konserve
  ([]
   (map->Konserve {:type :memstore}))
  ([path]
   (map->Konserve {:type :filestore :path path}))
  ([path serializer]
   (map->Konserve {:type :filestore :path path :serializer serializer})))
