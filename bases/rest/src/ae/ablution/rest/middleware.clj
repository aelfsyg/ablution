(ns ae.ablution.rest.middleware
  (:require [ae.ablution.base.interface :as base]
            [ae.ablution.user.interface :as u]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn wrap-auth-user [handler]
  (fn [req]
    (let [authorization (get (:headers req) "authorization")
          token (when authorization (-> (str/split authorization #" ") last))]
      (if-not (str/blank? token)
        (let [[ok? user] (u/user-by-token token)]
          (if ok?
            (handler (assoc req :auth-user (:user user)))
            (handler req)))
        (handler req)))))

(defn wrap-exceptions [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (let [message (str "An unknown exception occurred.")]
          (log/error e message)
          {:status 500
           :body   {:errors {:other [message]}}})))))

(defn create-access-control-header [origin]
  (let [allowed-origins (or (base/env :allowed-origins) "")
        origins (str/split allowed-origins #",")
        allowed-origin (some #{origin} origins)]
    {"Access-Control-Allow-Origin" origin ; allowed-origin ; *
     "Access-Control-Allow-Methods" "POST, GET, PUT, OPTIONS, DELETE"
     "Access-Control-Max-Age"       "3600"
     "Access-Control-Allow-Headers" "Authorization, Content-Type, x-requested-with"}))

(defn wrap-cors [handler]
  (fn [req]
    (let [origin (get (:headers req) "origin")
          response (handler req)]
      (->> origin
           create-access-control-header
           (merge (:headers response))
           (assoc response :headers)))))
