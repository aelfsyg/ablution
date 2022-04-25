(ns dev.ae
  (:require [ae.ablution.address.core :as addr]
            [ae.ablution.base.core :as base]
            [ae.ablution.customer.core :as cust]
            [ae.ablution.db.core :as db]
            [ae.ablution.db.indexer :as idx]
            [ae.ablution.person.core :as pers]
            [ae.ablution.property.core :as prop]
            [ae.ablution.schedule.core :as sch]
            [ae.ablution.supply.core :as supp]
            [ae.ablution.rest.main :as main]
            [clojure.java.io :as io]
            [spec-tools.data-spec :as ds]
            [xtdb.api :as xt]
            [xtdb.codec :as codec]))

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
 '[ae.ablution.vehicle :as-alias vehicle])

(defn start! [port]
  (main/start! port))

(defn stop! []
  (main/stop!))

(comment
  (start! 6003)
  (stop!))

(def twentytwo-portobello
  {::address/first-line "22 Portobello Road"
   ::address/second-line ""
   ::address/postal-town "London"
   ::address/county "London"
   ::address/postcode "W11 3DH"
   ::address/country "UK"})

(def eric-arthur-blair
  {:xt/id ::entity.id/person-8891cec18a2c
   ::entity/type ::entity.type/person
   ::entity/type-exp ::person
   ::person/title ::person.title/mr
   ::person/short-name "Mr Blair"
   ::person/full-name "Eric Arthur Blair"
   ::person/name {::person/first-name "Eric"
                  ::person/middle-names ["Arthur"]
                  ::person/last-name "Blair"}
   ::person/contact-address twentytwo-portobello
   ::person/contacts {::contact/emails ["orwell@ingsoc.org"]
                      ::contact/phones ["07777 777 777"]}})

(def animal-farm
  {:xt/id :ae.ablution.entity.id/property-b380358ae43b
   ::entity/type ::entity.type/property
   ::property/name "Animal Farm"
   ::property/null? false
   ;; ::address/full-address "Animal Farm / Manor Farm /  / Polegate / East Sussex / BN26 1OI / UK"
   ::ablu/inactive? true
   ::ablu/address {::address/first-line "Manor Farm"   ; this should be roughly unique
                   ::address/second-line ""
                   ::address/postal-town "Polegate"
                   ::address/county "East Sussex"
                   ::address/postcode "BN26 1OI"
                   ::address/country "UK"}})

(def room-101
  {:xt/id :ae.ablution.entity.id/property-e3a788c31380
   ::entity/type ::entity.type/property
   ::property/name "Room 101"
   ::property/null? false
   ::ablu/inactive? true
   ;; ::address/full-address "Animal Farm / Manor Farm /  / Polegate / East Sussex / BN26 1OI / UK"
   ::ablu/address {::address/first-line "Room 101"
                   ::address/second-line "Ministry of Truth"
                   ::address/postal-town "London"
                   ::address/county "Landing Strip One"
                   ::address/postcode "SW1 1OI"
                   ::address/country "Oceania"}})

(def orwell-capital
  {:xt/id :ae.ablution.entity.id/customer-e78c5dd157a8
   ::entity/id :ae.ablution.entity.id/customer-e78c5dd157a8
   ::entity/type ::entity.type/customer
   ::customer/name "Orwell Capital"
   ::ablu/inactive? true
   ::ablu/properties [:ae.ablution.entity.id/property-e3a788c31380
                      :ae.ablution.entity.id/property-b380358ae43b]
   ::ablu/persons [::entity.id/person-8891cec18a2c]})

(def batch-blair-2022-03-21
  {:xt/id :ae.ablution.entity.id/batch-2c8138ca225d
   ::entity/type ::entity.type/batch
   ::ablu/property ::entity.id/property-b380358ae43b
   ::laundry/arrival-date #inst "2022-03-21"
   ::laundry/deadline #inst "2022-03-25"
   ::laundry/to-wash? true
   ::laundry/to-iron? true
   ::laundry/piles
   {::pile/darks
    {::pile/type ::pile/darks
     ::pile/state ::pile/complete}}})

(def batch-blair-2022-03-22
  {:xt/id :ae.ablution.entity.id/batch-33013299df21
   ::entity/type ::entity.type/batch
   ::ablu/property ::entity.id/property-b380358ae43b
   ::laundry/arrival-date #inst "2022-03-22"
   ::laundry/to-wash? true
   ::laundry/to-iron? true
   ::laundry/piles
   {::pile/darks
    {::pile/type ::pile/darks
     ::pile/state ::pile/complete}}})

(def animal-farm-supplies
  (let [id :ae.ablution.entity.id/supply-d6804c330033]
    {;; :xt/id id
     ;; ::entity/id id
     ::property/id ::entity.id/property-b380358ae43b
     ::entity/type ::entity.type/supply
     ::supply/regular {::supply/basic 1}
     ::supply/irregular {::supply/toilet-roll 3
                         ::supply/kitchen-roll 1}}))

(def room-101-supplies
  (let [id :ae.ablution.entity.id/supply-e7f7b5a27ba7]
    {:xt/id id ::entity/id id
     ::property/id :ae.ablution.entity.id/property-e3a788c31380
     ::entity/type ::entity.type/supply
     ::supply/regular {::supply/deluxe 1
                       ::supply/logs-3 1
                       ::supply/kindling 1}
     ::supply/irregular {::supply/champagne 1}}))

;; Start
(db/start! true)
(main/start! 6003)

;; Reference data
(for [county addr/counties]
  (db/put! county))
(for [title pers/titles]
  (db/put! title))

;; Application data
(db/put! eric-arthur-blair)
(db/put! animal-farm)
(db/put! room-101)
(db/put! orwell-capital)

(db/put! batch-blair-2022-03-21)
(db/put! batch-blair-2022-03-22)

(supp/add! animal-farm-supplies)
(supp/add! room-101-supplies)

(supp/fetch-all)
(supp/pull-all)
(supp/pull-all-and-api nil nil)

;; A group of possible job configs are described by the division of jobs into teams
({#{#{1} #{2 3}} 2} #{#{2 3} #{1}})

;; Pick a small domain and finish it.

(-> (sch/find-all-jobs-api)
    second
    first
    (select-keys #{::ablu/date ::schedule/arrival-time ::schedule/departure-time}))
