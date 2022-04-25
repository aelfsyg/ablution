(ns ae.ablution.address.interface
  (:require [ae.ablution.address.core :as core]
            [ae.ablution.address.spec :as spec]))

(def county? spec/county?)
(def postcode? spec/postcode?)
(def country? spec/country?)
(def address? spec/address?)

(def desc-address core/desc-address)

(def counties core/counties)
