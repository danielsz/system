(ns system.components.ldap
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log])
  (:import [com.unboundid.ldap.sdk LDAPConnection SimpleBindRequest]))

(defrecord Ldap [host port bind-dn pass]
  component/Lifecycle
  (start [component]
    (let [conn (LDAPConnection. host port)
          bind-request (SimpleBindRequest. bind-dn pass)
          bind-result (.bind conn bind-request)]
      (when (.hasResponseControl bind-result) (log/warn "Server has response controls to share."))
      (log/debug (.getResultString bind-result))
      (assoc component :conn conn)))
  (stop [component]
    (.close (:conn component) )
    (assoc component :conn nil)))

(defn new-ldap [& {:keys [host port bind-dn pass] :or {host "localhost" port 389}}]
  (map->Ldap {:host host :port port :bind-dn bind-dn :pass pass}))
