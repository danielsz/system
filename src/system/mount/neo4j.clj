(ns system.mount.neo4j
  (:require [system.mount :refer (config defstate)]
            [clojurewerkz.neocons.rest :as n]))

(defstate neo4j-db
  :start (let [{:keys [uri user passwd]} (get config :neo4j)]
           (if user
             (n/connect uri user passwd)
             (n/connect uri))))
