(ns ae.ablution.address.spec
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

(def county?
  (s/spec #{::county/lincolnshire ::county/norfolk ::county/suffolk ::county/essex ::county/cambridgeshire}))

(def postcode-regex "^([A-Z][A-HJ-Y]?\\d[A-Z\\d]? ?\\d[A-Z]{2}|GIR ?0A{2})$")
(def postcode? (s/and string? #(re-matches postcode-regex %)))

(def country? nil)

(def address?
  (ds/spec
   {:name ::ablu/address
    :spec {::address/first-line string?
           ::address/second-line string?
           ::address/postal-town string?
           ::address/county county?
           ::address/postcode postcode?
           ::address/country country?}}))
