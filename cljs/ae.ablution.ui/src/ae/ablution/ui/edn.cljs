(ns ae.ablution.ui.edn
  (:require [ajax.interceptors :refer [map->ResponseFormat]]
            [ajax.protocols :refer [-body]]
            [tick.core :as t]
            [cljs.reader :as edn]
            [cljs.tools.reader.edn :as edn2]))

(def more-data-readers
  {'time/date t/date
   'time/date-time t/date-time})

(defn edn-read [xhrio]
  (->> xhrio
       -body
       (edn/read-string more-data-readers)))

(defn edn-response-format
  ([] (map->ResponseFormat
       {:read edn-read
        :description "EDN"
        :content-type ["application/edn"]}))
  ([_] (edn-response-format)))

(defn edn-request-format
  ([] {:write pr-str
       :content-type ["application/edn"]})
  ([_] (edn-request-format)))
