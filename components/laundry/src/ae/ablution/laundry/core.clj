(ns ae.ablution.laundry.core
  (:require
   [spec-tools.data-spec :as ds]
   [ae.ablution.base.core :as core]))

#_{:clj-kondo/ignore [:unused-namespace]}
(require
 '[ae.ablution :as-alias ablu]
 '[ae.ablution.agent :as-alias ablu.agent]
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
 '[ae.ablution.address :as-alias address]
 '[ae.ablution.vehicle :as-alias vehicle])

(def batch-blair-2022-03-21
  {:xt/id (core/entity-id ::laundry/batch)
   ::entity/type ::entity.type/batch
   ::ablu/property ::entity.id/property-b380358ae43b
   ::laundry/arrival-date #inst "2022-03-21"
   (ds/opt ::laundry/deadline) inst?
   ::laundry/to-wash? true
   ::laundry/to-iron? true
   ::laundry/piles
   [::pile/darks
    {::pile/type ::pile/darks
     ::pile/state ::pile/complete}]})
