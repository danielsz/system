(ns framework.application
  (:require 
   [com.stuartsierra.component :as component]
   (framework.components 
    [jetty :refer [new-web-server]]
    [repl-server :refer [new-repl-server]]
    [datomic :refer [new-datomic-db]]
    [mongo :refer [new-mongo-db]])
   [environ.core :refer [env]]))


(defn system-map []
  (if (bound? (ns-resolve 'framework.core 'system))
    (deref (ns-resolve 'framework.core 'system))
    (deref (ns-resolve 'user 'system))))

(defn dev-system []
  (component/system-map
   :datomic-db (new-datomic-db (env :db-url))
   :mongo-db (new-mongo-db)
   :web (new-web-server (Integer. (env :http-port)) (env :trace-headers))))


(defn prod-system []
  "Assembles and returns components for a production application"
  []
    (component/system-map
     :datomic-db (new-datomic-db (env :db-url))
     :mongo-db (new-mongo-db (env :mongo-url))
     :web (new-web-server (env :http-port) (env :trace-headers))
     :repl-server (new-repl-server (Integer. (env :repl-port)))))


