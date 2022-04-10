(ns ae.ablution.db.interface
  (:require [ae.ablution.db.core :as core]))

(def db-url core/db-url)
(def node core/node)
(def close core/close)

(def q core/q)
(def entity core/entity)
(def find-entity core/find-entity)
(def put! core/put!)
(def del! core/del!)
(def del-many! core/del-many!)
(def del-all! core/del-all!)
(def evict! core/evict!)
