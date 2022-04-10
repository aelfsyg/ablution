(ns ae.ablution.schedule.core
  (:require
   [tick.core :as tick]
   [ae.ablution.db.interface :as db]
   [ae.ablution.legacy.interface :as legacy]))

(defn find-jobs
  ([] (find-jobs (tick/today)))
  ([date]
   (legacy/find-rota date)))

(defn find-jobs-api []
  [true (find-jobs)])

(defn find-all-jobs []
  (legacy/find-all-jobs))

(defn find-all-jobs-api []
  [true (find-all-jobs)])
