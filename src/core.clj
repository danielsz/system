(ns framework.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            (framework 
             [application :refer [prod-system]])))

"A Var containing an object representing the application under
  production. Unbound so that we call (system-map) at runtime"
(def system)

(defn -main 
  []
  "Start the application"
  (alter-var-root #'system (fn [_] (component/start (prod-system))))) 


