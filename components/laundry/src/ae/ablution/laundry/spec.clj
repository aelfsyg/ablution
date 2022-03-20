(ns ae.ablution.laundry.spec
  (:require [ae.ablution.laundry :as-alias laundry]
            [ae.ablution.laundry.batch :as-alias b]
            [ae.ablution.laundry.item :as-alias item]
            [ae.ablution.laundry.pile :as-alias pile]
            [ae.ablution.property.interface :as-alias prop]
            [ae.ablution.spec.interface :as spec]
            [clj-uuid :as uuid]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [java-time :as time]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]))

(def laundry-item
  (s/spec {:name ::laundry/item
           :spec #{::item/pillowcase ::item/duvet-cover ::item/sheet-king}}))

(def pile-state
  (st/spec {:name ::pile/state
            :spec (s/spec #{::b/arrived ::b/washed ::b/ironed ::b/complete})}))

(def pile-type
  (st/spec {:name ::pile/type
            :spec (s/spec #{::pile/darks ::pile/whites ::pile/privates ::pile/other})}))

(def pile
  (ds/spec
   {:name ::pile
    :spec {::prop/id ::prop/id
           ::laundry/arr-date spec/date?
           ::pile/type pile-type
           ::pile/state pile-state
           ::laundry/items {laundry-item pos-int?}}}))

(def batch
  (ds/spec
   {:name ::laundry/batch
    :spec {::b/id uuid?
           ::laundry/arr-date spec/date?
           ::laundry/to-wash? boolean?
           ::laundry/to-iron? boolean?
           ::prop/id pos-int?
           (ds/opt ::laundry/deadline) inst?
           ::laundry/piles {pile-type pile}}}))

(def property
  (ds/spec
   {:name ::laundry/property
    :spec {::prop/id ::prop/id
           ::laundry/type ::laundry/commercial
           ::b/active {spec/date? batch}
           ::b/completed {spec/date? batch}}}))
