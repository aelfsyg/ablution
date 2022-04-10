(ns ae.ablution.address.core
  (:require [clojure.spec.alpha :as s]
            [spec-tools.data-spec :as ds]
            [clojure.string :as string]
            [clojure.spec.gen.alpha :as gen]))

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
 '[ae.ablution.address.county :as-alias county]
 '[ae.ablution.vehicle :as-alias vehicle])

(def counties
  [{:xt/id ::county/norfolk
    ::county/full-name "Norfolk"
    ::county/names ["Norfolk" "Norf"]}])

(defn desc-address
  [{::address/keys [first-line second-line postal-town county postcode country]}]
  (string/join " / " [first-line second-line postal-town
                      county postcode country]))
