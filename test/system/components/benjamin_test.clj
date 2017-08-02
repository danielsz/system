(ns system.components.benjamin-test
  (:require [benjamin.core :as b]
            [benjamin.configuration :refer [config]]
            [system.components.benjamin :refer [new-logbook]]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest testing is]]))


(deftest configuration
  (testing "Sanity check"
    (is (nil? b/success-fn))
    (is ((:success-fn config)))
    (component/start (new-logbook :success-fn (constantly false)))
    (is (not ((:success-fn config))))
    (is (not (with-bindings {#'b/success-fn (:success-fn config)}
               (b/success-fn))))))
