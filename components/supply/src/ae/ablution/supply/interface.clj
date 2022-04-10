(ns ae.ablution.supply.interface
  (:require [ae.ablution.supply.core :as core]))

(def change core/change)
(def add-to core/add-to)
(def remove-from core/remove-from)
(def remove-all-from core/remove-all-from)

(def conform core/conform)
(def add! core/add!)
(def fetch-all core/fetch-all)
(def remove-all! core/remove-all!)

(def fetch core/fetch)
(def fetch-many core/fetch-many)
(def fetch-all core/fetch-all)
(def fetch-all-api core/fetch-all-api)

(def pull core/pull)
(def pull-many core/pull-many)
(def pull-all core/pull-all)
(def pull-all-api core/pull-all-api)
(def pull-all-and-api core/pull-all-and-api)

(def add! core/add!)
(def add!-api core/add!-api)
