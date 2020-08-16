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
                       :value @(subscribe [::subs/bug-name lane])
                       :on-change #(dispatch [::events/set-lane-name lane (-> % .-target .-value)])}]]]]]))

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
  [:div.control.mb-2
   [:button.button.is-primary.is-large
    {:on-click #(dispatch [::events/start-race])
     :class (if (or
                 (nil? @(subscribe [::subs/winner-guess]))
                 (= @(subscribe [::subs/race-stage]) :racing)
                 @(subscribe [::subs/duplicate-name?])
                 @(subscribe [::subs/duplicate-icon?]))
              :is-hidden)}
    "Start!"]])

(defn control-panel []
  [:div.columns
   [:div.column.is-6
    [:nav.panel
     [:p.panel-heading "Race Setup"]
     [:div.panel-block
      [speed-radio]]
     (for [lane [0 1 2 3]]
       ^{key lane}
       [:div.panel-block
        [lane-form lane]])]]
   [:div.column.is-4
    [race-button]
    (when @(subscribe [::subs/duplicate-name?])
      [:div.notification.is-warning "Please enter a unique name for each bug"])
    (when (nil? @(subscribe [::subs/winner-guess]))
      [:div.notification.is-warning "Pick a winner before starting the race!"])
    (when @(subscribe [::subs/duplicate-icon?])
      [:div.notification.is-warning "Please choose a different bug for each lane"])]])

(defn winner-guess-radio
  [id]
  (let [winner-guess @(subscribe [::subs/winner-guess id])
        race-stage @(subscribe [::subs/race-stage])
        on-click (when (= race-stage :pre-race) #(dispatch [::events/set-winner-guess id]))]
    [:div.box.is-child.has-text-centered {:key id}
     [:div.control
      [:input {:type :radio
               :name :winner-guess
               :checked (= winner-guess id)
               :on-click on-click}]]]))

(defn bug-img
  [id]
  ^{:key id}
  [:img {:src (bug-icons @(subscribe [::subs/lane-icon id]))
         :style {:position :absolute
                 :left @(subscribe [::subs/lane-position id])}}])

(defn race-track []
  [:div.tile.is-ancestor
   [:div.tile.is-1.is-parent.is-vertical
    [:div.tile.is-child
     (for [id [0 1 2 3]]
      [winner-guess-radio id])]]
   [:div#race-track.tile.is-8.box.is-child
    [:figure.image.is-48x48.mb-5.mt-1
     [bug-img 0]]
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

(defn winner-modal
  [winner-id]
  [:div.modal.is-active
   [:div.modal-background {:on-click #(dispatch [::events/reset-game winner-id])}]
   [:div.modal-content
    [:div.box
     [:p (str winner-id " WINS!")]]]
   [:button.modal-close.is-large {:aria-label "close"
                                  :on-click #(dispatch [::events/reset-game winner-id])}]])

(defn main-panel []
  [:div
   (when-let [winner-id @(subscribe [::subs/winner])]
     [winner-modal winner-id])
   [race-track]
   [control-panel]])
