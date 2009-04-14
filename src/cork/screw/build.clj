(ns cork.screw.build
  (:use [cork.screw])
;;  (:use [cork.screw.deps :as deps])
  (:gen-class))

;; (defn build-package [package]
;;   (if (empty? (.listFiles (java.io.File. (str (:root package) "/classes/"))))
;;     (unpack-package-dependencies package))
;;   (compile (:main package))
;;   (jar-classes))

(defn -main [])