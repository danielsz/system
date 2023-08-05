(ns system.components.sente
  #?(:clj
     (:require [com.stuartsierra.component :as component]
               [reitit.ring :as r]
               [taoensso.sente :as sente]
               [ring.util.response :as ring]
               [clojure.tools.logging :as log])
     :cljs
     (:require [com.stuartsierra.component :as component]
               [taoensso.sente :as sente])))


#?(:clj
   (defn sente-routes [{{ring-ajax-post :ring-ajax-post ring-ajax-get-or-ws-handshake :ring-ajax-get-or-ws-handshake middleware :middleware} :sente}]
     (r/router ["/chsk" (cond-> {:get (fn [req] (try
                                                 (ring-ajax-get-or-ws-handshake req)
                                                 (catch clojure.lang.ExceptionInfo e
                                                   (log/error (ex-data e))
                                                   (-> (ring/response (.getMessage e))
                                                      (ring/status 400)))))
                                 :post (fn [req] (ring-ajax-post req))}
                          (some? middleware) (assoc :middleware middleware))])))


;; Sente supports both CLJ and CLJS as a server
(defrecord ChannelSocketServer [handler adapter options middleware]
  component/Lifecycle
  (start [component]
    (let [{:keys [ch-recv ajax-post-fn ajax-get-or-ws-handshake-fn send-fn connected-uids]} (sente/make-channel-socket-server! adapter options)]
      (assoc component
             :ch-chsk ch-recv
             :ring-ajax-post ajax-post-fn
             :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
             :chsk-send! send-fn
             :connected-uids connected-uids
             :router (sente/start-chsk-router! ch-recv (handler component)))))
  (stop [component]
    (when-let [stop-f (:router component)]
      (stop-f))
    component))

(defn new-channel-socket-server [& {:keys [handler adapter options middleware]}]
  (map->ChannelSocketServer {:handler handler
                             :adapter adapter
                             :options options
                             :middleware middleware}))

;; Sente does not support CLJ as a client yet
#?(:cljs
   (defrecord ChannelSocketClient [path csrf-token options]
     component/Lifecycle
     (start [component]
       (let [{:keys [chsk ch-recv send-fn state]} (sente/make-channel-socket-client! path csrf-token options)]
         (assoc component
                :chsk chsk
                :ch-chsk ch-recv ; ChannelSocket's receive channel
                :chsk-send! send-fn ; ChannelSocket's send API fn
                :chsk-state state)))
     (stop [component]
       (when-let [chsk (:chsk component)]
         (sente/chsk-disconnect! chsk))
       (when-let [stop-f (:router component)]
         (stop-f))
       (assoc component
              :router nil
              :chsk nil
              :ch-chsk nil
              :chsk-send! nil
              :chsk-state nil))))

#?(:cljs
   (defn new-channel-socket-client
     ([csrf-token]
      (new-channel-socket-client "/chsk" csrf-token))
     ([path csrf-token]
      (new-channel-socket-client path csrf-token {:type :auto}))
     ([path csrf-token options]
      (map->ChannelSocketClient {:path path
                                 :csrf-token csrf-token
                                 :options options}))))
