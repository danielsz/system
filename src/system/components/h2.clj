(ns system.components.h2
  (:require [system.components.jdbc :as jdbcc]
            [com.stuartsierra.component :as component]))

;; returns a JDBC component with a H2 spec

(defn new-h2-database
  ([spec]
   (jdbcc/new-database spec))
  ([spec init-fn]
   (jdbcc/new-database spec init-fn)))
