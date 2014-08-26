(ns system.core
  (:require 
   [com.stuartsierra.component :as component]))

(defmacro defsystem
  "Convenience macro to build a system"
  [var system-map]
  `(def ~var (apply component/system-map ~@system-map)))
