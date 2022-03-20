(ns ae.ablution.person.core
  (:require [ae.ablution.address.interface :as i.address]))

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

(def titles
  [{:xt/id ::person.title/mr
    ::person.title/desc "Mr"}
   {:xt/id ::person.title/mrs
    ::person.title/desc "Mrs"}
   {:xt/id ::person.title/ms
    ::person.title/desc "Ms"}
   {:xt/id ::person.title/miss
    ::person.title/desc "Miss"}])

(def eric-arthur-blair
  {:xt/id ::entity.id/person-8891cec18a2c
   ::entity/type ::entity.type/person
   ::entity/type-exp ::person
   ::person/title ::person.title/mr
   ::person/name
   {::person/first-name "Eric"
    ::person/middle-names ["Arthur"]
    ::person/last-name "Blair"}
   ::person/contact-address i.address/twentytwo-portobello
   ::person/contacts {::contact/emails ["orwell@ingsoc.org"]
                      ::contact/phone-numbers ["07777 777 777"]}})

(defn add-email [] nil)
(defn add-primary-email [] nil)
(defn add-secondary-email [] nil)

(defn add-phone [] nil)
(defn add-primary-phone [] nil)
(defn add-secondary-phone [] nil)
