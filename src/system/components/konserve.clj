(ns system.components.konserve
  (:require [com.stuartsierra.component :as component]
            [konserve.filestore :refer [new-fs-store]]
            [konserve.memory :refer [new-mem-store]]
            [clojure.core.async :as async :refer [<!!]]))

(defrecord Konserve [type path serializer]
  component/Lifecycle
  (start [component]
    (let [store (case type
                  :carmine (do
                             (require 'konserve-carmine.core)
                             (if serializer
                               (<!! ((ns-resolve 'konserve-carmine.core (symbol "new-carmine-store")) {:pool {} :spec {}} :serializer serializer))
                               (<!! ((ns-resolve 'konserve-carmine.core (symbol "new-carmine-store"))))))
                  :filestore (if serializer
                               (<!! (new-fs-store path :serializer serializer))
                               (<!! (new-fs-store path)))
                  :memstore (<!! (new-mem-store)))]
      (assoc component :store store)))
  (stop [component]
    (dissoc component :store)))

(defn new-konserve [& {:keys [type path serializer] :or {type :memstore}}]
  (map->Konserve {:type type :path path :serializer serializer}))


