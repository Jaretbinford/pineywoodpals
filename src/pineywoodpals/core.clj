(ns pineywoodpals.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;Compojure

(ns hello-world.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(defroutes app
           (GET "/" [] "<h1>Hello World</h1>")
           (route/not-found "<h1>Page not found</h1>"))

;Datomic
(require '[datomic.client.api :as d])

(def cfg {:server-type :ion
          :region "us-east-1" ;; e.g. us-east-1
          :system "toy-solo-4"
          :endpoint "http://entry.toy-solo-4.us-east-1.datomic.net:8182/"
          :proxy-port 8182})

(def client (d/client cfg))

;;create and connect to DB
(d/create-database client {:db-name "pineywoodpals"})
(d/list-databases client {})

(def conn (d/connect client {:db-name "pineywoodpals"}))


;sandbox functions

(require '[clj-time.core :as time])

(declare in-same-month?)
(declare first-day-of-week)
(declare weekyears-between)

(defn week-calendar
  "Given a starting date this function will return a LazySeq of the enclosing date
   for the same month of the start-date.  Week days that are not in the the same
   month will be nil"
  ([start-date day-handler]
   (let [start-of-week (first-day-of-week start-date)]
     (for [day-offset (range 0 7)
           :let [current-day (time/plus start-of-week (time/days day-offset))]]
       (if (in-same-month? start-date current-day)
         (day-handler current-day)
         nil)))))

(defn month-calendar
  "for a given date or month/year combo this will return a representation of that month"
  ([date day-handler] (month-calendar (time/year date) (time/month date) day-handler))
  ([year month day-handler]
   (let [first-day-of-month (time/date-time year month 1)
         last-day-of-month  (time/last-day-of-the-month first-day-of-month)
         weeks-covered      (weekyears-between first-day-of-month last-day-of-month)]
     (for [week (range 0 (inc weeks-covered))
           :let [current-week  (time/plus first-day-of-month (time/weeks week))
                 ; the first week of the month we want to start at the 1st.  every other week
                 ; we want to start at the first day of the week
                 starting-from (if (= week 0)
                                 first-day-of-month
                                 (first-day-of-week current-week))]]
       (fill-week-of-month starting-from day-handler)))))

(defn year-by-month-calendar
  "for a given year this will generate a calendar covering each month"
  [year day-handler]
  (for [month (range 1 13)]
    (month-calendar year month day-handler)))

(defn- in-same-month?
  "compares 2 dates and returns true if they are in the same month and year"
  ([first-date second-date]
   (and (= (time/month first-date) (time/month second-date))
        (= (time/year first-date) (time/year second-date)))))

(defn- first-day-of-week [date]
  (.withDayOfWeek date 1))

(defn- weekyears-between [start end]
  (- (.getWeekOfWeekyear end)
     (.getWeekOfWeekyear start)))


