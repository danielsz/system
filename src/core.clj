(ns framework.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            (framework 
             [application :refer [dev-system prod-system system-map]])
            [reloaded.repl :refer [system init start stop go reset]]))

"A Var containing an object representing the application under
  production. Unbound so that we call (system-map) at runtime"


(defn -main 
  []
  "Start the application"
  (alter-var-root #'system (fn [_] (component/start (prod-system))))) 


