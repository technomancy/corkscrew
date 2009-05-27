(ns cork.screw.deps.maven
  (:require [clojure.xml :as xml])
  (:use [clojure.contrib.duck-streams :only [spit writer]]))

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
  (binding [*out* (writer (str "/tmp/clojure-" (:name project)
                               "-pom.xml"))]
    (xml/emit (pom-for (:name project)
                       (:version project)
                       (or (:group project) (:name project))
                       (filter maven-dep? (:dependencies project))))))

(defn handle-dependencies [project]
  )

;; (handle-dependencies {:name "sample" :version "1.0.0" :root "/tmp/sample"
;;                       :dependencies [{:name "clojure-contrib" :group "org.clojure"
;;                                       :version "1.0-SNAPSHOT"}
;;                                      {:name "rome" :group "rome" :version "1.0"}
;;                                      {:name "tagsoup" :group "tagsoup" :version "1.2"}]})