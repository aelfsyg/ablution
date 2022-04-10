(ns ae.ablution.ui.views
  (:require [ae.ablution :as-alias ablu]
            [ae.ablution.ui :as-alias ui]
            [ae.ablution.ui.events :as events]
            [ae.ablution.ui.router :as router]
            [ae.ablution.ui.subs :as subs]
            [ae.ablution.customer :as-alias customer]
            [ae.ablution.property :as-alias property]
            [ae.ablution.entity :as-alias entity]
            [ae.ablution.supply :as-alias supply]
            [clojure.pprint :as pp]
            [clojure.string :as string]
            [reagent.core :as r]
            [re-com.core :as rc]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(defn p [x]
  (with-out-str (pp/pprint x)))

(defn title []
  [rc/h-box
   :justify :center
   :children [[rc/gap :size "2"]
              [rc/title
               :src (rc/at)
               :label "Ablution"
               :level :level1]
              [rc/gap :size "2"]]])

(defn links []
  [rc/h-box
   :src (rc/at)
   :width "100%"
   :children
   [[rc/box :size "auto" :justify :center
     :child [:a {:href (router/url-for ::home)} "Home"]]
    [rc/box :size "auto" :justify :center
     :child [:a {:href (router/url-for ::bookings)} "Bookings"]]
    [rc/box :size "auto" :justify :center
     :child [:a {:href (router/url-for ::confirms)} "Confirms"]]
    [rc/box :size "auto" :justify :center
     :child [:a {:href (router/url-for ::laundry)} "Laundry"]]
    [rc/box :size "auto" :justify :center
     :child [:a {:href (router/url-for ::schedules)} "Schedules"]]
    [rc/box :size "auto" :justify :center
     :child [:a {:href (router/url-for ::supplies)} "Supplies"]]]])

(defn header []
  [rc/v-box :children [[title] [links] [rc/gap :size "20px"]]])

(defn footer [] nil)

(defn home []
  [rc/p "Welcome to the Ablution demo."])

(defn bookings [] nil)

(defn job-row [{:keys [::ablu/person ::ablu/property ::ablu/customer] :as job}]
  [rc/h-box
   :children
   [[rc/title :label person]
    [rc/title :label property]
    [rc/title :label customer]]])

(defn job-rows [jobs]
  (for [job jobs]
    ^{:key (::entity/id job)}
    [job-row job]))

(defn confirms []
  (if-let [jobs @(rf/subscribe [::ablu/jobs])]
    [rc/v-box :children [job-rows]]
    [rc/p "There are no jobs."]))

(defn laundry [] nil)

(defn schedules [] nil)

(defn supplies-row [id supplies]
  (let [prop-name (::property/name supplies)
        cust-name (::customer/name supplies)
        cust-id (::customer/id supplies)]
    ^{:key id}
    [rc/h-box :children
     [[rc/hyperlink
       :label [rc/title :label prop-name :level :level2]
       :on-click (fn [& _] (rf/dispatch [::events/set-active-supply id]))]
      [rc/hyperlink
       :label [rc/title :label cust-name :level :level3]
       :on-click (fn [& _] (js/alert cust-id))]]]))

(def supply-types
  {::supply/regular "Regular"
   ::supply/irregular "Irregular"})

(defn update-item-count [supplies supply-type item f]
  (let [s (update-in supplies [supply-type item] #(if (some? %) (f %) (f 0)))]
    (rf/dispatch [::events/update-supply-model s])))

(defn minus [supplies supply-type item]
  (update-item-count supplies supply-type item #(- % 1)))

(defn plus [supplies supply-type item]
  (update-item-count supplies supply-type item #(+ % 1)))

(defn breakdown [supplies item-list supply-type]
  [rc/v-box :size "50%" :children
   [[rc/title :label (supply-type supply-types) :level :level3]
    (if item-list
      (for [[item n] item-list]
        ^{:key {::supply/type supply-type ::supply/item item}}
        [rc/h-box :children
         [(with-out-str (pp/pprint [item n]))
          [rc/md-icon-button :md-icon-name "zmdi-minus"
           :size :smaller
           :on-click #(minus supplies supply-type item)]
          [rc/md-icon-button :md-icon-name "zmdi-plus"
           :size :smaller
           :on-click #(plus supplies supply-type item)]]])
      [:div "No items."])]])

(def supply-items
  [{:id ::supply/basic, :label "Basic", :group "Welcome packs", :order 0}
   {:id ::supply/champagne, :label "Champagne", :group "Alcoholic drinks", :order 23}
   {:id ::supply/coal, :label "Coal", :group "Fire", :order 72}
   {:id ::supply/coffee-decaff, :label "Decaff. coffee", :group "Hot drinks", :order 12}
   {:id ::supply/coffee-decaff-sub, :label "Decaff. coffee as sub.", :group "Hot drinks", :order 13}
   {:id ::supply/coffee-ground, :label "Ground coffee", :group "Hot drinks", :order 10}
   {:id ::supply/coffee-instant, :label "Instant coffee", :group "Hot drinks", :order 11}
   {:id ::supply/deluxe, :label "Deluxe", :group "Welcome packs", :order 1}
   {:id ::supply/dishwasher-tablets, :label "Dishwasher tablets", :group "Cleaning", :order 40}
   {:id ::supply/dog, :label "Dog WP", :group "Welcome packs", :order 2}
   {:id ::supply/hand-soap, :label "Hand soap", :group "Bathroom", :order 50}
   {:id ::supply/j-cloth, :label "J cloths", :group "Cleaning", :order 41}
   {:id ::supply/kindling, :label "Kindling", :group "Fire", :order 70}
   {:id ::supply/kitchen-roll, :label "Kitchen roll", :group "Cleaning", :order 42}
   {:id ::supply/logs-3, :label "Logs", :group "Fire", :order 71}
   {:id ::supply/milk-1-pint, :label "Semi-skimmed 1 pint", :group "Milk", :order 30}
   {:id ::supply/milk-2-pint, :label "Semi-skimmed 2 pint", :group "Milk", :order 31}
   {:id ::supply/milk-4-pint, :label "Semi-skimmed 4 pint", :group "Milk", :order 32}
   {:id ::supply/milk-skimmed, :label "Skimmed milk 1 pint", :group "Milk", :order 34}
   {:id ::supply/milk-whole, :label "Whole milk 1 pint", :group "Milk", :order 33}
   {:id ::supply/oven-gloves, :label "Oven gloves", :group "Kitchen", :order 60}
   {:id ::supply/red-wine, :label "Red wine", :group "Alcoholic drinks", :order 21}
   {:id ::supply/sanitiser, :label "Sanitiser", :group "Cleaning", :order 43}
   {:id ::supply/scourer, :label "Scourer", :group "Cleaning", :order 44}
   {:id ::supply/sparkling-wine, :label "Sparkling wine", :group "Alcoholic drinks", :order 22}
   {:id ::supply/sponge-scourer, :label "Sponge scourer", :group "Cleaning", :order 45}
   {:id ::supply/stationery, :label "Stationery", :group "Misc", :order 90}
   {:id ::supply/sugar, :label "Sugar", :group "Kitchen", :order 61}
   {:id ::supply/tea-bags, :label "Tea bags", :group "Hot drinks", :order 15}
   {:id ::supply/tea-decaff, :label "Decaff. tea bags", :group "Hot drinks", :order 16}
   {:id ::supply/tea-decaff-sub, :label "Decaff. tea bags as sub.", :group "Hot drinks", :order 17}
   {:id ::supply/tea-loose, :label "Loose-leaf tea", :group "Hot drinks", :order 14}
   {:id ::supply/tea-towel, :label "Tea towel", :group "Kitchen", :order 62}
   {:id ::supply/toilet-roll, :label "Toilet roll", :group "Bathroom", :order 51}
   {:id ::supply/viraklean, :label "Viraklean", :group "Cleaning", :order 46}
   {:id ::supply/washing-up-liquid, :label "Washing-up liquid", :group "Cleaning", :order 47}
   {:id ::supply/white-wine, :label "White wine", :group "Alcoholic drinks", :order 20}
   {:id ::supply/wipes, :label "Wipes", :group "Cleaning", :order 48}])

(def model-regular (r/atom #{}))
(def model-irregular (r/atom #{}))

(defn update-s [supplies supply-type model]
  (let [foo (fn [s supply-type item-id]
              (update-in s [supply-type item-id] #(if (some? %) % 0)))]
    (as-> supplies $
      (reduce #(foo %1 supply-type %2) $ model)
      (update-in $ [supply-type] #(select-keys % model)))))

(defn picker [s supply-type item-list model]
  [rc/multi-select
   :src (rc/at)
   :choices supply-items
   :sort-fn :order ;; :group  ;; #(compare (:order %1) (:order %2))
   :placeholder "Select some items."
   :model model
   :on-change
   (fn [m]
     (do (reset! model m)
         (rf/dispatch [::events/update-supply-model (update-s s supply-type m)])))
   :left-label "Unselected"
   :right-label "Selected"
   :width "600px"])

(defn supply [id supplies]
  (let [{:keys [::supply/regular ::supply/irregular] :as s} (id supplies)]
    (when (empty? @model-regular) (reset! model-regular (into #{} (keys regular))))
    (when (empty? @model-irregular) (reset! model-irregular (into #{} (keys irregular))))
    [rc/v-box :children
     [[supplies-row id s]
      [rc/h-box :children
       [[breakdown s regular ::supply/regular]
        [breakdown s irregular ::supply/irregular]]]
      [rc/gap :size "20px"]
      [rc/h-box :children
       [[rc/gap :size "1"]
        [rc/v-box :children
         [[rc/title :label "Regular" :level :level2]
          [picker s ::supply/regular regular model-regular]
          [rc/gap :size "20px"]
          [rc/title :label "Irregular" :level :level2]
          [picker s ::supply/irregular irregular model-irregular]
          [rc/gap :size "20px"]
          [rc/h-box :children
           [[rc/gap :size "1"]
            [rc/button
             :label [:span [:i.zmdi.zmdi-hc-fw-rc.zmdi-download] "  Save"]
             :on-click #(rf/dispatch [::events/put-supplies id])]
            [rc/gap :size "1"]]]]]
        [rc/gap :size "1"]]]]]))

(defn supplies []
  (if-let [supplies @(rf/subscribe [::ablu/supplies])]
    (if-let [active-supply @(rf/subscribe [::ui/supply])]
      (supply active-supply supplies)
      [rc/v-box :children
       [(if supplies
          (for [[id supply] supplies]
            (supplies-row id supply))
          [:div])]])
    [rc/p "There are no supplies."]))

(defn pages [page]
  (case page
    ::home [home]
    ::bookings [bookings]
    ::confirms [confirms]
    ::laundry [laundry]
    ::schedules [schedules]
    ::supplies [supplies]
    [home]))

(defn margins [child]
  [rc/h-box
   :src (rc/at)
   :children
   [[rc/gap :size "1"]
    child
    [rc/gap :size "1"]]])

(defn main []
  (let [active-page (rf/subscribe [::ui/page])]
    (margins
     [rc/v-box
      :src (rc/at)
      :height "100%"
      :size "1 0 400px"
      :children [[header]
                 [pages @active-page]
                 [footer]]])))
