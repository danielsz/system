(ns system.components.watcher-test
  (:require [system.components.watcher :refer :all]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [testing deftest is]]))

(deftest watcher-lifecycle
  (is [:create "Hello"]

      (let [box (atom nil)
            result (promise)
            wtch   (new-watcher ["."]
                                (fn [kind file]
                                  (component/stop @box)
                                  (deliver result [kind (slurp file)])
                                  (.delete file))
                                {:filter [".watcher"]
                                 :recursive false})
            _  (reset! box (component/start wtch))]
        (spit "test.watcher" "Hello")
        @result)))
