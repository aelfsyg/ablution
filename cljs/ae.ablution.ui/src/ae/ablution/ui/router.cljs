(ns ae.ablution.ui.router
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [ae.ablution.ui :as-alias ui]
            [ae.ablution.ui.views :as-alias views]
            [ae.ablution.ui.events :as-alias events]
            [re-frame.core :as rf]))

(def routes
  ["/" {"" ::views/home
        "bookings" ::views/bookings
        "confirms" ::views/confirms
        "laundry" ::views/laundry
        "schedules" ::views/schedules
        "supplies/" {"" ::views/supplies
                     [:ae.ablution.entity/id] ::views/supplies}}])

(def history
  (let [dispatch
        #(rf/dispatch
          [::events/set-active-page
           {::ui/page (:handler %)
            :ae.ablution.entity/id (:ae.ablution.entity/id %)}])
        match #(bidi/match-route routes %)]
    (pushy/pushy dispatch match)))

(def url-for (partial bidi/path-for routes))

(defn set-token! [token]
  (pushy/set-token! history token))

(defn start! []
  (pushy/start! history))
