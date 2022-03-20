(ns ae.ablution.customer.spec
  (:require [ae.ablution :as-alias ablu]
            [ae.ablution.customer.core :as-alias cust]
            [ae.ablution.customer.person :as-alias person]
            [ae.ablution.customer.contact :as-alias contact]
            [ae.ablution.property :as-alias prop]
            [ae.ablution.property.interface.spec :as-alias prop-spec]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]))

(def customer-id
  (st/spec {:name ::cust/id
            :spec (s/and keyword? #(= namespace "ae.ablution.customer.id"))
            :gen #(gen/fmap (fn [_]
                              (as-> (random-uuid) $
                                (. $ (toString))
                                (string/split $ #"-")
                                (first $)
                                (string/join "-" ["c" $])
                                (keyword "ae.ablution.customer.id" $)))
                            (s/gen nil?))}))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(def email
  (st/spec {:name ::contact/email
            :spec (s/and string? #(re-matches email-regex %))}))

(def contact
  (st/spec {:name ::person/contact
            :spec (ds/or {:email ::contact/email
                          :phone ::contact/phone})}))

(def person-name
  (st/spec {:name ::person/name
            :spec {::person/first-name string?
                   ::person/middle-names [string?]
                   ::person/last-name string?}}))

(def person
  (st/spec {:name ::cust/person
            :spec {::person/name ::person/name
                   ::person/home-address ::person/address
                   ::person/contacts [::person/contact]}}))

(def customer
  (st/spec {:name ::ablu/customer
            :spec {::cust/id customer-id
                   ::ablu/properties [::ablu/property]
                   ::cust/persons [::cust/person]
                   ::cust/agent ::cust/agent}}))
