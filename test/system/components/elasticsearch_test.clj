(ns system.components.elasticsearch-test
  (:require [system.components.elasticsearch :refer [new-elasticsearch-db]]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is]])
  (:import [org.elasticsearch.action.search SearchRequest]))

(deftest ^:dependency test-elasticsearch
  (let [cluster-name (str "elasticsearch_" (System/getProperty "user.name"))
        elasticsearch-db (component/start
                           (new-elasticsearch-db
                             [["localhost" 9300]]
                             {"cluster.name" cluster-name}))]
    (try
      (is @(.search (:client elasticsearch-db)
                    (SearchRequest. (make-array String 0))))
      (is (nil? (:client (component/stop elasticsearch-db))))
      (finally
        (component/stop elasticsearch-db)))))
