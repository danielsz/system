(ns system.components.sente
  #?(:clj
     (:require [com.stuartsierra.component :as component]
               [compojure.core :refer [routes GET POST]]
               [taoensso.sente :as sente]
               [ring.util.response :as ring]
               [clojure.tools.logging :as log])

     :cljs
     (:require [com.stuartsierra.component :as component]
               [taoensso.sente :as sente])))
#?(:clj
   (defn sente-routes [{{ring-ajax-post :ring-ajax-post ring-ajax-get-or-ws-handshake :ring-ajax-get-or-ws-handshake} :sente}]
     (routes
      (GET  "/chsk" req (try
                          (ring-ajax-get-or-ws-handshake req)
                          (catch clojure.lang.ExceptionInfo e
                            (log/error (ex-data e))
                            (-> (ring/response "")
                                (ring/status 400)))))
      (POST "/chsk" req (ring-ajax-post                req)))))

;; Sente supports both CLJ and CLJS as a server
(defrecord ChannelSocketServer [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! connected-uids router web-server-adapter handler options]
  component/Lifecycle
  (start [component]
    (let [handler (get-in component [:sente-handler :handler] handler)
          {:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]}
          (sente/make-channel-socket-server! web-server-adapter options)
          component (assoc component
                           :ring-ajax-post ajax-post-fn
                           :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
                           :ch-chsk ch-recv
                           :chsk-send! send-fn
                           :connected-uids connected-uids)]
      (assoc component
             :router (sente/start-chsk-router!
                      ch-recv (if (:wrap-component? options)
                                (handler component)
                                handler)))))
  (stop [component]
    (if-let [stop-f router]
      (assoc component :router (stop-f))
      component)))

(defn new-channel-socket-server
  ([web-server-adapter]
   (new-channel-socket-server nil web-server-adapter {}))
  ([event-msg-handler web-server-adapter]
   (new-channel-socket-server event-msg-handler web-server-adapter {}))
  ([event-msg-handler web-server-adapter options]
   (map->ChannelSocketServer {:web-server-adapter web-server-adapter
                              :handler event-msg-handler
                              :options options})))

;; Old fn name
(def new-channel-sockets new-channel-socket-server)

;; Sente does not support CLJ as a client yet
#?(:cljs
   (defrecord ChannelSocketClient [chsk ch-chsk chsk-send! chsk-state path csrf-token router handler options]
     component/Lifecycle
     (start [component]
       (let [handler (get-in component [:sente-handler :handler] handler)
             {:keys [chsk ch-recv send-fn state]} (sente/make-channel-socket-client! path csrf-token options)
             component (assoc component
                              :chsk chsk
                              :ch-chsk ch-recv ; ChannelSocket's receive channel
                              :chsk-send! send-fn ; ChannelSocket's send API fn
                              :chsk-state state)]
         (if handler
           (assoc component :router (sente/start-chsk-router! ch-recv handler))
           component)))
     (stop [component]
       (when chsk
         (sente/chsk-disconnect! chsk))
       (when-let [stop-f router]
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
      (new-channel-socket-client nil "/chsk" csrf-token {}))
     ([path csrf-token]
      (new-channel-socket-client nil path csrf-token {}))
     ([event-msg-handler path csrf-token]
      (new-channel-socket-client event-msg-handler path csrf-token {}))
     ([event-msg-handler path csrf-token options]
      (map->ChannelSocketClient {:path    path
                                 :csrf-token csrf-token
                                 :handler event-msg-handler
                                 :options options}))))
