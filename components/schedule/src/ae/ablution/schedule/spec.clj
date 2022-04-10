(ns ae.ablution.schedule.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [ae.ablution.base.interface :as base]
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
 '[ae.ablution.schedule :as-alias schedule]
 '[ae.ablution.schedule.job :as-alias job]
 '[ae.ablution.vehicle :as-alias vehicle])

(def property-id? (constantly true))
(def customer-id? (constantly true))

#_(def job?
    (ds/spec {:spec {:xt/id base/entity-id?
                     ::property/id property-id? ; or property
                     ::customer/id customer-id? ; or customer
                     ::job/date inst?
                     ::job/start-time inst?
                     ::job/end-time inst?}
              :name ""
              :description nil}))

(def job?
  (ds/spec {:spec {:xt/id nil
                   ::entity/id nil
                   ::entity/type ::entity.type/job
                   ::ablu/date nil
                   ::schedule/arrival-time base/date?
                   ::schedule/departure-time base/date?
                   ::job/type #{::job/holiday-home
                                ::job/commercial
                                ::job/window-clean
                                ::job/domestic}
                   ::ablu/property nil
                   ::ablu/person nil
                   ::ablu/customer nil}}))
