(ns cork.screw.deps
  (:require [cork.screw.deps.maven :as maven])
  (:use [cork.screw.utils])
  (:use [clojure.contrib.shell-out :only [sh]])
  (:gen-class))

(def *force-fetch* false)
(def corkscrew-dir (str (System/getProperty "user.home") "/.corkscrew/"))

(defmulti fetch-dependency
  "Takes a list of name, version, type, and url of a dependency. Downloads
 it if necessary and returns a string of where it exists on disk. The string
could refer to a jar file or a directory of the unpacked project."
  #(% 2))

(defn unpack-dependency [dep-file root]
  (println "Unpacking: " dep-file)
  (if (.isDirectory dep-file)
    ;; TODO: need to copy src/* instead
    (sh "cp" "-r" (str dep-file "/src/") root) ;; TODO: write cp -r in Clojure
    (extract-jar dep-file root)))

(defn non-maven-dep? [dep]
  (not= :maven (dep 2)))

(defn handle-project-dependencies [project]
  (doseq [dependency (filter non-maven-dep? (:dependencies project))]
    (println "Handling: " dependency)
    (unpack-dependency (fetch-dependency dependency)
                       (str (:root project) "/target/dependency/")))
  (maven/handle-dependencies project))

(defn -main
  "Fetch and unpack all the dependencies."
  ([] (-main "."))
  ([target-dir]
     (handle-project-dependencies
      (read-project (str target-dir "/project.clj")))))

(require 'cork.screw.deps.http)
(require 'cork.screw.deps.svn)
(require 'cork.screw.deps.git)

