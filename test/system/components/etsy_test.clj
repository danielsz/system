(ns system.components.etsy-test
  (:require [system.components.etsy :refer [new-etsy-client]]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [deftest is]]))

(def etsy-client (new-etsy-client "123" "1234"))

(deftest etsy-client-test
  (alter-var-root #'etsy-client component/start)
  (is (:client etsy-client) "a client is present")
  (alter-var-root #'etsy-client component/stop))

