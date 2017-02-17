(ns system.components.core-async-pubsub-test
  (:require [system.components.core-async-pubsub :refer [new-pubsub]]
            [com.stuartsierra.component :as component]
            [clojure.core.async :as a :refer [<!! >!! >! <! chan thread timeout]]
            [clojure.test :refer [deftest is]]))

(def presidents ["George Washington"
                 "John Adams"
                 "Thomas Jefferson"
                 "James Madison"
                 "James Monroe"])

(def scientists ["Emilie du Chatelet"
                 "Caroline Herschel"
                 "Mary Anning"
                 "Mary Somerville"
                 "Maria Mitchell"])

(deftest pubsub
  (let [topic-fn :topic
        channel-fn (fn [_] (let [c (chan)]
                            (thread
                              (loop []
                                (>!! c {:topic :president :man (rand-nth presidents)})
                                (>!! c {:topic :scientist :woman (rand-nth scientists)})
                                (<!! (timeout 100))
                                (recur)))
                            c))
        pubsub (component/start (new-pubsub channel-fn topic-fn))
        president-subscriber (let [c (chan)
                                   o (a/take 5 c)]
                               (a/sub (:publication pubsub) :president c)
                               o)
        scientist-subscriber (let [c (chan)
                                   o (a/take 5 c)]
                               (a/sub (:publication pubsub) :scientist c)
                               o)]
    (dotimes [n 5]
      (let [{:keys [topic man]} (<!! president-subscriber)
            {:keys [topic woman]} (<!! scientist-subscriber)]
        (is (some #{man} presidents))
        (is (some #{woman} scientists))))
    (component/stop pubsub)))
