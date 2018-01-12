(ns system.components.kampbell
  (:require [com.stuartsierra.component :as component]
            [lang-utils.core :refer [seek ∘]]
            [kampbell.core :as k]))

(defrecord Kampbell [entities equality-specs]
  component/Lifecycle
  (start [component]
    (when equality-specs
      (if (sequential? equality-specs)
        (alter-var-root #'k/equality-specs concat equality-specs)
        (alter-var-root #'k/equality-specs conj equality-specs)))
    (when entities
      (when-let [db (seek (∘ :store val) component)]
        (k/seed-db (:store (val db)) entities)))
    component)
  (stop [component]
    component))

(defn new-kampbell
  [& {:keys [entities equality-specs]}]
  (map->Kampbell {:entities entities :equality-specs equality-specs}))
