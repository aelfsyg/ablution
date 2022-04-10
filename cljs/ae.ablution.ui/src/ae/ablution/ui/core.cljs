(ns ae.ablution.ui.core
  (:require [ae.ablution.ui.events :as events]
            [ae.ablution.ui.router :as router]
            [ae.ablution.ui.subs]
            [ae.ablution.ui.views :as views]
            [re-frame.core :as rf]
            [reagent.dom :as rdom]))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (let [root (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root)
    (rdom/render [views/main] root)))

(defn init []
  (router/start!)
  (rf/dispatch-sync [::events/initialise-db])
  (mount-root))
