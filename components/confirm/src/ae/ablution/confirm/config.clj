(ns ae.ablution.confirm.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]))

(defn config [profile]
  (aero/read-config (io/resource "~/src/ablution/config.edn")))
