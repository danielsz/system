(ns system.core
  (:require 
   [com.stuartsierra.component :as component]))

(defmacro defsystem
  "Convenience macro to build a system"
  [fname system-map]
  `(defn ~fname [] (component/system-map ~@system-map)))
