(ns system.components.postgres
  (:require [system.components.jdbc :as data]))

;; component for a PostgreSQL database

(def DEFAULT-DB-SPEC
  {:classname   "org.postgresql.Driver" ; must be in classpath
   :subprotocol "postgresql"
   :subname ""
   :user     "test"
   :password "test"
   :host "127.0.0.1"})

(defn new-postgres-database
  [spec]
  (data/new-database spec))
