(ns system.components.adi
  (:require [com.stuartsierra.component :as component]
            [adi.core :as adi]
            [adi.core.types :refer [map->Adi]]))

  (extend-type adi.core.types.Adi
    component/Lifecycle
    (start [component]
      (if (:connection component)
        component
        (adi/connect! component)))

    (stop [component]
      (if (:connection component)
        (adi/disconnect! component)
        component)))

  (defn new-adi-db
    [uri schema & [reset? install-schema?]]
    (map->Adi {:meta {:uri             uri
                      :reset?          reset?
                      :install-schema? install-schema?}
               :schema schema}))
