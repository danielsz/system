(ns system.components.konserve-test
  (:require [system.components.konserve :as c]
            [konserve.core :as k]
            [konserve.serializers :as s]
            [konserve.filestore :refer [delete-store list-keys]]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as async :refer [<!!]]
            [clojure.test :refer [deftest testing is]]))


(deftest memory-store
  (testing "Test the core API."
    (let [db (component/start (c/new-konserve))
          store (:store db)]
      (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/assoc-in store [:foo] :bar))
      (is (= (<!! (k/get-in store [:foo]))
             :bar))
      (<!! (k/dissoc store :foo))
      (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/bassoc store :binbar (byte-array (range 10))))
      (<!! (k/bget store :binbar (fn [{:keys [input-stream]}]
                                   (is (= (map byte (slurp input-stream))
                                          (range 10)))))))))



(deftest filesystem
  (let [path "/tmp/konserve-fs-test"
        _ (delete-store path)
        db (component/start (c/new-konserve :type :filestore :path path))
        store (:store db)]
     (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/assoc-in store [:foo] :bar))
      (is (= (<!! (k/get-in store [:foo]))
             :bar))
      (is (= (<!! (list-keys store))
             #{[:foo]}))
            (<!! (k/dissoc store :foo))
      (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/bassoc store :binbar (byte-array (range 10))))
      (<!! (k/bget store :binbar (fn [{:keys [input-stream]}]
                                 (is (= (map byte (slurp input-stream))
                                        (range 10))))))

      (is (= (<!! (list-keys store))
             #{}))
      (component/stop db)))

(deftest serializer
  (let [path "/tmp/konserve-fs-test"
        _ (delete-store path)
        db (component/start (c/new-konserve :type :filestore :path path :serializer (s/fressian-serializer)))
        store (:store db)]
     (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/assoc-in store [:foo] :bar))
      (is (= (<!! (k/get-in store [:foo]))
             :bar))
      (is (= (<!! (list-keys store))
             #{[:foo]}))
            (<!! (k/dissoc store :foo))
      (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/bassoc store :binbar (byte-array (range 10))))
      (<!! (k/bget store :binbar (fn [{:keys [input-stream]}]
                                 (is (= (map byte (slurp input-stream))
                                        (range 10))))))

      (is (= (<!! (list-keys store))
             #{}))
      (component/stop db)))


(deftest ^:dependency carmine-store
  (testing "Test the core API."
    (let [db (component/start (c/new-konserve :type :carmine))
          store (:store db)]
      (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/assoc-in store [:foo] :bar))
      (is (= (<!! (k/get-in store [:foo]))
             :bar))
      (<!! (k/dissoc store :foo))
      (is (= (<!! (k/get-in store [:foo]))
             nil))
      (<!! (k/bassoc store :binbar (byte-array (range 10))))
      (<!! (k/bget store :binbar (fn [{:keys [input-stream]}]
                                   (is (= (map byte (slurp input-stream))
                                          (range 10)))))))))
