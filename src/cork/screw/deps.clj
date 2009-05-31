(ns cork.screw.deps
  (:require [cork.screw.deps.maven :as maven])
  (:use [cork.screw.utils])
  (:use [clojure.contrib.shell-out :only [sh]]
        [clojure.contrib.java-utils :only [file]])
  (:gen-class))

(def *force-fetch* false)
(def corkscrew-dir (str (System/getProperty "user.home") "/.corkscrew/"))

(defmulti fetch-source-dependency
  "Takes a list of name, version, type, and url of a dependency. Downloads
 it if necessary and returns a string of where it exists on disk. The string
could refer to a jar file or a directory of the unpacked project."
  #(% 2))

(defn copy-dependency [dep-file root]
  (println "Copying source for: " dep-file)
  ;; TODO: write cp -r in Clojure
  (doseq [dir (.list (file "/src/" dep-file))]
    (sh "cp" "-r" dir root)))

(defn handle-project-dependencies [project]
  (maven/handle-dependencies project)
  (doseq [dependency (:source-dependencies project)]
    (println "Handling: " dependency)
    (copy-dependency (fetch-source-dependency dependency)
                       (str (:root project) "/target/dependency/"))))

(defn -main
  "Fetch and unpack all the dependencies."
  ([] (-main "."))
  ([target-dir]
     (handle-project-dependencies
      (read-project (str target-dir "/project.clj")))))

(require 'cork.screw.deps.svn)
(require 'cork.screw.deps.git)

