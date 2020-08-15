(ns bug-race.db)

(def default-db
  {:race-speed :normal
   :race-stage :pre-race
   :winner-guess nil
   :lanes [{:position 0 :name nil :icon 0 :speed nil}
           {:position 0 :name nil :icon 1 :speed nil}
           {:position 0 :name nil :icon 2 :speed nil}
           {:position 0 :name nil :icon 3 :speed nil}]})
