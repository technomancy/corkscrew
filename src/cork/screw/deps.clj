(ns cork.screw.deps
  (:use [cork.screw])
  (:use [clojure.contrib.shell-out :only [with-sh-dir sh]]
        [clojure.contrib.java-utils :only [file]])
  (:gen-class))

(def *force-fetch* false)
;; (def corkscrew-dir (str (System/getProperty "user.home") "/.corkscrew/"))

(defmulti fetch-dependency #(% 2))
(defmulti unpack-dependency (fn [dep root] (dep 2)))

(defn compile-checkout [root]
  (println "Compiling: " root)
  (with-sh-dir root "ant"))

(defn handle-project-dependencies [project]
  (doseq [dependency (:dependencies project)]
    (println "Handling: " dependency)
    (fetch-dependency dependency)
    (unpack-dependency dependency (:root project))))

(defn -main
  "Fetch and unpack all the dependencies into dependencies/."
  ([] (-main "."))
  ([target-dir]
     (handle-project-dependencies
      (read-project (str target-dir "/project.clj")))))

;; (require 'cork.screw.deps.svn)
;; (require 'cork.screw.deps.git)
;; (require 'cork.screw.deps.maven)
;; (require 'cork.screw.deps.http)
