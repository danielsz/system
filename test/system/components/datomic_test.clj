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
