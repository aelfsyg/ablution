(ns ae.ablution.customer.core
  (:require [xtdb.api :as xt]
            [ae.ablution.db.core :as db]))

#_{:clj-kondo/ignore [:unused-namespace]}
(require
 '[ae.ablution :as-alias ablu]
 '[ae.ablution.address :as-alias address]
 '[ae.ablution.address.county :as-alias county]
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
 '[ae.ablution.vehicle :as-alias vehicle])

(defn put-customer! [customer] nil)

(def orwell-capital
  {:xt/id :ae.ablution.entity.id/customer-e78c5dd157a8
   ::entity/type ::entity.type/customer
   ::ablu/properties [:ae.ablution.entity.id/property-b380358ae43b]
   ::ablu/persons [::entity.id/person-8891cec18a2c]})

(xt/q (xt/db db/node)
      '{:find [(pull cust [* {::ablu/properties [*]} {::ablu/persons [*]}])]
        :where [[cust ::ablu/persons person]
                [person ::person/name name]
                [cust ::ablu/properties prop]
                [prop ::ablu/address add]
                [(get-in add [::address/first-line]) v]
                [(= v "Manor Farm")]]})
