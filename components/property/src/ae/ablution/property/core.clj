(ns ae.ablution.property.core
  (:require
   [ae.ablution.db.interface :as db]
   [ae.ablution.base.interface :as base]
   [ae.ablution.address.interface :as i.address]
   [clojure.string :as string]))

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
 '[ae.ablution.property.type :as-alias property.type]
 '[ae.ablution.address :as-alias address]
 '[ae.ablution.vehicle :as-alias vehicle])

(def property
  {:xt/id base/entity-id
   ::entity/type #{::entity.type/property}
   ::property/name string?
   ::property/type #{::property.type/holiday-home ::property.type/commercial
                     ::property.type/domestic ::property.type/window-clean
                     ::property.type/unknown ::property.type/enquiry}
   ::ablu/address i.address/address?})

(defn desc-property [{:keys [::property/name ::ablu/address]}]
  (string/join " / " [name (i.address/desc-address address)]))

(defn find-property [term]
  (db/find-entity term ::ablu/property))
