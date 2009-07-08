(ns cork.screw.pom
  (:use [clojure.contrib.duck-streams :only [writer]]
        [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [read-project]])
  (:require [clojure.xml :as xml])
  (:gen-class))

(defn dependency-xml
  "A tree structure (for XML conversion) representing a single dependency."
  [[artifact version & [group]]]
  {:tag :dependency :content [{:tag :artifactId :content [artifact]}
                              {:tag :groupId :content [(or group artifact)]}
                              {:tag :version :content [version]}]})

(defn repository-xml
  "A seq of trees represeting XML for repository list."
  [[id url]]
  {:tag :repository :content [{:tag :id :content [id]}
                              {:tag :url :content [url]}]})

(defn pom-for
  "A tree structure representing the project's POM for XML conversion."
  [project]
  (let [name (:name project)
        version (:version project)
        group (or (:group project) (:name project))
        dependencies (:dependencies project)
        repositories (:repositories project)]
    {:tag :project
     :attrs {:xmlns "http://maven.apache.org/POM/4.0.0"
             :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance"
             :xsi:schemaLocation (str "http://maven.apache.org/POM/4.0.0 "
                                      "http://maven.apache.org/xsd/maven-4.0.0.xsd")}
     :content [{:tag :modelVersion :content ["4.0.0"]}
               {:tag :artifactId :content [name]}
               {:tag :name :content [name]}
               {:tag :groupId :content [group]}
               ;; TODO: allow custom version of clojure-pom
               {:tag :parent
                :content [{:tag :artifactId :content ["clojure-pom"]}
                          {:tag :groupId :content ["org.clojure"]}
                          {:tag :version :content ["1.0.0"]}]}
               {:tag :version :content [version]}
               {:tag :dependencies
                :content (map dependency-xml dependencies)}
               {:tag :repositories
                :content (map repository-xml
                              (assoc repositories "technomancy"
                                     "http://repo.technomancy.us/"))}]}))

(defn write-pom [project]
  (binding [*out* (writer (str (:root project) "/pom.xml"))
            println print]
    (xml/emit (pom-for project))
    (flush)))

(defn -main
  ([] (-main "."))
  ([target-dir]
     (write-pom (read-project (file target-dir "project.clj")))))