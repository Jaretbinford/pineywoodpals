(ns pineywoodpals.client
  (:require [reagent.core :as r]))

(defn root-component []
  [:div [:h1 "Hello world!"]])

(defn ^:export mount-root []
  (r/render [root-component]
            (.getElementById js/document "app")))

