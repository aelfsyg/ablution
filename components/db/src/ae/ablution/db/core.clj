(ns ae.ablution.db.core
  (:require [ae.ablution :as-alias ablu]
            [clojure.string :as string]
            [xtdb.api :as xt]
            [ae.ablution.db.indexer :as idx]))

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

(def db-url "jdbc:postgresql://localhost:5432/ablution?user=ae")

(def xtdb-pg
  {:xtdb.jdbc/connection-pool
   {:dialect {:xtdb/module 'xtdb.jdbc.psql/->dialect}
    :db-spec {:jdbcUrl db-url}}
   :xtdb/tx-log
   {:xtdb/module 'xtdb.jdbc/->tx-log
    :connection-pool :xtdb.jdbc/connection-pool}
   :xtdb/document-store
   {:xtdb/module 'xtdb.jdbc/->document-store
    :connection-pool :xtdb.jdbc/connection-pool}})

(def xtdb-eav
  {:eav {:xtdb/module 'xtdb.lucene/->lucene-store
         :analyzer 'xtdb.lucene/->analyzer
         :db-dir "/home/ae/src/ablution/lucene/eav"
         ;; :indexer 'ae.ablution.db.indexer/->indexer
         :indexer 'xtdb.lucene/->indexer}})

(def xtdb-multi
  {:multi {:xtdb/module 'xtdb.lucene/->lucene-store
           :analyzer 'xtdb.lucene/->analyzer
           :db-dir "/home/ae/src/ablution/lucene/multi"
           :indexer 'xtdb.lucene.multi-field/->indexer}})

(defonce node (atom nil))

(defn start! [in-memory?]
  (if in-memory?
    (reset! node (xt/start-node {}))
    (reset! node (xt/start-node (merge xtdb-pg xtdb-eav #_xtdb-multi)))))

(defn close [node]
  (.close @node))

(defn q
  ([query] (xt/q (xt/db @node) query))
  ([query in] (xt/q (xt/db @node) query in)))

(defn entity [id]
  (xt/entity (xt/db @node) id))

(defn find-entity
  ([term]
   (q '{:find [e v a s]
        :in [term]
        :where [[(wildcard-text-search term {:lucene-store-k :eav}) [[e v a s]]]]}
      term))
  ([term type]
   (q '{:find [e v a s]
        :in [[term type]]
        :where [[e ::entity/type type]
                [(wildcard-text-search term {:lucene-store-k :eav}) [[e v a s]]]]}
      [term type])))

;;

(defn put! [doc]
  (xt/submit-tx @node [[::xt/put doc]]))

(defn del! [id]
  (xt/submit-tx @node [[::xt/delete id]]))

(defn del-many! [ids]
  (xt/submit-tx @node (vec (for [id ids] [::xt/delete id]))))

(defn del-all! [type]
  (->> '{:find [e]
         :where [[e ::entity/type type]]}
       q
       (map first)
       del-many!))

(defn evict! [id]
  (xt/submit-tx @node [[::xt/evict id]]))
