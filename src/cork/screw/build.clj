(ns cork.screw.build
  (:use [cork.screw])
;;  (:use [cork.screw.deps :as deps])
  (:gen-class))

;; (defn build-project [project]
;;   (if (empty? (.listFiles (java.io.File. (str (:root project) "/classes/"))))
;;     (unpack-project-dependencies project))
;;   (compile (:main project))
;;   (jar-classes))

(defn -main [])