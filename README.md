<div id="table-of-contents">
<h2>Table of Contents</h2>
<div id="text-table-of-contents">
<ul>
<li><a href="#sec-1">1. System</a>
<ul>
<li><a href="#sec-1-1">1.1. What is it?</a></li>
<li><a href="#sec-1-2">1.2. Is it good?</a></li>
<li><a href="#sec-1-3">1.3. Installation</a></li>
<li><a href="#sec-1-4">1.4. Usage</a></li>
<li><a href="#sec-1-5">1.5. Contributing</a></li>
</ul>
</li>
</ul>
</div>
</div>

# System<a id="sec-1" name="sec-1"></a>

## What is it?<a id="sec-1-1" name="sec-1-1"></a>

System owes to [component](https://github.com/stuartsierra/component) both in spirit and in name. Component gave you the ability to implement the reload pattern as promoted by Stuart Sierra, System gives you a set of readymade components. This set is open to contributions from the community and so is expected to expand. Currently includes: 
-   Jetty (HTTP server)
-   Http-kit (Async HTTP server)
-   Datomic (Immutable database)
-   Monger (MongoDB client)
-   Sente (Websockets/Ajax communications library)
-   nREPL (Clojure network REPL )
-   Langohr (RabbitMQ client)

## Is it good?<a id="sec-1-2" name="sec-1-2"></a>

[Yes](https://news.ycombinator.com/item?id%3D3067434).

## Installation<a id="sec-1-3" name="sec-1-3"></a>

Add the following to the Leiningen dependencies in project.clj. 

    [org.danielsz/system "0.1.0-SNAPSHOT"]

## Usage<a id="sec-1-4" name="sec-1-4"></a>

First, assemble your application. 

    (ns my-app.systems
      (:require 
       [com.stuartsierra.component :as component]
       (system.components 
        [jetty :refer [new-web-server]]
        [repl-server :refer [new-repl-server]]
        [datomic :refer [new-datomic-db]]
        [mongo :refer [new-mongo-db]])
       [environ.core :refer [env]]))
    
    
    (defn dev-system []
      (component/system-map
       :datomic-db (new-datomic-db (env :db-url))
       :mongo-db (new-mongo-db)
       :web (new-web-server (Integer. (env :http-port)) handler)))
    
    
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

Or, if you don’t want to have a handler on your application:

    (defn -main 
      []
      "Start the application"
      (alter-var-root #'system (fn [_] (component/start (prod-system)))))

## Contributing<a id="sec-1-5" name="sec-1-5"></a>

Please fork and issue a pull request to add more components. Please don't forget to include tests. You can refer to the existing ones to get started.
