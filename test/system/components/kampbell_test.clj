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

(s/def ::name string?)
(s/def ::email (s/and string? #(re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}" %)))
(s/def ::user (s/keys :req [::name
                            ::email]))

(defn save-user [store v]
  (let [v (assoc v :created_at (Instant/now))]
    (<!! (k/save-entity store ::user v))))

(defn get-users [store]
  (<!! (k/get-coll store "users")))

(def daniel #::{:name "Daniel Szmulewicz"
                :email "daniel@szmulewicz.com"})

(deftest Kampbell
  (let [system (-> (component/system-map
                    :db (konserve/new-konserve :type :filestore :path *db-path* :serializer (m/fressian-serializer))
                    :kampbell (component/using (kampbell/new-kampbell :equality-specs [:made_at] :entities ["users"]) [:db]))
                   component/start)
        db (:store (:db system))]
    (is (some? db))
    (is (= #{["users"]} (k/list-collections db)))
    (is (empty? (get-users db)))
    (save-user db daniel)
    (is (not (empty? (get-users db))))
    (component/stop system)))
