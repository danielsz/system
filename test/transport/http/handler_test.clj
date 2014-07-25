(ns framework.transport.http.handler-test
  (:require [framework.transport.http.handler :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :refer :all]))

(deftest request-ok
  (testing "get /bar returns succesfully"
    (is (= (select-keys (app (request :get "/bar")) [:status]) 
        {:status 200}))))

(deftest request-not-ok
  (testing "get /foo doesn't return succesfully"
    (is (not= (select-keys (app (request :get "/foo")) [:status]) 
        {:status 200}))))
