(ns system.components.kampbell-test
  (:require [system.components
             [kampbell :as kampbell]
             [konserve :as konserve]]
            [com.stuartsierra.component :as component]
            [clojure.core.async :refer [<!! <! go]]
            [kampbell.core :as k]
            [maarschalk.konserve :as m]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.test :as t :refer [use-fixtures deftest is testing]]
            [clojure.tools.logging :as log])
  (:import [java.nio.file Files]
           [java.nio.file.attribute PosixFilePermissions FileAttribute]
           [java.nio.file Path Paths]
           [java.time Instant]))

(def ^:dynamic *db-path*)

(defn delete-dir [path]
  (doseq [f (reverse (file-seq (io/file path)))]
    (clojure.java.io/delete-file f)))

(defn create-temp-dir [prefix]
  (let [perms (PosixFilePermissions/fromString "rwxr-x---")
        attr (PosixFilePermissions/asFileAttribute perms)
        attrs (into-array FileAttribute [attr])]
    (Files/createTempDirectory prefix attrs)))


(defn once-fixture [f]
  (binding [*db-path* (str (create-temp-dir "data"))]
    (f)
    (delete-dir *db-path*)))

;; register as a one-time callback
(use-fixtures :once once-fixture)

(s/def :domain.user/address string?)
(s/def :domain.user/name string?)
(s/def :domain.user/email (s/and string? #(re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}" %)))
(s/def :domain/user (s/keys :req [:domain.user/name
                                  :domain.user/email]
                            :opt [:domain.user/address]))

(def good-input #:domain.user{:name "Daniel Szmulewicz"
                              :email "daniel@szmulewicz.com"})

(def bad-input {:name "Daniel Szmulewicz"
                :email "daniel@szmulewicz.com"})

(defn save-user [db v]
  (let [v (assoc v :domain.utils/created-at (Instant/now))]
    (<!! (k/save-entity db :domain/user v))))

(defn get-users [db]
  (<!! (k/get-entities db "users")))

(defn get-user [db v]
  (<!! (k/get-entity db "users" :domain.user/email v)))

(defn update-user [db v & specs]
  {:pre [(some? specs) (every? keyword? specs)]}
  (<!! (k/update-entity db "users" v (into #{} specs))))

(defn delete-user [db v]
  (<!! (k/delete-entity db :domain/user v)))

(deftest Kampbell
  (let [system (-> (component/system-map
                    :db (konserve/new-konserve :type :filestore :path *db-path* :serializer (m/fressian-serializer))
                    :kampbell (component/using (kampbell/new-kampbell :equality-specs #{:domain.utils/created-at} :entities #{"users"}) [:db]))
                   component/start)
        db (:store (:db system))]
    (is (some? db))
    (is (= #{["users"]} (k/list-collections db)))
    (is (contains? kampbell.core/equality-specs :created-at))
    (is (contains? kampbell.core/equality-specs :domain.utils/created-at))
    (is (empty? (get-users db)))
    (save-user db good-input)
    (is (= 1 (count (get-users db))))
    (save-user db good-input)
    (is (= 1 (count (get-users db))))
    (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Invalid input" (save-user db bad-input)))
    (save-user db (assoc good-input :domain.user/name "Alan Kay"))
    (is (= 2 (count (get-users db))))
    (let [user (get-user db "daniel@szmulewicz.com")]
      (is (=  "Daniel Szmulewicz" (:domain.user/name user)))
      (update-user db (assoc user :domain.user/name "Hans Solo") :domain.user/name))
    (let [user (get-user db "daniel@szmulewicz.com")]
      (s/valid? :domain/user user))
    (let [user (get-user db "daniel@szmulewicz.com")]
      (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Invalid input" (update-user db (assoc user :domain.user/name 1234) :domain.user/name))))
    (let [user (get-user db "daniel@szmulewicz.com")]
      (is (=  "Hans Solo" (:domain.user/name user)))
      (update-user db (assoc user :domain.user/name "Greedo" :domain.user/email "greedo@greedo.com") :domain.user/name :domain.user/email))
    (is (some #(= "Greedo" (:domain.user/name %)) (get-users db)))
    (let [user (get-user db "greedo@greedo.com")]
      (update-user db (assoc user :domain.user/address "Stairway to Heaven 669") :domain.user/address)
      (is (= "Stairway to Heaven 669" (:domain.user/address (get-user db "greedo@greedo.com")))))
    (let [user (get-user db "greedo@greedo.com")]
      (is (true? (contains? user :domain.user/address))))
    (let [user (get-user db "greedo@greedo.com")]
      (update-user db (dissoc user :domain.user/address) :domain.user/address)
      (is (nil? (:domain.user/address (get-user db "greedo@greedo.com")))))
    (let [user (get-user db "greedo@greedo.com")]
      (is (false? (contains? user :domain.user/address))))
    (let [user (get-user db "greedo@greedo.com")]
      (update-user db (dissoc user :domain.user/name) :domain.user/name)
      (is (nil? (:domain.user/name (get-user db "greedo@greedo.com")))))
    (let [user (get-user db "greedo@greedo.com")]
      (is (false? (s/valid? :domain/user user)))) ;; doesn't catch absence of required keys, breaks integrity guarantee
    (let [user (get-user db "greedo@greedo.com")]
      (delete-user db user))
    (is (= 1 (count (get-users db))))
    (component/stop system)))
