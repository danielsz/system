(ns system.components.postgres-test
  (:use clojure.test)
  (:require [system.components.postgres :as p]
            [clojure.java.jdbc :as jdbc]
            [com.stuartsierra.component :as component]))

;; Postgres can't be run in-memory, so I'm not sure how to test...
