(ns system.mount
  (:import [java.io FileNotFoundException]))

;;; Declare defstate macro based on available mount library.

(def ^:private defstate-sym
  (try
    (require 'mount.core) 'mount.core/defstate
    (catch FileNotFoundException e1
      (try
        (require 'mount.lite) 'mount.lite/defstate
        (catch FileNotFoundException e2
          nil)))))

(defmacro defstate [& args]
  (when defstate-sym
    `(~defstate-sym ~@args)))

;;; Configuration used by other defstates.

;;---TODO: use ENV vars here, using something like environ?

(def example-config
  {:adi                {:meta   {:uri             "datomic:mem//test"
                                 :reset?          true
                                 :install-schema? false}
                        :schema nil}
   :aleph              {:handler identity
                        :options {:port 8080}}
   :elasticsearch      {:addresses ["localhost:9200"]
                        :settings  nil}
   :etsy               {:token  "12345667890"
                        :secret "abcdefghijk"}
   :h2                 {:spec {:classname   "org.h2.Driver"
                               :subprotocol "h2"
                               :subname     "mem:test"
                               :user        "sa"
                               :password    ""}}
   :http-kit           {:handler identity
                        :options {:port 8090}}
   :immutant-web       {:handler identity
                        :options {:port 8100
                                  :host "0.0.0.0"}}
   :jdbc               {:spec {:classname   "org.h2.Driver"
                               :subprotocol "h2"
                               :subname     "mem:test"
                               :user        "sa"
                               :password    ""}}
   :jetty              {:handler identity
                        :options {:port  8110
                                  :join? false}}
   :mongo              {:uri "localhost"}
   :neo4j              {:uri "localhost"}
   :nrepl-server       {:port 13337}
   :postgres           {:spec {:classname   "org.postgres.Driver"
                               :subprotocol "postgres"
                               :subname     "test"
                               :user        "root"
                               :password    ""}}
   :rabbitmq           {:uri "localhost:9876"}
   :sente              {:server-adapter 'jetty
                        :handler        identity
                        :options        {:wrap-component false}}
   :scheduled-executor {:n-threads (.availableProcessors (Runtime/getRuntime))}})

(defstate config :start example-config)
