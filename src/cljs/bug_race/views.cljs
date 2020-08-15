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
        [:input.input {:type :text
                       :placeholder "name"
                       :on-blur #(dispatch [::events/set-lane-name lane (-> % .-target .-value)])}]]]]]))

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
                  :on-click #(dispatch [::events/set-game-speed 1])
                  :checked (= race-speed 1)}] " Slow"]
        [:label.radio
         [:input {:type :radio
                  :name "game-speed"
                  :on-click #(dispatch [::events/set-game-speed 2])
                  :checked (= race-speed 2)}] " Normal"]
        [:label.radio
         [:input {:type :radio
                  :name "game-speed"
                  :on-click #(dispatch [::events/set-game-speed 4])
                  :checked (= race-speed 4)}] " Fast"]]]]]))

(defn race-button []
  [:div.control.mb-6
   [:button.button.is-primary
    {:on-click #(dispatch [::events/start-race])
     :disabled (or
                @(subscribe [::subs/duplicate-name?])
                @(subscribe [::subs/duplicate-icon?]))}
    "Start!"]])

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
    (when @(subscribe [::subs/duplicate-name?])
      [:div.notification.is-warning "Please enter a unique name for each bug"])
    (when @(subscribe [::subs/duplicate-icon?])
      [:div.notification.is-warning "Please choose a different bug for each lane"])]])

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
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 0]))
            :style {:position :absolute
                    :left @(subscribe [::subs/lane-position 0])}}]]
    [:figure.image.is-48x48.mb-6
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 1]))
            :style {:position :absolute
                    :left @(subscribe [::subs/lane-position 1])}}]]
    [:figure.image.is-48x48.mb-6
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 2]))
            :style {:position :absolute
                    :left @(subscribe [::subs/lane-position 2])}}]]
    [:figure.image.is-48x48.mb-1
     [:img {:src (bug-icons @(subscribe [::subs/lane-icon 3]))
            :style {:position :absolute
                    :left @(subscribe [::subs/lane-position 3])}}]]]])

(defn main-panel []
  [:div
   [race-track]
   [control-panel]])
