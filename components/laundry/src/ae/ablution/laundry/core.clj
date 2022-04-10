(ns ae.ablution.laundry.core
  (:require
   [spec-tools.data-spec :as ds]
   [ae.ablution.db.interface :as db]
   [ae.ablution.base.interface :as base]
   [xtdb.api :as xt]))

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

(defn find-all-batches []
  (db/q '{:find [id]
          :where [[batch ::entity/type ::entity.type/batch]
                  [batch :xt/id id]]}))

(defn find-batches-without-deadline []
  (db/q '{:find [(pull batch [:xt/id ::laundry/arrival-date]) prop-name cust-name]
          :where [[batch ::ablu/property prop]
                  [cust ::ablu/properties prop]
                  [(get-attr prop ::property/name nil) [prop-name]]
                  [(get-attr cust ::customer/name nil) [cust-name]]
                  (not [batch ::laundry/deadline])]}))

(defn piles->map [piles]
  (group-by ::pile/type piles))

(defn add-new-batch! [batch-id prop-id date piles]
  {:xt/id batch-id
   ::entity/type ::entity.type/batch
   ::ablu/property prop-id
   ::laundry/arrival-date date
   ::laundry/to-iron? true
   ::laundry/to-wash? true
   ::laundry/piles (piles->map piles)})

(defn del-all-batches! []
  (for [[batch] (find-all-batches)]
    (db/del! batch)))

(defn record-laundry-arrival [date-time property bags] nil)
(defn ingest-batch [batch] nil)
(defn seperate [batch buckets])
(defn add-items
  ([batch items] nil)
  ([batch bucket items] nil))
(defn ingest-batch [date-time property bags item-list] nil)
(defn add-batch-deadline [batch date] nil)
(defn wash-batch [batch] nil)
(defn rewash-batch [batch] nil)
(defn iron-batch [batch] nil)
(defn dispatch-batch [batch] nil)
