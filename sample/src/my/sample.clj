(ns my.sample
  (:use [clojure.contrib duck-streams]
        [net.cgrand enlive-html])
  (:import [org.jdom.input TextBuffer]
           [com.sun.syndication.io SyndFeedInput])
  (:gen-class))

(defn -main [& args]
  (println (if (empty? args)
             "No args."
             "You had some arrrrrghs?")))