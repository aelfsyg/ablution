(ns ae.ablution.person.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [spec-tools.data-spec :as ds]
            [spec-tools.core :as st]))

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
 '[ae.ablution.vehicle :as-alias vehicle])

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(def email
  (st/spec {:name ::contact/email
            :spec (s/and string? #(re-matches email-regex %))
            :gen #(gen/fmap (fn [[user domain]] (str user "@" domain ".com"))
                            (gen/tuple (gen/string-alphanumeric)
                                       (gen/string-alphanumeric)))}))

(def phone-number
  (st/spec {:name ::contact/phone-number
            :spec string?
            :gen (fn [] (gen/fmap (fn [nos] (string/join "" nos))
                                  (s/gen (s/coll-of (s/gen (s/and int?) #(<= 0 % 9))
                                                    :count 11))))}))

(def person-name
  (st/spec {:name ::person/name
            :spec {::person/first-name string?
                   ::person/middle-names [string?]
                   ::person/last-name string?}}))

(def person
  (st/spec {:name ::ablu/person
            :spec {:xt/id (base/entity-id  ::entity.type/person)
                   ::entity/type ::entity.type/person
                   ::person/title #{::person.title/mr ::person.title/mrs ::person.title/ms ::person.title/miss}
                   ::person/name person-name
                   ::person/contact-address ::person/address
                   ::person/contacts {::contact/emails [email]
                                      ::contact/phone-numbers [phone-number]
                                      ::contact/prefer #{:email :phone}}}}))
