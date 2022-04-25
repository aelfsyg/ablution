(ns ae.ablution.rest.api
  (:require [ae.ablution.db.interface :as db]
            [ae.ablution.rest.handler :as h]
            [ae.ablution.rest.middleware :as m]
            [clojure.tools.logging :as log]
            [clojure.pprint :as pp]
            [compojure.core :refer [routes wrap-routes defroutes GET POST PUT DELETE ANY OPTIONS]]
            [ring.logger.timbre :as logger]
            [ring.middleware.json :as js]
            [ring.middleware.edn :as edn]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.multipart-params :as mp]
            [ring.middleware.nested-params :as np]
            [ring.middleware.params :as pr]))

(defroutes public-routes
  (OPTIONS "/**" [] h/options)
  (GET "/api/health" [] h/health)
  (GET "/api/supplies" [] h/supplies)
  (POST "/api/supplies" [] h/add-supplies)
  (GET "/api/jobs" [] h/jobs))

(defroutes other-routes
  (ANY     "/**"                              [] h/other))

(def ^:private app-routes
  (routes
   (-> public-routes
       (wrap-routes m/wrap-auth-user))
   other-routes))

(defn generate-response [data & [status]] nil)

(defn edn-response [response]
  (-> response
      (update :body #(with-out-str (pp/pprint %)))
      (assoc-in [:headers "Content-Type"] "application/edn")))

(defn wrap-edn-response [handler]
  (fn [request]
    (edn-response (handler request))))

(def app
  (-> app-routes
      logger/wrap-with-logger
      ;; kp/wrap-keyword-params
      ;; pr/wrap-params
      ;; mp/wrap-multipart-params
      ;; js/wrap-json-params
      wrap-edn-response
      edn/wrap-edn-params
      ;; np/wrap-nested-params
      m/wrap-exceptions
      m/wrap-cors))

(defn init []
  (try (log/info "Initialized server.")
       (catch Exception e
         (log/error e "Could not start server."))))

(defn destroy []
  (log/info "Destroyed server."))
