(ns system.components.datomic-test
  (:require [system.components.datomic :refer [new-datomic-db]]
   [com.stuartsierra.component :as component]
   [datomic.api :as d]
   [clojure.test :refer [deftest testing is]]))

(def uri "datomic:mem://localhost:4334/framework-test")

(def datomic-db (new-datomic-db uri))

(deftest datomic-lifecycle
  (testing "Datomic lifecycle operations."
    (alter-var-root #'datomic-db component/start)
    (is (= (type (:conn datomic-db))
           datomic.peer.LocalConnection))
    (is (d/delete-database uri))
    (alter-var-root #'datomic-db component/stop)))

(def schema '[{:db/ident ::name
               :db/valueType :db.type/string
               :db/cardinality :db.cardinality/one
               :db/doc "a test attr"}])

(defn init-schema [conn]
  @(d/transact conn schema))

(def datomic-db-with-schema (new-datomic-db uri
                                            init-schema))

(defn has-attribute?
  "Does database have an attribute named attr-name?"
  [db attr-name]
  (-> (d/entity db attr-name)
      :db.install/_attribute
      boolean))

(deftest datomic-lifecycle-with-schema
  (testing "Datomic lifecycle operations."
    (alter-var-root #'datomic-db-with-schema component/start)
    (is (= (type (:conn datomic-db-with-schema))
           datomic.peer.LocalConnection))
    (is (has-attribute? (d/db (:conn datomic-db-with-schema)) ::name))
    (is (d/delete-database uri))
    (alter-var-root #'datomic-db-with-schema component/stop)))
