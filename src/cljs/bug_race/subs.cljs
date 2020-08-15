(ns bug-race.subs
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as str]))

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
 ::lane-icons
 :<- [::lanes]
 (fn [lanes [_ _]]
   (mapv :icon lanes)))

(re-frame/reg-sub
 ::lane-names
 :<- [::lanes]
 (fn [lanes [_ _]]
   (mapv :name lanes)))

(re-frame/reg-sub
 ::lane-positions
 :<- [::lanes]
 (fn [lanes [_ _]]
   (mapv :position lanes)))

(re-frame/reg-sub
 ::duplicate-name?
 :<- [::lane-names]
 (fn [lane-names [_ _]]
   (or
    (some str/blank? lane-names)
    (not= (distinct lane-names) lane-names))))

(re-frame/reg-sub
 ::lane-icon
 :<- [::lane-icons]
 (fn [lane-icons [_ id]]
   (lane-icons id)))

(re-frame/reg-sub
 ::lane-position
 :<- [::lane-positions]
 (fn [lane-positions [_ id]]
   (lane-positions id)))

(re-frame/reg-sub
 ::duplicate-icon?
 :<- [::lane-icons]
 (fn [lane-icons [_ _]]
   (not= (distinct lane-icons) lane-icons)))


