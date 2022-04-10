(ns ae.ablution.rest.main
  (:require
   ;; [ae.ablution.log.interface :as log]
   [ae.ablution.rest.api :as api]
   [clojure.tools.logging :as log]
   [ring.adapter.jetty :as jetty])
  (:gen-class))

(def ^:private server-ref (atom nil))

(defn start!
  [port]
  (if-let [_server @server-ref]
    (log/warn "Server already running? (stop!) it first.")
    (do (log/info "Starting server on port: " port)
        (api/init)
        (reset! server-ref
                (jetty/run-jetty api/app {:port port :join? false})))))

(defn stop! []
  (if-let [server @server-ref]
    (do (api/destroy)
        (.stop server)
        (reset! server-ref nil))
    (log/warn "No server")))

(defn -main [& _args]
  (-> "port" System/getenv (or "6003") Integer/valueOf start!))
