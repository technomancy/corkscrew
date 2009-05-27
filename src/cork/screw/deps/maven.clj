(ns cork.screw.deps.maven
  (:require [clojure.xml :as xml])
  (:use [clojure.contrib.duck-streams :only [spit writer]]
        [clojure.contrib.shell-out :only [with-sh-dir sh]]))

(defn maven-dep? [dep]
  (= :maven (dep 2)))

(defn dependency-xml [[artifact version _ group]]
  {:tag :dependency :content [{:tag :artifactId :content [artifact]}
                              {:tag :groupId :content [group]}
                              {:tag :version :content [version]}]})

(defn pom-for [name version group dependencies]
  {:tag :project
   :attrs {:xmlns "http://maven.apache.org/POM/4.0.0"
           :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
           :xsi:schemaLocation "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"}
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
              :content (map dependency-xml dependencies)}]})

(defn write-pom [project]
  (binding [*out* (writer (str (:root project) "/pom.xml"))
            println print]
    (xml/emit (pom-for (:name project)
                       (:version project)
                       (or (:group project) (:name project))
                       (filter maven-dep? (:dependencies project))))
    (flush)))

(defn handle-dependencies [project]
  (try
   (write-pom project)
   (with-sh-dir (:root project)
                (sh "mvn" "process-resources"))
   (finally (.delete (java.io.File. (str (:root project) "/pom.xml"))))))
