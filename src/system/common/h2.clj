(ns system.common.h2)

(def DEFAULT-DB-SPEC
  {:classname   "org.h2.Driver" ; must be in classpath
   :subprotocol "h2"
   :subname "~/test"
   :user     "sa"
   :password ""
   :host "127.0.0.1"
   :AUTO_SERVER "TRUE"
   :DB_CLOSE_DELAY "-1"})

(def DEFAULT-MEM-SPEC
  {:classname   "org.h2.Driver" ; must be in classpath
   :subprotocol "h2"
   :subname "mem:test"
   :user     "sa"
   :password ""
   :DB_CLOSE_DELAY "-1"})
