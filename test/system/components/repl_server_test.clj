(ns system.components.repl-server-test
  (:require
   [system.components.repl-server :refer [new-repl-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is run-tests]]
   [clojure.tools.nrepl :as repl]))

(defn connect [code]
  (with-open [conn (repl/connect :port 8082)]
               (-> (repl/client conn 1000)
                   (repl/message {:op :eval :code code})
                   repl/response-values)))

(deftest repl-server-availability
  (let [server (component/start (new-repl-server :port 8082))]
    (is (:server server) "REPL server has been added to component")
    (is (= [2] (connect "(+ 1 1)")) "REPL functions normally")
    (component/stop server)))

(deftest repl-server-with-cider
  (let [server (component/start (new-repl-server :port 8082 :with-cider true))]
    (is (:server server) "REPL server has been added to component")
    (component/stop server)))

(deftest repl-server-bind
  (let [server (component/start (new-repl-server :port 8082 :bind "0.0.0.0"))]
    (is (:server server) "REPL server has been added to component")
    (is (= [2] (connect "(+ 1 1)")) "REPL functions normally")
    (component/stop server)))

(deftest repl-server-wrong-bind
  (is (thrown-with-msg? java.net.BindException #"Cannot assign requested address"
         (component/start (new-repl-server :port 8082 :bind "1.0.0.0")))))
