(ns bug-race.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [bug-race.subs :as subs]
   [bug-race.events :as events]))

(def bug-icons
  ["images/ant.png"
   "images/bee.png"
   "images/butterfly.png"
   "images/ladybug.png"])

(defn lane-form
  [lane]
  (let [bug-icon @(subscribe [::subs/lane-icon lane])]
    [:div.field.is-horizontal
     [:div.field-label.is-normal
      [:label.label (str "Lane " (inc lane))]]
     [:div.field-body
      [:div.field.is-grouped
       [:div.control
        (for [bug-index [0 1 2 3]]
          ^{:key bug-index} [:label.radio
                             [:figure.image.is-24x24
                              [:img {:src (bug-icons bug-index)}]]
                             [:input {:type :radio
                                      :checked (= bug-icon bug-index)
                                      :name (str "icon-lane" lane)
                                      :on-click #(dispatch [::events/select-icon lane bug-index])}]])]]
      [:div.field
       [:div.control
        [:input.input {:type :text :placeholder "name"}]]]]]))

(defn speed-radio
  []
  (let [race-speed @(subscribe [::subs/race-speed])]
    [:div.field.is-horizontal
     [:div.field-label
      [:label.label "Speed"]]
     [:div.field-body
      [:div.field
       [:div.control
        [:label.radio
         [:input {:type :radio
                  :name "game-speed"
                  :on-click #(dispatch [::events/set-game-speed :slow])
                  :checked (= race-speed :slow)}] " Slow"]
        [:label.radio
         [:input {:type :radio
                  :name "game-speed"
                  :on-click #(dispatch [::events/set-game-speed :normal])
                  :checked (= race-speed :normal)}] " Normal"]
        [:label.radio
         [:input {:type :radio
                  :name "game-speed"
                  :on-click #(dispatch [::events/set-game-speed :fast])
                  :checked (= race-speed :fast)}] " Fast"]]]]]))

(defn race-button []
  [:button.button "Start!"])

(defn unique-icon-notif []
  [:div.notification.is-warning 
   "Please select a different bug icon for each lane"])

(defn control-panel []
  [:div.columns
   [:div.column.is-6
    [:nav.panel
     [:p.panel-heading "Race Setup"]
     [:div.panel-block
      [speed-radio]]
     [:div.panel-block
      [lane-form 0]]
     [:div.panel-block
      [lane-form 1]]
     [:div.panel-block
      [lane-form 2]]
     [:div.panel-block
      [lane-form 3]]]]
   [:div.column.is-4
    [race-button]
    (when @(subscribe [::subs/duplicate-icon?])
      [unique-icon-notif])]])

(defn race-track []
  [:div.tile.is-ancestor
   [:div.tile.is-1.is-parent.is-vertical
    [:div.tile.is-child
     [:div.box.is-child.has-text-centered
      [:div.control
       [:input {:type :radio :name :winner-guess}]]]
     [:div.box.is-child.has-text-centered
      [:div.control
       [:input {:type :radio :name :winner-guess}]]]
     [:div.box.is-child.has-text-centered
      [:div.control
       [:input {:type :radio :name :winner-guess}]]]
     [:div.box.is-child.has-text-centered
      [:div.control
       [:input {:type :radio :name :winner-guess}]]]]]
   [:div#race-track.tile.is-8.box.is-child
    [:figure.image.is-48x48.mb-5.mt-1
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 0]))}]]
    [:figure.image.is-48x48.mb-6
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 1]))}]]
    [:figure.image.is-48x48.mb-6
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 2]))}]]
    [:figure.image.is-48x48.mb-1
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 3]))}]]]])

(defn main-panel []
  [:div
   [race-track]
   [control-panel]])
