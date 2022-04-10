(ns ae.ablution.laundry.spec
  (:require [ae.ablution.laundry :as-alias laundry]
            [ae.ablution.laundry.batch :as-alias b]
            [ae.ablution.laundry.item :as-alias item]
            [ae.ablution.laundry.pile :as-alias pile]
            [ae.ablution.property.interface :as-alias prop]
            [ae.ablution.base.interface :as base]
            [clj-uuid :as uuid]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [java-time :as time]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]))

#_{:clj-kondo/ignore [:unused-namespace
                      :duplicate-require]}
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

(def laundry-item?
  (st/spec {:name ::laundry/item
            :spec {::item/type #{::item/pillowcase ::item/sheet ::item/duvet-cover
                                 ::item/towel ::item/bath-mat}
                   ::item/size #{::item/small ::item/medium ::item/large
                                 ::item/single ::item/double ::item/king ::item/super-king}}}))

(def pile-state?
  (st/spec {:name ::pile/state
            :spec (s/spec #{::b/arrived ::b/washed ::b/ironed ::b/complete})}))

(def pile-type?
  (st/spec {:name ::pile/type
            :spec (s/spec #{::pile/darks ::pile/whites ::pile/privates ::pile/other})}))

(def pile?
  (ds/spec
   {:name ::laundry/pile
    :spec {::prop/id base/entity-id?
           ::laundry/arr-date base/date?
           ::pile/type pile-type?
           ::pile/state pile-state?
           ::laundry/items {laundry-item? pos-int?}}}))

(def batch?
  (ds/spec
   {:name ::laundry/batch
    :spec {:xt/id base/entity-id?
           ::laundry/arr-date base/date?
           ::laundry/to-wash? boolean?
           ::laundry/to-iron? boolean?
           ::prop/id pos-int?
           (ds/opt ::laundry/deadline) inst?
           ::laundry/piles {pile-type? pile?}}}))
