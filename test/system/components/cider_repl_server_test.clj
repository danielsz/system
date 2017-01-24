(ns system.components.cider-repl-server-test
  (:require
   [system.components.cider-repl-server :refer [new-cider-repl-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is run-tests]]
   [clojure.tools.nrepl :as repl]))

(def repl-server (new-cider-repl-server 8082))
(def repl-server-with-bind-address (new-cider-repl-server 8082 "0.0.0.0"))

(defn connect [code]
  (with-open [conn (repl/connect :port 8082)]
               (-> (repl/client conn 1000)
                   (repl/message {:op :eval :code code})
                   repl/response-values)))

(deftest repl-server-availability
  (alter-var-root #'repl-server component/start)
  (is (:server repl-server) "REPL server has been added to component")
  (is (= [2] (connect "(+ 1 1)")) "REPL functions normally")
  (alter-var-root #'repl-server component/stop))

(deftest repl-server-bind
  (alter-var-root #'repl-server-with-bind-address component/start)
  (is (= [2] (connect "(+ 1 1)")) "REPL functions normally")
  (alter-var-root #'repl-server-with-bind-address component/stop))
