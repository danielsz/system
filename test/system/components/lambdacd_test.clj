(ns system.components.lambdacd-test
  (:require [system.components.lambdacd :refer [new-lambdacd-pipeline]]
            [lambdacd.steps.shell :as shell]
            [com.stuartsierra.component :as component]
            [clojure.test :refer [testing deftest is]]))


(defn some-step-that-echos-foo [args ctx]
  (shell/bash ctx "/" "echo foo"))

(defn some-step-that-echos-bar [args ctx]
  (shell/bash ctx "/" "echo bar"))

(def pipeline-def
  `(some-step-that-echos-foo
    some-step-that-echos-bar))

(def pipelines (new-lambdacd-pipeline {:demo pipeline-def}
                                      {:name "test pipeline"
                                       :home-dir "/tmp/"}))

(deftest pipeline-lifecycle
  (alter-var-root #'pipelines component/start)
  (is (seq? (:pipelines pipelines)) "Pipelines has been added to component")
  (doseq [pipeline (:pipelines pipelines)]
    (is (:context pipeline)) "Pipeline should have context")
  (alter-var-root #'pipelines component/stop))
