(ns system.components.repl-server-test
  (:require
   [system.components.repl-server :refer [new-repl-server]]
   [com.stuartsierra.component :as component]
   [clojure.test :refer [deftest is run-tests]]
   [clojure.tools.nrepl :as repl]))

(def repl-server (new-repl-server 8082))
(def repl-server-with-bind-address (new-repl-server 8082 "0.0.0.0"))
(def repl-server-with-wrong-bind-address (new-repl-server 8082 "1.0.0.0"))

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

(deftest repl-server-wrong-bind
  (is (= "Cannot assign requested address (Bind failed)"
         (try
           (alter-var-root #'repl-server-with-wrong-bind-address component/start)
           (catch java.net.BindException e (.getMessage e))))))
