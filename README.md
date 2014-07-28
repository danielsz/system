<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. Framework</a>
<ul>
<li><a href="#sec-1-1">1.1. Is it good?</a></li>
<li><a href="#sec-1-2">1.2. Installation</a></li>
<li><a href="#sec-1-3">1.3. Usage</a></li>
</ul>
</li>
</ul>
</div>
</div>

# Framework<a id="sec-1" name="sec-1"></a>

## Is it good?<a id="sec-1-1" name="sec-1-1"></a>

Yes.

## Installation<a id="sec-1-2" name="sec-1-2"></a>

## Usage<a id="sec-1-3" name="sec-1-3"></a>

First, assemble your application. 

    (ns my-app.systems
      (:require 
       [com.stuartsierra.component :as component]
       (framework.components 
        [jetty :refer [new-web-server]]
        [repl-server :refer [new-repl-server]]
        [datomic :refer [new-datomic-db]]
        [mongo :refer [new-mongo-db]])
       [environ.core :refer [env]]))
    
    
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

Then, in user.clj:

    (ns user
      (:require [reloaded.repl :refer [system init start stop go reset]]
                [my-app.systems :refer [dev-system]]))
    
    (reloaded.repl/set-init! dev-system)

And for production, in core.clj:

    (ns my-app.core
      (:gen-class)
      (:require [reloaded.repl :refer [system init start stop go reset]]
                [my-app.systems :refer [prod-system]]))
    
    (defn -main 
      []
      "Start the application"
      (reloaded.repl/set-init! prod-system)
      (go))
