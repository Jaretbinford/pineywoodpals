(ns pineywoodpals.handler
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [prone.middleware :refer [wrap-exceptions]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [pineywoodpals.views :refer [render-page]]))

(defroutes routes
  (GET "/" [] (render-page))
  (not-found "Not Found"))

(def development-app (wrap-reload
                      (wrap-exceptions
                       (wrap-defaults #'routes site-defaults))))

(def production-app (wrap-defaults #'routes site-defaults))

