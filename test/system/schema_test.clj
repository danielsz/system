(ns system.schema-test
  (:require [system.schema :as sc]
            [schema.core :as s]
            [clojure.test :refer [deftest is]]))

(deftest port-lt-0-throws
  (is (thrown? RuntimeException (s/validate sc/Port -1))))

(deftest port-gt-65535-throws
  (is (thrown? RuntimeException (s/validate sc/Port 65536))))

(deftest port-0-valid
  (is (= 0 (s/validate sc/Port 0))))

(deftest port-65535-valid
  (is (= 65535 (s/validate sc/Port 65535))))

(deftest port-float-throws
  (is (thrown? RuntimeException (s/validate sc/Port 0.0))))

(deftest posint-0-throws
  (is (thrown? RuntimeException (s/validate sc/PosInt 0))))

(deftest posint-1-valid
  (is (= 1 (s/validate sc/PosInt 1))))

(deftest posint-float-throws
  (is (thrown? RuntimeException (s/validate sc/PosInt 1.0))))
