(ns bug-race.events
  (:require
   [re-frame.core :as re-frame]
   [bug-race.db :as db]
   ))

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
