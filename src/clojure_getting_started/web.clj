(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [hiccup.core :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import [java.lang.Integer]))

(defn load-wordlist-file [path-with-extension]
  (with-open [reader (io/reader path-with-extension)]
    (doall
     (csv/read-csv reader))))

()

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (html [:h1 "Howdy, partner"])})

(defroutes app
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
