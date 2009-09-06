(ns cork.screw.deps.maven
  (:import [org.apache.maven.model Model Parent Dependency Repository]
           [org.apache.maven.monitor.event EventMonitor]
           [org.apache.maven.wagon.observers ChecksumObserver]
           [org.apache.maven.project MavenProject]
           [org.apache.maven.embedder MavenEmbedder]
           [java.util Properties])
  (:use [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [read-project into-list]]))

;; Alternate definition from the example; probably noisier:
;; (def event-monitor (org.apache.maven.monitor.event.DefaultEventMonitor.
;;                     (org.apache.maven.embedder.PlexusLoggerAdapter.
;;                      (org.apache.maven.embedder.MavenEmbedderConsoleLogger.))))

(def event-monitor
     (proxy [EventMonitor] []
       (.startEvent [this event-name target timestamp])
       (.endEvent [this event-name target timestamp])
       (.errorEvent [this event-name target timestamp cause])))

(def transfer-listener (ChecksumObserver.))

(defn make-model [project]
  (let [model (doto (Model.)
                (.setModelVersion "4.0.0")
                (.setArtifactId (:name project))
                (.setName (:name project))
                (.setVersion (:version project))
                (.setGroupId (:group project))
                (.setParent (doto (Parent.)
                              (.setArtifactId "clojure-pom")
                              (.setGroupId "org.clojure")
                              (.setVersion "1.0.0")))
                (.setVersion (:version project)))]
    (doseq [[artifact version & [group]] (:dependencies project)]
      (.addDependency model (doto (Dependency.)
                              (.setArtifactId artifact)
                              (.setGroupId (or group artifact))
                              (.setVersion version))))
    (doseq [[id url] (:repositories project)]
      (.addRepository model (doto (Repository.)
                              (.setId id)
                              (.setUrl url)))
      ;; Not going to be very useful without access to clojure-pom!
      ;; Eventually a centralized Clojure repo can be set up and used here.
      (.addRepository model (doto (Repository.)
                              (.setId "technomancy")
                              (.setUrl "http://repo.technomancy.us"))))
    model))

(defn execute [project & goals]
  (let [maven (doto (MavenEmbedder.)
                ;; TODO: suspect plexus disaster is due to wrong classloader
                (.setClassLoader (.getContextClassLoader
                                  (Thread/currentThread)))
                   (.start))
        model (make-model project)
        maven-project (MavenProject. model)]
    (try (.execute maven
                   maven-project (into-list goals)
                   event-monitor transfer-listener nil
                   (file (:root project)))
         (finally (.stop maven)))))

(defn handle-dependencies [project]
  (execute project "process-resources"))

(comment
  (def project (read-project (file "/home/phil/src/corkscrew/sample/project.clj")))
  (handle-dependencies project)
  )
