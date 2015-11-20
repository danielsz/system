(ns system.components.mongo-test
  (:require 
   [system.components.mongo :refer [new-mongo-db]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is]]
   [monger.db :as db]
   [monger.collection]))


(def mongo-db-prod (new-mongo-db "mongodb://127.0.0.1/monger-test4"))
(def mongo-db-dev (new-mongo-db)) 
(def mongo-with-indices (new-mongo-db "mongodb://127.0.0.1/monger-test4" #(do (monger.collection/ensure-index % "coll" (array-map :shop-id 1))
                                                                              (monger.collection/ensure-index % "users" {:email 1} {:unique true}))))

(deftest mongo-production
  (alter-var-root #'mongo-db-prod component/start)
  (is (:db mongo-db-prod) "DB has been added to component")
  (is (= clojure.lang.PersistentHashSet (type (db/get-collection-names (:db mongo-db-prod)))) "Collections on DB is a set")
  (alter-var-root #'mongo-db-prod component/stop)
  (is (nil? (:db mongo-db-prod)) "DB is stopped"))

(deftest mongo-development
  (alter-var-root #'mongo-db-dev component/start)
  (is (:db mongo-db-dev) "DB has been added to component")
  (monger.collection/create (:db mongo-db-dev) "coll" {:capped true :size 100000 :max 10})
  (is (monger.collection/exists? (:db mongo-db-dev) "coll"))
  (monger.collection/drop (:db mongo-db-dev) "coll")
  (alter-var-root #'mongo-db-dev component/stop)
  (is (nil? (:db mongo-db-dev)) "DB is stopped"))

(deftest mongo-indices
  (alter-var-root #'mongo-with-indices component/start)
  (is (:db mongo-with-indices) "DB has been added to component")
  (is (= clojure.lang.PersistentHashSet (type (db/get-collection-names (:db mongo-with-indices)))) "Collections on DB is a set")
  (is (monger.collection/exists? (:db mongo-with-indices) "users"))
  (is (monger.collection/exists? (:db mongo-with-indices) "coll"))
  (is (> (count (monger.collection/indexes-on (:db mongo-with-indices) "users")) 1))
  (is (> (count (monger.collection/indexes-on (:db mongo-with-indices) "coll")) 1))
  (alter-var-root #'mongo-with-indices component/stop)
  (is (nil? (:db mongo-with-indices)) "DB is stopped"))
