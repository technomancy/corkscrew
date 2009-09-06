(ns cork.screw.pom
  (:use [clojure.contrib.duck-streams :only [writer]]
        [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [read-project]]
        [cork.screw.deps.maven :only [make-model]])
  (:import [org.apache.maven.model Model]
           [org.apache.maven.project MavenProject])
  (:gen-class))

(defn write-pom [project]
  (.writeModel (MavenProject. (make-model project))
               (writer (file (:root project) "pom.xml"))))

(defn -main
  ([] (-main "."))
  ([target-dir]
     (write-pom (read-project (file target-dir "project.clj")))
     (println "Wrote pom.xml to " target-dir ".")))