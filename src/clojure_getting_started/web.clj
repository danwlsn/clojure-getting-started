(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [hiccup.core :refer :all]
            [hiccup.element :refer :all]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.data.csv :as csv]))

(def default-file-location "./data/wordlist.csv")

(defn load-wordlist-file [path-with-extension]
  (with-open [reader (io/reader path-with-extension)]
    (doall
     (csv/read-csv reader))))

(defn wordlist-numbered-vec [file-location]
  (reduce (fn [acc i]
            (assoc acc (Integer/parseInt (first i)) (second i)))
          {}
          (load-wordlist-file file-location)))

(defn dice-roll->word [roll file-location]
  (get (wordlist-numbered-vec file-location) roll))

(defn dice-roll [sides]
  (inc (rand-int sides)))

(defn roll-multiple-dice [num-dice]
  (repeatedly num-dice #(dice-roll 5)))

(defn get-key-from-dice []
  (Integer/parseInt (clojure.string/join "" (roll-multiple-dice 5))))

(defn get-random-word []
  (get (wordlist-numbered-vec default-file-location) (get-key-from-dice)))

(defn multi-dice-roll [num]
  (repeatedly num #(get-key-from-dice)))

(defn multi-random-word [num]
  (String/join " " (map (wordlist-numbered-vec default-file-location) (multi-dice-roll num))))

(defn link [math length]
(link-to (str "/" (math length 10)) (str (math length 10) " word password")))

(defn html-body [num]
(html
 [:h1 "Your password is"]
 [:h2 (multi-random-word num)]
 [:h3 "Not the right length for you?"]
 (if (> num 11)
   (link - num))
 [:br]
 (link + num)
 ))


(defn splash [num]
{:status 200
 :headers {"Content-Type" "text/html"}
 :body (html-body num)})

(defroutes app
(GET "/" []
     (splash 5))
(GET "/:num" [num]
     (splash (Integer/parseInt num)))
(ANY "*" []
     (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
(let [port (Integer. (or port (env :port) 5000))]
  (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
