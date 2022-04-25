(ns ae.ablution.ui.events
  (:require [ae.ablution :as-alias ablu]
            [ae.ablution.ui :as-alias ui]
            [ae.ablution.ui.db :as db]
            [ae.ablution.ui.router :as router]
            [ae.ablution.ui.views :as-alias views]
            [ae.ablution.supply :as-alias supply]
            [ae.ablution.entity :as-alias entity]
            [ae.ablution.customer :as-alias customer]
            [ae.ablution.property :as-alias property]
            [ae.ablution.ui.edn :as ajax]
            [clojure.string :as string]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [clojure.pprint :as pp]))

(def api-url "http://localhost:6003/api")

(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (string/join "/" (cons api-url params)))

(rf/reg-event-db
 ::api-request-failure
 (fn-traced
  [db [_ {::keys [request-type loading]} response]]
  (js/console.log (with-out-str (pp/pprint (get-in response [:response :errors]))))
  (-> db
      (assoc-in [::ui/errors request-type] (get-in response [:response :errors]))
      (assoc-in [::ui/loading (or loading request-type)] false))))

(rf/reg-fx
 ::set-url
 (fn-traced
  [{:keys [url]}]
  (router/set-token! url)))

(rf/reg-event-db
 ::initialise-db
 (fn-traced [_ _] db/default-db))

(rf/reg-event-fx
 ::set-active-page
 (fn-traced
  [{:keys [db]} [_ {:keys [::ui/page ::supply/id]}]]
  (let [set-page (assoc db ::ui/page page)]
    (case page
      ::views/home {:db set-page}
      ::views/bookings {:db set-page}
      ::views/confirms {:db set-page :dispatch [::get-jobs]}
      ::views/laundry {:db set-page}
      ::views/schedules {:db set-page}
      ::views/supplies
      {:db set-page
       :dispatch-n
       (if id
         [[::get-supplies] [::set-active-supply id]]
         [[::get-supplies] [::reset-active-supply]])}))))

(rf/reg-event-fx
 ::get-jobs
 (fn-traced
  [{:keys [db]} [_ params]]
  {:db (-> db (assoc-in [::ui/loading ::ablu/jobs] true))
   :http-xhrio
   {:method :get
    :uri (endpoint "jobs")
    :params params
    :response-format (ajax/edn-response-format)
    :on-success [::get-jobs-success]
    :on-failure [::api-request-failure
                 {::request-type ::get-jobs
                  ::ui/loading ::ablu/jobs}]}}))

(rf/reg-event-db
 ::get-jobs-success
 (fn-traced
  [db [_ jobs]]
  (assoc db ::ablu/jobs jobs)))

(rf/reg-event-fx
 ::get-supplies
 (fn-traced
  [{:keys [db]} [_ params]]
  {:db (-> db (assoc-in [::ui/loading ::ablu/supplies] true))
   :http-xhrio
   {:method :get
    :uri (endpoint "supplies")
    :params params
    :response-format (ajax/edn-response-format)
    :on-success [::get-supplies-success]
    :on-failure [::api-request-failure
                 {::request-type ::get-supplies
                  ::ui/loading ::ablu/supplies}]}}))

(rf/reg-event-db
 ::get-supplies-success
 (fn-traced
  [db [_ spcs]]
  (let [spc->supp (fn [[s p c]]
                    (-> s
                        (merge (select-keys p [::property/name])
                               (select-keys c [::customer/name]))
                        (assoc ::customer/id (::entity/id c))))
        supplies (map spc->supp spcs)]
    (-> db
        (assoc-in [::ui/loading ::ablu/supplies] false)
        (assoc ::ablu/supplies (->> supplies
                                    (group-by ::entity/id)
                                    (map (fn [[id os]] [id (first os)]))
                                    (into {})))))))

(rf/reg-event-db
 ::reset-active-supply
 (fn-traced
  [db _]
  (dissoc db ::ui/supply)))

(rf/reg-event-db
 ::set-active-supply
 (fn-traced
  [db [_ supply-id]]
  (assoc db ::ui/supply supply-id)))

(rf/reg-event-db
 ::update-supply-model
 (fn-traced
  [db [_ {:keys [::entity/id] :as s}]]
  (assoc-in db [::ablu/supplies id] s)))

(rf/reg-event-fx
 ::put-supplies
 (fn-traced
  [{:keys [db]} [_ id]]
  (let [supplies (get-in db [::ablu/supplies id])]
    (js/console.log db)
    {:db db
     :http-xhrio
     {:method :post
      :uri (endpoint "supplies")
      :params {::ablu/supplies supplies}
      :format (ajax/edn-request-format)
      :response-format (ajax/edn-response-format)
      ;; :headers {"Access-Control-Allow-Origin" "*"}
      :on-success [::put-supplies-success]
      :on-failure [::api-request-failure
                   {::request-type ::put-supplies}]}})))

(rf/reg-event-fx
 ::put-supplies-success
 (fn-traced
  [{:keys [db]} _]
  (js/console.log db)
  {:db db
   :dispatch [::get-supplies]}))
