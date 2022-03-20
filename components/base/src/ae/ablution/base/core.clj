(ns ae.ablution.base.core
  (:require [clojure.string :as string]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [spec-tools.core :as st]))

#_{:clj-kondo/ignore [:unused-namespace]}
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

(defn random-48 []
  (-> (random-uuid) .toString (string/split #"-") last))

(defn entity-id
  ([prefix] (entity-id prefix false))
  ([prefix full?]
   (let [pre (if (string? prefix) prefix (name prefix))
         id (if full? (str (random-uuid)) (random-48))]
     (->> [pre id]
          (string/join "-")
          (keyword "ae.ablution.entity.id")))))

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
            :gen (fn [] (gen/fmap #(apply time/local-date %)
                                  (s/gen (s/cat :year year? :month month? :day day?))))}))
