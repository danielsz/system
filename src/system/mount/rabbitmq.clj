(ns system.mount.rabbitmq
  (:require [system.mount :refer [defstate config]]
            [langohr.core      :as rmq]
            [langohr.channel   :as lch]))

(defstate rabbit-mq
  :start (let [conn (rmq/connect (get-in config [:rabbitmq :uri]))
               ch   (lch/open conn)]
           {:conn conn
            :ch ch})
  :stop (do (rmq/close (:ch rabbit-mq))
            (rmq/close (:conn rabbit-mq))))
