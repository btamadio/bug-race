(ns bug-race.events
  (:require
   [re-frame.core :as re-frame]
   [bug-race.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::select-icon
 (fn [db [_ lane bug-index]]
   (assoc-in db [:lanes lane :icon] bug-index)))

(re-frame/reg-event-db
 ::set-game-speed
 (fn [db [_ speed]]
   (assoc db :race-speed speed)))

(re-frame/reg-event-db
 ::set-lane-name
 (fn [db [_ lane name]]
   (assoc-in db [:lanes lane :name] name)))

(def registered-keys (atom nil))

(re-frame/reg-fx
 ::dispatch-interval
 (fn [{:keys [:dispatch :ms :id]}]
   (let [interval-id (js/setInterval #(re-frame/dispatch dispatch) ms)]
     (swap! registered-keys assoc id interval-id))))

(re-frame/reg-fx
 ::clear-interval
 (fn [{:keys [:id]}]
   (when-let [interval-id (get @registered-keys id)]
     (js/clearInterval interval-id)
     (swap! registered-keys dissoc id))))


(re-frame/reg-event-fx
 ::start-race
 (fn [cofx _]
   (let [db (:db cofx)]
     {:db (assoc db :race-stage :racing)
      ::dispatch-interval {:dispatch [::tick]
                           :id :game-ticker
                           :ms 250}})))

(defn move-bug
  [game-speed bug-speed]
  (fn [x] (+ x (* bug-speed game-speed))))

(re-frame/reg-event-db
 ::move-bug
 (fn [db [_ id]]
   (-> db
       (update-in [:lanes id :position] (move-bug (:race-speed db) (get-in db [:lanes id :speed]))))))

(re-frame/reg-event-fx
 ::tick
 (fn [_ _]
   {:dispatch-n (list [::move-bug 0] [::move-bug 1] [::move-bug 2] [::move-bug 3])}))
