(ns livewire.server
  (:require
    [compojure.core :refer [GET ANY defroutes]]
    [clojure.tools.logging :as log]
    [cheshire.core :as json]
    [org.httpkit.server :refer [send! with-channel on-close on-receive]]))

(ns livewire.server
  (:import
    [com.impossibl.postgres.jdbc PGDataSource]
    [com.impossibl.postgres.api.jdbc PGNotificationListener]))

(def datasource (doto (PGDataSource.)
                      (.setHost     "localhost") ; todo move into
                      (.setPort     5432)
                      (.setDatabase "josh")
                      (.setUser     "josh")
                      (.setPassword "")))

(defonce channels (atom #{}))

; add new websocket client connection to map
(defn connect! [channel]
  (swap! channels conj channel))

; remove disconnected websocket client from map
(defn disconnect! [channel status]
  (swap! channels disj channel))

; broadcast server message to clients
(defn broadcast-out [payload]
  (let [msg (json/encode {:type "broadcast" :payload payload})]
    (run! #(send! % msg) @channels)))

; broadcast client message and respond
(defn broadcast [ch payload]
  (let [msg (json/encode {:type "broadcast" :payload payload})]
    (run! #(send! % msg) @channels))
  (send! ch (json/encode {:type "broadcastResult" :payload payload})))

; respond to echo client message
(defn echo [ch payload]
  (send! ch (json/encode {:type "echo" :payload payload})))

; respond to unknown type of client message
(defn unknown-type-response [ch _]
  (send! ch (json/encode {:type "error" :payload "ERROR: unknown message type"})))

; dispatch handler for incoming client message
(defn dispatch [ch msg]
  (let [parsed (json/decode msg)]
    ((case (get parsed "type")
        "echo" echo
        "broadcast" broadcast
        unknown-type-response)
      ch (get parsed "payload"))))

; handle incoming websocket client
(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel #(disconnect! channel %))
    (on-receive channel #(dispatch channel %))))

; create a listener that triggers when a message is received
(def listener
  (reify PGNotificationListener
    (^void notification [this ^int processId ^String channelName ^String payload]
           (broadcast-out payload)
           (println "msg: " payload) )))


; setup a connection with the listener
(def connection
  (doto (.getConnection datasource)
        (.addNotificationListener listener)))


; begin listening to a channel
(doto (.createStatement connection)
      (.execute "LISTEN mymessages;")
      (.close))

(defn not-found []
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "Not Found"})

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Livewire"})

(defroutes app
  (GET "/" []
    (splash))
  (GET "/ws" request (ws-handler request))
  (ANY "*" []
    (not-found)))
