(ns cork.screw.deps.maven
  (:require [clojure.xml :as xml])
  ;; (:import [org.apache.maven.cli MavenCli])
  (:use [clojure.contrib.duck-streams :only [spit writer]]
        [clojure.contrib.shell-out :only [sh]]
        [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [with-preserving-file]]))

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
                :content (map dependency-xml dependencies)}
               {:tag :repositories
                :content [{:tag :repository
                           :content [{:tag :id :content ["technomancy"]}
                                     {:tag :url :content
                                      ["http://repo.technomancy.us/maven2"]}]}
                          {:tag :repository
                           :content [{:tag :id :content ["central"]}
                                     {:tag :url :content
                                      ["http://repo1.maven.org/maven2"]}]}]}]}))

(defn write-pom [project]
  (binding [*out* (writer (str (:root project) "/pom.xml"))
            println print]
    (xml/emit (pom-for project))
    (flush)))

;; TODO: Shelling out to mvn is laaame. We should eventually be able
;; to call the Maven API from Java, possibly by constructing a
;; MavenCli if all else fails.
(defn handle-dependencies [project]
  (when-not (empty? (:dependencies project))
    (with-preserving-file (file (:root project) "pom.xml")
      (write-pom project)
      ;; (MavenCli/main (make-array String "-f" (str (:root project) "/pom.xml")
      ;;                            "process-resources"))
      ;; TODO: need to report errors here.
      (sh "mvn" "-f" (str (:root project) "/pom.xml") "process-resources"))))
