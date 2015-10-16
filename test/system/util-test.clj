(ns system.util-test
  (:require [system.util :refer [assert-only-contains-options!]]
            [clojure.test :refer [testing deftest is]]))

(deftest assert-only-contains-options!-test
  (is (nil? (assert-only-contains-options!
             "test-component"
             {:opt1 1 :opt2 2}
             [:opt1 :opt2])))
  (is (thrown-with-msg? AssertionError
                        #"Invalid option\(s\) for test-component: \(:opt2\)"
                        (assert-only-contains-options!
                               "test-component"
                               {:opt1 1 :opt2 2}
                               [:opt1]))))
