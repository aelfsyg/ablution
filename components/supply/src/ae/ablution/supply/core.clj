(ns ae.ablution.supply.core
  (:require [ae.ablution.base.interface :as base]
            [ae.ablution.db.interface :as db]))

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

(def supply-items
  #{::supply/basic
    ::supply/deluxe
    ::supply/dog
    ::supply/coffee-ground
    ::supply/coffee-instant
    ::supply/coffee-decaff
    ::supply/coffee-decaff-sub
    ::supply/tea-loose
    ::supply/tea-bags
    ::supply/tea-decaff
    ::supply/tea-decaff-sub
    ::supply/white-wine
    ::supply/red-wine
    ::supply/sparkling-wine
    ::supply/champagne
    ::supply/milk-1-pint
    ::supply/milk-2-pint
    ::supply/milk-4-pint
    ::supply/milk-whole
    ::supply/milk-skimmed
    ::supply/sugar
    ::supply/kindling
    ::supply/logs-3
    ::supply/coal
    ::supply/dishwasher-tablets
    ::supply/sponge-scourer
    ::supply/scourer
    ::supply/j-cloth
    ::supply/kitchen-roll
    ::supply/toilet-roll
    ::supply/sanitiser
    ::supply/viraklean
    ::supply/wipes
    ::supply/washing-up-liquid
    ::supply/hand-soap
    ::supply/oven-gloves
    ::supply/tea-towel
    ::supply/stationery})

;; There are two forms of supply
;; repeated/regular
;; one-off/irregular/welcome-packs

;; A welcome pack will be sent for every booking
;; An irregular supply will be sent just once, then removed.

;; Match properties to rota
;; This will create a list of supplies to send
;; Manually remove irregular supplies

;; Keys

(def supplies
  (let [id (base/entity-id "supply")]
    {::entity.id/property-b380358ae43b
     {:xt/id id
      ::entity/id id
      ::ablu/property ::entity.id/property-b380358ae43b
      ::entity/type ::entity.type/supply
      ::supply/regular {::supply.wp/basic 1}
      ::supply/irregular {::supply/toilet-roll 3
                          ::supply/kitchen-roll 1}}}))

(defn ks-help [prop-id regular? item]
  [prop-id (if regular? ::supply/regular ::supply/irregular) item])

(defn change [supplies ks func]
  (update-in supplies ks func))

(defn adder [orig count]
  (+ (or orig 0) count))

(defn add-to
  ([supplies ks]
   (add-to supplies ks 1))
  ([supplies ks count]
   (change supplies ks #(adder % count))))

(defn abber [orig count]
  (- (or orig 0) count))

(defn remove-from [supplies ks count]
  (change supplies ks #(abber % count)))

(defn remove-all-from [supplies ks]
  (change supplies ks (constantly 0)))

;; DB

(defn conform
  ([supplies]
   (if-let [id (::entity/id supplies)]
     (conform id supplies)
     (conform (base/entity-id "supply" {:suffix (base/suffix (::property/id supplies))})
              supplies)))
  ([id supplies] (-> supplies (merge {:xt/id id ::entity/id id}))))

(defn fetch [prop-id]
  (->> prop-id
       (db/q '{:find [e] :in [[prop-id]]
               :where [[e ::entity/type ::entity.type/supply]
                       [e ::property/id prop-id]]})
       (map first)))

(defn pull [prop-id]
  (->> prop-id
       (db/q '{:find [(pull e [*])] :in [[prop-id]]
               :where [[e ::entity/type ::entity.type/supply]
                       [e ::property/id prop-id]]})
       (map first)))

(defn fetch-many [prop-ids]
  (->> prop-ids
       (db/q '{:find [e]
               :in [[prop-id ...]]
               :where [[e ::entity/type ::entity.type/supply]
                       [e ::property/id prop-id]]})
       (map first)))

(defn pull-many [prop-ids]
  (->> prop-ids
       (db/q '{:find [(pull e [*])]
               :in [[prop-id ...]]
               :where [[e ::entity/type ::entity.type/supply]
                       [e ::property/id prop-id]]})
       (map first)))

(defn fetch-all []
  (->> '{:find [e]
         :where [[e ::entity/type ::entity.type/supply]]}
       db/q
       (map first)))

(defn pull-all []
  (->> '{:find [(pull e [*])]
         :where [[e ::entity/type ::entity.type/supply]]}
       db/q
       (map first)))

(defn fetch-all-api [user prop-ids]
  [true (fetch-many prop-ids)])

(defn pull-all-api [user prop-ids]
  (if prop-ids
    [true (pull-many prop-ids)]
    [true (pull-all)]))

(defn pull-all-and-api [user prop-ids]
  [true
   (db/q '{:find [(pull ?supp [*])
                  (pull ?prop [*])
                  (pull ?cust [*])]
           :where [[?supp ::entity/type ::entity.type/supply]
                   [?supp ::property/id ?prop]
                   [?cust ::ablu/properties ?prop]
                   [(base/active? ?prop)]
                   [(base/active? ?cust)]]})])

;;

(defn add!
  ([supplies] (-> supplies conform db/put!))
  ([id supplies] (-> id (conform supplies) db/put!)))

(defn add!-api [supplies]
  [true (add! supplies)])

(defn remove! [prop-id]
  (db/del-many! (fetch prop-id)))

(defn remove-all! []
  (db/del-all! ::entity.type/supply))
