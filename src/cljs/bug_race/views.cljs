(ns bug-race.views
  (:require
   [re-frame.core :as re-frame]
   [bug-race.subs :as subs]
   ))

(def bug-icons
  ["images/ant.png"
   "images/bee.png"
   "images/butterfly.png"
   "images/ladybug.png"])

(defn lane-form
  [lane]
  [:div.field.is-horizontal
   [:div.field-label.is-normal
    [:label.label (str "Lane " lane)]]
   [:div.field-body
    [:div.field.is-grouped
     [:div.control
      (for [bug-icon bug-icons]
        [:label.radio
         [:figure.image.is-24x24
          [:img {:src bug-icon}]]
         [:input {:type :radio :name (str "icon-lane" lane)}]])]]
    [:div.field
     [:div.control
      [:input.input {:type :text :placeholder "name"}]]]]])

(defn speed-radio
  []
  [:div.field.is-horizontal
   [:div.field-label
    [:label.label "Speed"]]
   [:div.field-body
    [:div.field
     [:div.control
      [:label.radio
       [:input {:type :radio :name "game-speed"}] "Slow"]
      [:label.radio
       [:input {:type :radio :name "game-speed"}] "Normal"]
      [:label.radio
       [:input {:type :radio :name "game-speed"}] "Fast"]]]]])

(defn race-button []
  [:button.button "Start!"])

(defn control-panel []
  [:div.columns
   [:div.column.is-6
    [:nav.panel
     [:p.panel-heading "Race Setup"]
     [:div.panel-block
      [speed-radio]]
     [:div.panel-block
      [lane-form 1]]
     [:div.panel-block
      [lane-form 2]]
     [:div.panel-block
      [lane-form 3]]
     [:div.panel-block
      [lane-form 4]]]]
   [:div.column
    [race-button]]])

(defn race-track []
  [:div.tile.is-ancestor
   [:div.tile.is-1.is-parent.is-vertical
    [:div.tile.is-child.box.pt-6
     [:input.my-5 {:type :radio :name :winner-guess}]
     [:input.my-5 {:type :radio :name :winner-guess}]
     [:input.my-5 {:type :radio :name :winner-guess}]
     [:input.my-5 {:type :radio :name :winner-guess}]]]
   [:div#race-track.tile.is-8.box.is-child
    [:figure.image.is-48x48.my-5
     [:img {:src (bug-icons 0)}]]
    [:figure.image.is-48x48.my-5
     [:img {:src (bug-icons 1)}]]
    [:figure.image.is-48x48.my-5
     [:img {:src (bug-icons 2)}]]
    [:figure.image.is-48x48.my-5
     [:img {:src (bug-icons 3)}]]]])

(defn main-panel []
  [:body
   [race-track]
   [control-panel]])
