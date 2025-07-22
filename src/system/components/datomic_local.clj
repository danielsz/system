(ns system.components.datomic-local
  (:require [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [datomic.client.api :as d]
            [clojure.edn :as edn]))

(defn test-storage-directory []
  (let [conf (io/file (str (System/getProperty "user.home") "/.datomic/local.edn"))]
    (if (.exists conf)
      (let [edn (-> conf
                   slurp
                   edn/read-string)
            storage-dir (io/file (:storage-dir edn))]
        (if (.exists storage-dir)
          (println "Datomic local:" storage-dir)
          (throw (ex-info "Storage directory for Datomic local does not exist!" {:value storage-dir}))))
      (throw (ex-info "Storage directory for Datomic local is not set up!" {:value "~/.datomic/local.edn"})))))

(defrecord DatomicLocal [cfg db init-fn]
  component/Lifecycle
  (start [component]
    (test-storage-directory)
    (let [client (d/client cfg)
          _ (d/create-database client db)
          conn (d/connect client db)]
      (when init-fn
        (init-fn conn))
      (assoc component :client client :conn conn)))
  (stop [component]
    (dissoc component :client :conn)))

(defn new-datomic-local [& {:keys [cfg db init-fn]}]
  (map->DatomicLocal {:cfg cfg :db db :init-fn init-fn}))
