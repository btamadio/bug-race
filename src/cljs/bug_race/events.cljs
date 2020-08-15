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

; https://github.com/district0x/re-frame-interval-fx
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
      :dispatch-n (list [::set-bug-speed 0] [::set-bug-speed 1] [::set-bug-speed 2] [::set-bug-speed 3])
      ::dispatch-interval {:dispatch [::tick]
                           :id :game-ticker
                           :ms 10}})))

(re-frame/reg-event-fx
 ::end-race
 (fn [cofx _]
   {::clear-interval {:id :game-ticker}}))

(def speeds
  {:slow 5
   :normal 10
   :fast 15})

(defn move-bug
  [game-speed bug-speed]
  (fn [x] (+ x (* bug-speed (game-speed speeds)))))

(re-frame/reg-event-db
 ::move-bug
 (fn [db [_ id]]
   (update-in db [:lanes id :position] (move-bug (:race-speed db) (get-in db [:lanes id :speed])))))

(defn winner
  [positions]
  (first (keep-indexed #(when (> %2 400) %1) positions)))

(re-frame/reg-event-fx
 ::tick
 (fn [cofx _]
   (let [positions (mapv :position (get-in cofx [:db :lanes]))
         winner-id (winner positions)]
     (if winner-id
       {:dispatch [::end-race]}
       {:dispatch-n (list [::move-bug 0] [::move-bug 1] [::move-bug 2] [::move-bug 3])}))))

(re-frame/reg-cofx
 :random-int
 (fn [coeffects val]
   (assoc coeffects :random-int (rand-int val))))

(re-frame/reg-event-fx
 ::set-bug-speed
 [(re-frame/inject-cofx :random-int 100)]
 (fn [cofx [_ id]]
   (let [db (:db cofx)
         rand-int (:random-int cofx)]
     {:db (assoc-in db [:lanes id :speed] (/ rand-int 1000))})))


