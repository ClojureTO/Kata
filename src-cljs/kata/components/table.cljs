(ns kata.components.table
  (:require [reagent.core :as r]
            [secretary.core :as secretary]
            [kata.util :as util]))

(defn filter-problems [search-text problems]
  (filter
    (fn [{:keys [title]}]
      (or (nil? search-text)
          (util/str-contains? title search-text)))
    problems))

(defn td [id title]
  [:td
   {:on-click #(do
                (println "loading problem" id)
                (secretary/dispatch! (str "#/problem/" id)))}
   title])

(defn problem-link [{:keys [id title submitted difficulty submitted-by times-solved solved]}]
  [:tr.problem-link
   [td id title]
   [td id submitted]
   [td id difficulty]
   [td id submitted-by]
   [td id times-solved]
   [td id
    (if solved [:i.fa.fa-check-square-o]
               [:i.fa.fa-square-o])]])

(defn update-sort-value [table-state sort-key]
  (println "sorting by" sort-key)
  (if (= sort-key (:sort-key @table-state))
    (swap! table-state update :ascending not)
    (swap! table-state assoc :ascending true))
  (swap! table-state assoc :sort-key sort-key))

(defn sorted-contents [table-state]
  (let [sorted-contents (sort-by (:sort-key @table-state) (:rows @table-state))]
    (if (:ascending @table-state)
      sorted-contents
      (rseq sorted-contents))))

(defn header [title sort-key table-state]
  [:th {:on-click #(update-sort-value table-state sort-key)} title])

(defn table-body [problems]
  [:tbody
   (for [problem problems]
     ^{:key (:id problem)}
     [problem-link problem])])

(defn problem-table [problems]
  (let [table-state (r/atom {:sort-key  :difficulty
                             :ascending true
                             :rows      problems})]
    (fn []
      [:table.table-striped.table
       [:thead
        [:tr
         [header "Title" :title table-state]
         [header "Submitted" :submitted table-state]
         [header "Difficulty" :difficulty table-state]
         [header "Submitted By" :submitted-by table-state]
         [header "Times Solved" :times-solved table-state]
         [header "Solved" :solved table-state]]]
       [table-body (sorted-contents table-state)]])))
