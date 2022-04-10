(ns ae.ablution.legacy.core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.date-time :as jdbc.dt]
            [ae.ablution.base.interface :as base]
            [clojure.string :as string]
            [tick.core :as time]
            [tick.core :as tick]))

#_{:clj-kondo/ignore [:unused-namespace]}
(require
 '[ae.ablution :as-alias ablu]
 '[ae.ablution.address :as-alias address]
 '[ae.ablution.address.county :as-alias county]
 '[ae.ablution.agent :as-alias ablu.agent]
 '[ae.ablution.customer :as-alias customer]
 '[ae.ablution.confirm :as-alias confirm]
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

(defn connect [] nil)

(defn ->job [[date job-type prop cust prop-name cust-name arr-time dep-time phone mobile email prefer-tel prefer-email]]
  (let [property {::entity/id (base/legacy-entity-id ::entity.type/property prop)
                  ::entity/type ::entity.type/property
                  ::property/name prop-name}
        person {::entity/id (base/legacy-entity-id ::entity.type/person cust)
                ::entity/type ::entity.type/person
                ::person/full-name cust-name
                ::person/name (let [[first last] ((juxt first last) (string/split cust-name #" "))]
                                {::person/first-name first ::person/last-name last})
                ::person/contacts {::contact/emails ["aelfsyg@aelfsyg.com" #_email]
                                   ::contact/phones (filter not-empty [phone mobile])
                                   ::contact/prefer (if ({0 false 1 true} prefer-tel) ::contact/phone ::contact/email)}}
        customer {::entity/id (base/legacy-entity-id ::entity.type/customer cust)
                  ::entity/type ::entity.type/customer
                  ::customer/name cust-name
                  ::ablu/properties [property]
                  ::ablu/persons [person]}]
    {:xt/id (base/entity-id (string/join "-" [date prop job-type]))
     ::ablu/date (tick/date date)
     ::schedule/arrival-time (-> (tick/date date) (tick/at arr-time))
     ::schedule/departure-time (-> (tick/date date) (tick/at dep-time))
     ::job/type ({"holiday-home" ::job/holiday-home
                  "commercial" ::job/commercial
                  "window-clean" ::job/window-clean
                  "domestic" ::job/domestic} job-type)
     ::ablu/property property
     ::ablu/person person
     ::ablu/customer customer}))

(def hansell-shipbarn
  ["2022-03-25" "holiday-home" 25 56 "Shipbarn" "Sasha Hansell"
   "10:15" "10:45"
   "01263 488 307" "" "hansell@shipbarn.co.uk" 0 1])

(def hansell-wellworn
  ["2022-03-25" "holiday-home" 24 56 "Wellworn" "Sasha Hansell"
   "10:45" "11:15"
   "01263 488 307" "" "hansell@shipbarn.co.uk" 0 1])

(def simpson-mundesley
  ["2022-03-25" "domestic-clean" 28 30 "13 Walcott Crescent" "Jim Simpson"
   "11:30" "14:45"
   "" "07786 235 123" "simpson@gmail.com" 0 1])

(def ag-corp
  ["2022-03-25" "commercial" 45 89 "AgCorp" "Stuart Frome"
   "15:00" "15:45"
   "01263 464 355" "" "admin@agcorp.com" 0 1])

(def willow-windows
  ["2022-03-25" "window-clean" 23 54 "Willow Farm" "Mr Frumler"
   "09:15" "09:30"
   "01263 464 355" "" "frumler@willowfarm.com" 1 0])

(def willow-domestic
  ["2022-03-25" "domestic" 23 54 "Willow Farm" "Mr Frumler"
   "09:00" "09:45"
   "01263 464 355" "" "frumler@willowfarm.com" 1 0])

(def jobs [willow-windows willow-domestic
           hansell-shipbarn hansell-wellworn
           simpson-mundesley ag-corp])

(defn find-rota
  ([] (find-rota (tick/today)))
  ([^java.time.LocalDate date]
   (->> jobs
        (map #(assoc % 0 date))
        (map ->job))))

(defn find-todays-rota
  [] (find-rota (time/today)))

(defn find-all-jobs []
  (->> jobs
       (map ->job)))
