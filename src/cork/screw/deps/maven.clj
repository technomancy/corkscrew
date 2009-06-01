(ns cork.screw.deps.maven
  (:require [clojure.xml :as xml])
  (:use [clojure.contrib.duck-streams :only [spit writer]]
        [clojure.contrib.shell-out :only [with-sh-dir sh]]))

(defn dependency-xml
  "A tree structure (for XML conversion) representing a single dependency."
  [[artifact version & [group]]]
  {:tag :dependency :content [{:tag :artifactId :content [artifact]}
                              {:tag :groupId :content [(or group artifact)]}
                              {:tag :version :content [version]}]})

(defn pom-for
  "A tree structure representing the project's POM for XML conversion."
  [project]
  (let [name (:name project)
        version (:version project)
        group (or (:group project) (:name project))
        dependencies (:dependencies project)]
    {:tag :project
     :attrs {:xmlns "http://maven.apache.org/POM/4.0.0"
             :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
             :xsi:schemaLocation (str "http://maven.apache.org/POM/4.0.0 "
                                      "http://maven.apache.org/xsd/maven-4.0.0.xsd")}
     :content [{:tag :modelVersion :content ["4.0.0"]}
               {:tag :parent
                :content [{:tag :artifactId :content ["clojure-pom"]}
                          {:tag :groupId :content ["org.clojure"]}
                          {:tag :version :content ["1.0-SNAPSHOT"]}]}
               {:tag :artifactId :content [name]}
               {:tag :name :content [name]}
               {:tag :groupId :content [group]}
               {:tag :version :content [version]}
               {:tag :dependencies
                :content (map dependency-xml dependencies)}]}))

(defn write-pom [project]
  (binding [*out* (writer (str (:root project) "/pom.xml"))
            println print]
    (xml/emit (pom-for project))
    (flush)))

(defn handle-dependencies [project]
  (when-not (empty? (:dependencies project))
    (try
     (write-pom project)
     (with-sh-dir (:root project)
                  (sh "mvn" "process-resources"))
     (finally (.delete (java.io.File. (str (:root project) "/pom.xml")))))))
