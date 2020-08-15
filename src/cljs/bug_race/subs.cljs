(ns bug-race.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::race-speed
 (fn [db]
   (:race-speed db)))

(re-frame/reg-sub
 ::winner-guess
 (fn [db]
   (:winner-guess db)))

(re-frame/reg-sub
 ::race-stage
 (fn [db]
   (:race-stage db)))

(re-frame/reg-sub
 ::lanes
 (fn [db]
   (:lanes db)))

(re-frame/reg-sub
 ::lane-icon
 :<- [::lanes]
 (fn [lanes [_ id]]
   (:icon (lanes id))))
