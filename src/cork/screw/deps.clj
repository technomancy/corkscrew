(ns cork.screw.deps
  (:require [cork.screw.deps.maven :as maven])
  (:use [cork.screw.utils])
  (:use [clojure.contrib.java-utils :only [file]])
  (:gen-class))

(def *force-fetch* false)
(def corkscrew-dir (file (System/getProperty "user.home") ".corkscrew"))

(defmulti fetch-source-dependency
  "Takes a list of name, version, type, and url of a dependency. Downloads
 it if necessary and returns a string of where it exists on disk. The string
could refer to a jar file or a directory of the unpacked project."
  #(% 2)) ;; methods are defined in deps/git.clj etc.

(defn handle-project-dependencies [project]
  ;; TODO: replace println with logger
  (println "Handling dependencies for" (:name project) "in" (:root project))
  (.mkdirs (file (:root project) "target" "dependency"))
  (maven/handle-dependencies project)
  (doseq [dependency (or (:source-dependencies project) [])]
    (println "Handling source:" dependency)
    ;; TODO: option to AOT-compile source dependencies
    ;; TODO: report errors during checkout
    (copy-file (file (fetch-source-dependency dependency) "src")
               (file (:root project) "target" "dependency"))))

(defn -main
  "Fetch and unpack all the dependencies."
  ([] (-main "."))
  ([target-dir]
     (handle-project-dependencies
      (read-project (file target-dir "project.clj")))))

(require 'cork.screw.deps.svn)
(require 'cork.screw.deps.git)

