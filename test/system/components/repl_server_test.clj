(ns system.components.repl-server-test
  (:require
   [system.components.repl-server :refer [new-repl-server]]
   system.monitoring.repl-server
   [com.stuartsierra.component :as component]
   [system.monitoring.monitoring :as monitoring]
   [clojure.test :refer [deftest is run-tests]]
   [clojure.tools.nrepl :as repl]))

(def repl-server (new-repl-server 8082))

(deftest repl-server-availability
  (alter-var-root #'repl-server component/start)
  (is (:server repl-server) "REPL server has been added to component")
  (is (= [2] (with-open [conn (repl/connect :port 8082)]
               (-> (repl/client conn 1000)
                   (repl/message {:op :eval :code "(+ 1 1)"})
                   repl/response-values)))
      "REPL functions normally")
  (alter-var-root #'repl-server component/stop))

(deftest repl-server-monitoring-status
  (alter-var-root #'repl-server component/start)
  (is (= (monitoring/status repl-server) :running))
  (alter-var-root #'repl-server component/stop)
  (is (= (monitoring/status repl-server) :down)))
