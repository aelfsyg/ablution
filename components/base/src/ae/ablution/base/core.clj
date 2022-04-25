(ns ae.ablution.base.core
  (:require [clojure.string :as string]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            ;; [java-time :as time]
            [tick.core :as t]
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
 '[ae.ablution.supply :as-alias supply]
 '[ae.ablution.supply.wp :as-alias supply.wp]
 '[ae.ablution.vehicle :as-alias vehicle])

(defn random-48 []
  (-> (random-uuid) .toString (string/split #"-") last))

(defn create-entity-id [prefix id]
  (let [pre (if (string? prefix) prefix (name prefix))]
    (->> [pre id]
         (string/join "-")
         (keyword "ae.ablution.entity.id"))))

(defn entity-id [prefix & [{:keys [full? suffix]}]]
  (if suffix
    (create-entity-id prefix suffix)
    (create-entity-id prefix (if full? (str (random-uuid)) (random-48)))))

(defn suffix [id]
  (-> id
      name
      (string/split #"-")
      rest
      (#(string/join "-" %))))

(defn legacy-entity-id [prefix id]
  (create-entity-id prefix id))

(defn parse-legacy-id [id]
  (-> id str (string/split #"-") last))

(def year?
  (st/spec {:spec int?
            :type :int
            :description "A year after 2020."
            :gen (fn [] (gen/fmap #(+ % 2020) (s/gen (s/and pos-int? #(< 0 % 10)))))}))

(def month?
  (st/spec {:spec (s/and pos-int? #(<= 0 % 12))
            :type :int
            :description ""}))

(def day?
  (st/spec {:spec (s/and pos-int? #(<= 0 % 28))
            :type :int
            :description ""}))

(def date?
  (st/spec {:spec #(instance? java.time.LocalDate %)
            :type :local-date
            :description ""
            :gen (fn [] (gen/fmap #(apply t/date %)
                                  (s/gen (s/cat :year year?
                                                :month month?
                                                :day day?))))}))

(def date-time? nil)

(def entity-id?
  (st/spec {:name ::entity/id
            :spec (s/and keyword? #(= (namespace %) "ae.ablution.entity.id"))
            :gen (fn [] (gen/fmap #(entity-id %) (gen/string-alphanumeric)))}))

(def entity-type?
  #{::entity.type/address ::entity.type/booking ::entity.type/batch
    ::entity.type/confirmation ::entity.type/customer ::entity.type/employee
    ::entity.type/person ::entity.type/property ::entity.type/vehicle})

(defn active? [e]
  (not (::ablu/inactive? e)))
