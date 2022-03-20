(ns ae.ablution.property.spec
  (:require [spec-tools.core :as st]
            [clojure.spec.alpha :as s]))

(def id (st/spec pos-int?))
