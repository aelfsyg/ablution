(ns ae.ablution.property.core)

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

(def animal-farm
  {:xt/id :ae.ablution.entity.id/property-b380358ae43b
   ::entity/type ::entity.type/property
   ::ablu/address
   {::address/first-line "Manor Farm"
    ::address/second-line ""
    ::address/postal-town "Polegate"
    ::address/county "East Sussex"
    ::address/postcode "BN26 1OI"
    ::address/country "UK"}})
