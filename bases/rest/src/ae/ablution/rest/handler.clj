(ns ae.ablution.rest.handler
  (:require [clojure.edn :as edn]
            [ae.ablution.supply.interface :as supp]
            [ae.ablution.base.interface :as base]
            [ae.ablution.user.interface :as u]
            [ae.ablution.schedule.interface :as sch]
            ;; [ae.ablution.article.interface :as article]
            ;; [ae.ablution.article.interface.spec :as article-spec]
            ;; [ae.ablution.comment.interface :as comment-comp]
            ;; [ae.ablution.comment.interface.spec :as comment-spec]
            ;; [ae.ablution.spec.interface :as spec]
            ;; [ae.ablution.profile.interface :as profile]
            ;; [ae.ablution.tag.interface :as tag]
            ;; [ae.ablution.user.interface :as user.i]
            ;; [ae.ablution.user.interface.spec :as user-spec]
            [clojure.spec.alpha :as s]))

#_{:clj-kondo/ignore [:unused-namespace]}
(require
 '[ae.ablution :as-alias ablu]
 '[ae.ablution.address :as-alias address]
 '[ae.ablution.address.county :as-alias county]
 '[ae.ablution.agent :as-alias ablu.agent]
 '[ae.ablution.confirm :as-alias confirm]
 '[ae.ablution.customer :as-alias customer]
 '[ae.ablution.entity :as-alias entity]
 '[ae.ablution.entity.id :as-alias entity.id]
 '[ae.ablution.entity.type :as-alias entity.type]
 '[ae.ablution.employee :as-alias employee]
 '[ae.ablution.laundry :as-alias laundry]
 '[ae.ablution.laundry.batch :as-alias batch]
 '[ae.ablution.laundry.pile :as-alias pile]
 '[ae.ablution.person :as-alias person]
 '[ae.ablution.person.contact :as-alias contact]
 '[ae.ablution.person.title :as-alias person.title]
 '[ae.ablution.property :as-alias property]
 '[ae.ablution.schedule :as-alias schedule]
 '[ae.ablution.schedule.job :as-alias job]
 '[ae.ablution.supply :as-alias supply]
 '[ae.ablution.supply.wp :as-alias supply.wp]
 '[ae.ablution.user :as-alias user]
 '[ae.ablution.vehicle :as-alias vehicle])

(defn- parse-query-param [param]
  (if (string? param)
    (try
      (edn/read-string param)
      (catch Exception _
        param))
    param))

(defn- handle
  ([status body]
   {:status (or status 404)
    :body   body})
  ([status]
   (handle status nil)))

(defn options [_]
  (handle 200))

(defn health [_]
  (handle 200 {:environment (base/env :environment)}))

(defn other [_]
  (handle 404 {:errors {:other ["Route not found."]}}))

(defn jobs [req]
  (let [[ok? res] (sch/find-all-jobs-api)]
    (handle (if ok? 200 404) res)))

(defn add-jobs [req]
  nil)

(defn supplies [req]
  (let [auth-user (-> req ::user/auth)
        prop-ids (-> req ::property/ids)
        [ok? res] #_(supp/pull-all-api auth-user prop-ids)
        (supp/pull-all-and-api auth-user prop-ids)]
    (handle (if ok? 200 404) res)))

(defn add-supplies [req]
  (let [supplies (-> req :params ::ablu/supplies)
        [ok? res] (supp/add!-api supplies)]
    (handle (if ok? 200 404) res)))
