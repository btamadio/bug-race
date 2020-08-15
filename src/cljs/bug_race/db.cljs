(ns bug-race.db)

(def default-db
  {:race-speed :normal
   :race-stage :pre-race
   :winner-guess nil
   :lanes [{:position 0 :name nil :icon nil :speed nil}
           {:position 0 :name nil :icon nil :speed nil}
           {:position 0 :name nil :icon nil :speed nil}
           {:position 0 :name nil :icon nil :speed nil}]})
