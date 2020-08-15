(ns bug-race.events
  (:require
   [re-frame.core :as re-frame]
   [bug-race.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
