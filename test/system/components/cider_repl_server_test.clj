(ns system.components.cider-repl-server-test
  (:require
   [system.components.cider-repl-server :refer [new-cider-repl-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is run-tests]]
   [clojure.tools.nrepl :as repl]))


(defn connect [code]
  (with-open [conn (repl/connect :port 8083)]
    (-> (repl/client conn 2000)
        (repl/message {:op :eval :code code})
        repl/response-values)))

(deftest repl-server-availability
  (let [server (component/start (new-cider-repl-server 8083))]
    (is (:server server) "REPL server has been added to component")
    (is (= [2] (connect "(+ 1 1)")) "REPL functions normally")
    (component/stop server)))

(deftest repl-server-bind
  (let [server (component/start (new-cider-repl-server 8083 "0.0.0.0"))]
    (is (:server server) "REPL server has been added to component")
    (is (= [2] (connect "(+ 1 1)")) "REPL functions normally")
    (component/stop server)))
