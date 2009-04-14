(ns my.sample
  (:gen-class))

(defn -main [& args]
  (println (if (empty? args)
             "No args."
             "You had some arrrrrghs?")))