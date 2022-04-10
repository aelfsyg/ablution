(ns ae.ablution.ui.subs
  (:require [ae.ablution :as-alias ablu]
            [ae.ablution.ui :as-alias ui]
            [ae.ablution.ui.events :as-alias events]
            [re-frame.core :as rf]))

(rf/reg-sub
 ::ui/page
 (fn [db _]
   (::ui/page db)))

(rf/reg-sub
 ::ablu/jobs
 (fn [db _]
   (::ablu/jobs db)))

(rf/reg-sub
 ::ui/supply
 (fn [db _]
   (::ui/supply db)))

(rf/reg-sub
 ::ablu/supplies
 (fn [db _]
   (::ablu/supplies db)))
