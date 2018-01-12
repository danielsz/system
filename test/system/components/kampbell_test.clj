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
            [clojure.test :as t :refer [use-fixtures deftest is testing]])
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

(defn one-time-setup []
  (println "one time setup"))

(defn one-time-teardown []
  (println "one time teardown"))

(defn once-fixture [f]
  (binding [*db-path* (str (create-temp-dir "data"))]
    (f)
    (delete-dir *db-path*)))

;; register as a one-time callback
(use-fixtures :once once-fixture)

(s/def :domain.user/name string?)
(s/def :domain.user/email (s/and string? #(re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}" %)))
(s/def :domain/user (s/keys :req [:domain.user/name
                                  :domain.user/email]))

(def good-input #:domain.user{:name "Daniel Szmulewicz"
                               :email "daniel@szmulewicz.com"})

(def bad-input {:name "Daniel Szmulewicz"
                :email "daniel@szmulewicz.com"})

(defn save-user [store v]
  (let [v (assoc v :domain.utils/created-at (Instant/now))]
    (<!! (k/save-entity store :domain/user v))))

(defn get-users [store]
  (<!! (k/get-coll store "users")))

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
    (component/stop system)))
