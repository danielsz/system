(ns system.schema_test
  (:require [system.schema :as ss]
            [schema.core :as s]
            [clojure.test :refer [deftest is]]))

(deftest port-lt-0-throws
  (is (thrown? RuntimeException (s/validate ss/Port -1))))

(deftest port-gt-65535-throws
  (is (thrown? RuntimeException (s/validate ss/Port 65536))))

(deftest port-0-valid
  (is (= 0 (s/validate ss/Port 0))))

(deftest port-65535-valid
  (is (= 65535 (s/validate ss/Port 65535))))

(deftest port-float-throws
  (is (thrown? RuntimeException (s/validate ss/Port 0.0))))

(deftest posint-0-throws
  (is (thrown? RuntimeException (s/validate ss/PosInt 0))))

(deftest posint-1-valid
  (is (= 1 (s/validate ss/PosInt 1))))

(deftest posint-float-throws
  (is (thrown? RuntimeException (s/validate ss/PosInt 1.0))))
