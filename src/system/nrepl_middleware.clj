(ns system.nrepl-middleware
  (:require  [nrepl.misc :refer [response-for]]
             [nrepl.middleware :refer [set-descriptor!]]
             [nrepl.middleware.session :refer [session]]
             [nrepl.transport :as t]
             [clojure.edn :as edn]
             [clojure.string :as str]
             [system.repl :refer [go reset set-init!]]))

(defn key-to-java-property [k]
  (->  k
       name
       (str/replace "-" ".")))

(defn set-profile-properties [xs]
  (doseq [[k v] xs
          :let [k (key-to-java-property k)]]
    (System/setProperty k v)))

(defn read-conf []
  (-> "meyvn.edn"
      slurp
      edn/read-string))

(defn system-init []
  (let [conf (read-conf)
        profile (get-in conf [:profiles :development])
        sys (get-in conf [:interactive :system :var])]
    (set-profile-properties profile)
    (require (symbol (namespace sys)))
    (set-init! (resolve sys))))

(defn wrap-meyvn
  [h]
  (fn [{:keys [op transport] :as msg}]
    (condp = op
      "meyvn-properties" (let [conf (read-conf)
                               profile (get-in conf [:profiles :development])]
                           (set-profile-properties profile)
                           (t/send transport (response-for msg :status :done :report {:count (count (keys profile))})))
      "meyvn-system-init" (do (system-init)
                            (t/send transport (response-for msg :status :done :value system.repl/system-sym)))
      "meyvn-system-go" (do (when-not (bound? (var system.repl/system-sym))
                              (system-init))
                            (go)
                            (t/send transport (response-for msg :status :done :value "OK")))
      "meyvn-system-reset" (do (when-not (bound? (var system.repl/system-sym))
                                 (system-init))
                               (reset)
                               (t/send transport (response-for msg :status :done :value "OK")))
      (h msg))))


(set-descriptor! #'wrap-meyvn
                 {:requires #{#'session}
                  :handles {"meyvn-system-init" {:doc "Sets the system var"}
                            "meyvn-system-go" {:doc "Starts the system"}
                            "meyvn-system-reset" {:doc "Resets the system"}
                            "meyvn-properties" {:doc "Sets properties for the environment"}}})

