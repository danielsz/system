(ns system.core
  (:require 
   [com.stuartsierra.component :as component]))

(defmacro defsystem
  "Convenience macro to build a system"
  [fn system-map]
  `(defn ~fn [] (apply component/system-map ~@system-map)))
