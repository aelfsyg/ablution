(ns ae.ablution.confirm.core
  (:require [ae.ablution.legacy.interface :as legacy]
            [ae.ablution.base.interface :as base]
            [clojure.string :as string]
            [tick.core :as tick]
            [postal.core :as postal]
            [clojure.pprint :as pp]))

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

(def auth nil)

(def date-formatter
  (tick/formatter "dd-MM-yyyy"))

(def foreword-str
  "Hi %s,

I am writing to confirm that we will be cleaning your %s on %s:
")

(defn prepare-body [pers-name date jobs]
  (let [foreword (format foreword-str pers-name
                         (if (< (count jobs) 2) "property" "properties")
                         (tick/format date-formatter date))
        summary (string/join "\n" (for [{:keys [::schedule/arrival-time ::ablu/property ::job/type]} jobs
                                        :let [job-type (-> type str (string/split #"\/") last)
                                              prop-name (::property/name property)]]
                                    (format "%s at approximately %s, (%s)"
                                            prop-name (tick/time arrival-time) job-type)))
        closing "\n\nKind regards,\n\nDaniel"]
    (str foreword summary closing)))

(defn prepare-email [jobs date]
  (let [cust (-> jobs first ::ablu/customer)
        pers (-> cust ::ablu/persons first)
        name (-> pers ::person/name ::person/first-name)
        email (-> pers ::person/contacts ::contact/emails first)]
    {:postal/from "aelfsyg@gmail.com"
     :postal/to email
     ;; :postal/cc nil
     :postal/subject (str (tick/format date-formatter date) " cleaning confirmation")
     :postal/body (prepare-body name date jobs)}))

(defn prepare-emails [jobs date]
  (->> jobs
       (group-by #(-> % ::ablu/customer ::entity/id))
       (map #(-> % second (prepare-email date)))))

(defn prepare-call [jobs date]
  (let [cust (-> jobs first ::ablu/customer)
        cust-name (cust ::customer/name)
        pers (-> cust ::ablu/persons first)
        name (-> pers ::person/full-name)
        contacts (-> pers ::person/contacts (#(with-out-str (pp/pprint %))))
        summary (string/join "\n" (for [{:keys [::schedule/arrival-time ::ablu/property]} jobs
                                        :let [{:keys [::property/name]} property]]
                                    (format "%s : %s" arrival-time name)))]
    (string/join "\n" [cust-name name contacts summary])))

(defn prepare-calls [jobs date]
  (->> jobs
       (group-by #(-> % ::ablu/customer ::entity/id))
       (map #(-> % second (prepare-call date)))
       (cons (tick/format date-formatter date))
       (string/join "\n\n")))

(defn send-emails! [emails]
  (->> emails
       (map (fn [email] (update-keys email #(keyword (name %)))))
       #_(map #(postal/send-message auth %))
       doall))

(defn send!
  ([] (send! {:date (tick/today)}))
  ([{:keys [date rota]}]
   (let [date (or (::ablu/date rota) date (tick/today))
         rota (or rota (legacy/find-rota date))]
     (as-> rota $
       (group-by #(get-in % [::ablu/person ::person/contacts ::contact/prefer]) $)
       (update $ ::contact/email #(prepare-emails % date))
       (update $ ::contact/phone #(prepare-calls % date))
       (-> $ ::contact/email send-emails!)))))

(send!)

(postal/send-message
 auth
 {:to "aelfsyg@aelfsyg.com"
  :from "aelfsyg@aelfsyg.com"
  :subject "Subj"
  :body "Body"})
