(ns system.components.mongo-test
  (:require 
   [system.components.mongo :refer [new-mongo-db]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is]]
   [mikera.cljutils.error :refer [error?]]
   [monger.db :as db]
   [monger.collection]))


(def mongo-db-prod (new-mongo-db "mongodb://127.0.0.1/monger-test4"))
(def mongo-db-dev (new-mongo-db)) 

;; Execute tests only if Mongo connection is successful
(when (not (error? (let [d (component/start (new-mongo-db "mongodb://127.0.0.1/monger-test4"))]
                     (db/get-collection-names d)
                     (component/stop d)))) 

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

) 
