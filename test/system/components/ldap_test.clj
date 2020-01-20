(ns system.components.ldap-test
  (:require [system.components.ldap :as sut]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is]])
  (:import [com.unboundid.ldap.sdk LDAPBindException]))

(deftest ^:dependency openldap-connection
  (is (thrown? LDAPBindException (component/start (sut/new-ldap  :bind-dn "cn=root,dc=test,dc=com" :pass "password")))))
