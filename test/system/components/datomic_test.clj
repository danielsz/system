(ns system.components.datomic-test
  (:require [system.components.datomic :refer [new-datomic-db]]
   [com.stuartsierra.component :as component]
   [datomic.api :as d]
   [clojure.test :refer [deftest is]]
   [environ.core :refer [env]]))

(def uri (str (env :db-url) "-test"))

(def datomic-db (new-datomic-db uri))
 
(deftest datomic-lifecycle
  (alter-var-root #'datomic-db component/start)
  (is (:db datomic-db) "DB as a value has been added to component")
  (is (d/delete-database uri) "Database deleted")  
  (alter-var-root #'datomic-db component/stop))
