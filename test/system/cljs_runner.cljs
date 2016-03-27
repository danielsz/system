(ns system.cljs-runner
  (:require [cljs.test :as test]
            [doo.runner :refer-macros [doo-all-tests doo-tests]]
            [system.components.sente-client-test]))

(doo-tests 'system.components.sente-client-test)
