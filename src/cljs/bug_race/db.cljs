(ns bug-race.db)

(def default-db
  {:race-speed :normal
   :race-stage :pre-race
   :winner-guess nil
   :winner nil
   :lanes [{:position 0 :name nil :icon 0 :speed 1}
           {:position 0 :name nil :icon 1 :speed 1}
           {:position 0 :name nil :icon 2 :speed 1}
           {:position 0 :name nil :icon 3 :speed 1}]})
