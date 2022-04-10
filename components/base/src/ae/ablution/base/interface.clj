(ns ae.ablution.base.interface
  (:require [ae.ablution.base.core :as core]
            [ae.ablution.base.env :as env]))

(def env env/env)

(def year? core/year?)
(def month? core/month?)
(def day? core/day?)
(def date? core/date?)
(def date-time? core/date-time?)

(def random-48 core/random-48)
(def entity-id core/entity-id)
(def legacy-entity-id core/legacy-entity-id)
(def parse-legacy-id core/parse-legacy-id)
(def suffix core/suffix)

(def entity-id? core/entity-id?)
(def entity-type? core/entity-type?)

(def active? core/active?)
