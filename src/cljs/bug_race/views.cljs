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

(defn winner-guess-button
  [id]
  (let [winner-guess @(subscribe [::subs/winner-guess id])
        selected? (= winner-guess id)
        race-stage @(subscribe [::subs/race-stage])
        racing? (= race-stage :racing)
        on-click (when (not racing?) #(dispatch [::events/set-winner-guess id]))]
    [:div.control
     [:button.button {:on-click on-click
                      :class (if selected?
                               :is-dark
                               :is-light)
                      :disabled (and racing? (not selected?))}
      (str "Lane " (inc id))]]))

(defn speed-button
  [speed]
  (let [race-speed @(subscribe [::subs/race-speed])
        selected? (= race-speed speed)
        on-click #(dispatch [::events/set-game-speed speed])]
    [:button.button {:on-click on-click
                     :class (if selected? :is-dark :is-light)}
     speed]))

(defn speed-buttons
  []
  [:div.buttons.has-addons.is-centered
   [speed-button :slow]
   [speed-button :normal]
   [speed-button :fast]])

(defn lane-form
  [lane]
  (let [bug-icon @(subscribe [::subs/lane-icon lane])]
    [:div.field.is-horizontal.is-grouped
     [winner-guess-button lane]     
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
                       :value @(subscribe [::subs/bug-name lane])
                       :on-change #(dispatch [::events/set-lane-name lane (-> % .-target .-value)])}]]]]]))



(defn race-button []
  [:button.button.is-danger.is-large
   {:on-click #(dispatch [::events/start-race])
    :class (if (or
                (nil? @(subscribe [::subs/winner-guess]))
                (= @(subscribe [::subs/race-stage]) :racing)
                @(subscribe [::subs/duplicate-name?])
                @(subscribe [::subs/duplicate-icon?]))
             :is-hidden)}
   "Start!"])

(defn control-panel []
  [:div.columns.is-centered.is-vcentered
   [:div.column.is-2
        (when (nil? @(subscribe [::subs/winner-guess]))
      [:div.notification.is-info "Guess a winner!"])]
   [:div.column.is-6
    [:nav.panel.has-text-centered
     [:p.panel-heading "Race Setup"]
     [:div.panel-block
      [:div.column.is-3]
      [speed-buttons]]
     (for [lane [0 1 2 3]]
       ^{key lane}
       [:div.panel-block
        [lane-form lane]])]]
   [:div.column.is-2
    [race-button]
    (when @(subscribe [::subs/duplicate-name?])
      [:div.notification.is-info "Give each bug a unique name"])
    (when @(subscribe [::subs/duplicate-icon?])
      [:div.notification.is-warning "Please choose a different bug for each lane"])]])


(defn bug-img
  [id]
  (when (= @(subscribe [::subs/race-stage]) :racing)
    ^{:key id}
    [:img {:src (bug-icons @(subscribe [::subs/lane-icon id]))
           :style {:position :absolute
                   :left @(subscribe [::subs/lane-position id])}}]))

(defn race-track []
  [:div.columns.is-centered.is-vcentered
   [:div.column.is-11
    [:div#race-track.box
     [:figure.image.is-48x48.mb-5.mt-1
      [bug-img 0]]
     [:figure.image.is-48x48.mb-6
      [bug-img 1]]
     [:figure.image.is-48x48.mb-6.mt-1
      [bug-img 2]]
     [:figure.image.is-48x48.mb-5.mt-1
      [bug-img 3]]]]])

(defn winner-modal
  [winner-id]
  (let [winner-name @(subscribe [::subs/bug-name winner-id])
        winner-icon-id @(subscribe [::subs/lane-icon winner-id])]
    [:div.modal.is-active
     [:div.modal-background {:on-click #(dispatch [::events/reset-game winner-id])}]
     [:div.modal-content
      [:div.box
       [:figure.image.is-48x48
        [:img {:src (bug-icons winner-icon-id)}]]
       [:p (str "Winner: " winner-name)]]]
     [:button.modal-close.is-large {:aria-label "close"
                                    :on-click #(dispatch [::events/reset-game winner-id])}]]))

(defn main-panel []
  [:div
   (when-let [winner-id @(subscribe [::subs/winner])]
     [winner-modal winner-id])
   [race-track]
   [control-panel]])
