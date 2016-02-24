(ns system.components.mongo-test
  (:require
   [system.components.mongo :refer [new-mongo-db]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is use-fixtures]]
   [monger.db :as db]
   [monger.core :as mg]
   [monger.collection :as mc]
   [monger.command :as cmd]))

(def init-fn #(do (mc/ensure-index % "coll1" (array-map :shop-id 1))
                  (mc/ensure-index % "users" {:email 1} {:unique true})))

(defn create-and-drop-collection [db]
  (mc/create db  "collection" {:capped true :size 100000 :max 10})
  (is (mc/exists? db "collection"))
  (mc/drop db "collection"))

(defn current-connections [db]
  (get-in (cmd/server-status db) ["connections" "current"]))

(def options {:connections-per-host 40
              :threads-allowed-to-block-for-connection-multiplier 300})

(def mongo-db-with-options (new-mongo-db "127.0.0.1" 27017 "test" options))
(def mongo-db-with-options-and-init (new-mongo-db "127.0.0.1" 27017 "test" options init-fn))
(def mongo-db-prod (new-mongo-db "mongodb://127.0.0.1/monger-test4"))
(def mongo-db-dev (new-mongo-db))
(def mongo-with-indices (new-mongo-db "mongodb://127.0.0.1/monger-test4" init-fn))

(defn test-open-connections [f]
  (let [{:keys [conn db]} (mg/connect-via-uri "mongodb://127.0.0.1/test")
        initial-cc (current-connections db)]
    (f)
    (is (= initial-cc (current-connections db)))
    (mg/disconnect conn)))

(use-fixtures :each test-open-connections)

(deftest mongo-production
  (alter-var-root #'mongo-db-prod component/start)
  (is (:db mongo-db-prod) "DB has been added to component")
  (is (= clojure.lang.PersistentHashSet (type (db/get-collection-names (:db mongo-db-prod)))) "Collections on DB is a set")
  (create-and-drop-collection (:db mongo-db-prod))
  (println (get (cmd/server-status (:db mongo-db-prod)) "connections"))
  (alter-var-root #'mongo-db-prod component/stop)
  (is (nil? (:db mongo-db-prod)) "DB is stopped"))

(deftest mongo-production-with-options
  (alter-var-root #'mongo-db-with-options component/start)
  (is (:db mongo-db-with-options) "DB has been added to component")
  (is (= clojure.lang.PersistentHashSet (type (db/get-collection-names (:db mongo-db-with-options)))) "Collections on DB is a set")
  (create-and-drop-collection (:db mongo-db-with-options))
  (println (get (cmd/server-status (:db mongo-db-with-options)) "connections"))
  (alter-var-root #'mongo-db-with-options component/stop)
  (is (nil? (:db mongo-db-with-options)) "DB is stopped"))

(deftest mongo-production-with-options-and-init
  (let [db #'mongo-db-with-options-and-init]
    (alter-var-root db component/start)
    (is (:db mongo-db-with-options-and-init) "DB has been added to component")
    (is (= clojure.lang.PersistentHashSet (type (db/get-collection-names (:db mongo-db-with-options-and-init)))) "Collections on DB is a set")
    (create-and-drop-collection (:db mongo-db-with-options-and-init))
    (is (monger.collection/exists? (:db mongo-db-with-options-and-init) "users"))
    (is (monger.collection/exists? (:db mongo-db-with-options-and-init) "coll1"))
    (is (> (count (monger.collection/indexes-on (:db mongo-db-with-options-and-init) "users")) 1))
    (is (> (count (monger.collection/indexes-on (:db mongo-db-with-options-and-init) "coll1")) 1))
    (println (get (cmd/server-status (:db mongo-db-with-options-and-init)) "connections"))
    (alter-var-root db component/stop)
    (is (nil? (:db mongo-db-with-options-and-init)) "DB is stopped")))

(deftest mongo-development
  (alter-var-root #'mongo-db-dev component/start)
  (is (:db mongo-db-dev) "DB has been added to component")
  (create-and-drop-collection (:db mongo-db-dev))
  (alter-var-root #'mongo-db-dev component/stop)
  (is (nil? (:db mongo-db-dev)) "DB is stopped"))

(deftest mongo-development-idempotence
  (alter-var-root #'mongo-db-dev component/start)
  (test-open-connections #(alter-var-root #'mongo-db-dev component/start))
  (alter-var-root #'mongo-db-dev component/stop)
  (is (nil? (:db mongo-db-dev)) "DB is stopped"))

(deftest mongo-indices
  (alter-var-root #'mongo-with-indices component/start)
  (is (:db mongo-with-indices) "DB has been added to component")
  (is (= clojure.lang.PersistentHashSet (type (db/get-collection-names (:db mongo-with-indices)))) "Collections on DB is a set")
  (is (monger.collection/exists? (:db mongo-with-indices) "users"))
  (is (monger.collection/exists? (:db mongo-with-indices) "coll1"))
  (is (> (count (monger.collection/indexes-on (:db mongo-with-indices) "users")) 1))
  (is (> (count (monger.collection/indexes-on (:db mongo-with-indices) "coll1")) 1))
  (alter-var-root #'mongo-with-indices component/stop)
  (is (nil? (:db mongo-with-indices)) "DB is stopped"))

(deftest mongo-with-bogus-options
  (is (= {:bogus 'disallowed-key} (try (new-mongo-db "127.0.0.1" 27017 "test" {:bogus true})
                                        (catch clojure.lang.ExceptionInfo e (:error (ex-data e)))))))
