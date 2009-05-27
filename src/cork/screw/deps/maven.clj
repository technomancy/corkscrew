(ns cork.screw.deps.maven
  (:import [org.apache.maven.project MavenProject]
           [org.apache.maven.model Build Dependency]
           [org.apache.maven.artifact Artifact]
           [org.apache.maven.plugin.dependency UnpackDependenciesMojo]))

(def clojure-pom (doto (MavenProject.)
                   (.setArtifactId "clojure-pom")
                   (.setGroupId "org.clojure")
                   (.setVersion "1.0-SNAPSHOT")))

(defn dependency-list [dependencies]
  (let [deps (java.util.LinkedList.)]
    (doseq [dependency dependencies]
      (.add deps (doto (Dependency.)
                 (.setVersion (:version dependency))
                 (.setGroupId (:group dependency))
                 (.setArtifactId (:name dependency)))))
    deps))

(defn create-project-model [project]
  (let [project-model (doto (MavenProject.)
                        (.setName (:name project))
                        (.setArtifactId (:name project))
                        (.setGroupId (or (:group project) (:name project)))
                        (.setVersion (:version project))
                        (.setModelVersion "4.0.0")
                        (.setParent clojure-pom))]

    (.setBuild project-model (doto (Build.)
                               (.setOutputDirectory (str (:root project)
                                                         "/target"))
                               (.setSourceDirectory (str (:root project)
                                                         "/src"))))
    (.setDependencies project-model (dependency-list (:depdedencies project)))))

(defn handle-dependencies [project]
  (let [unpacker (UnpackDependenciesMojo.)]
    ;; TODO: no method to set project. bugger!
    (.setProject unpacker (create-project-model project))
    (.execute unpacker)))

;; (handle-dependencies {:name "sample" :version "1.0.0" :root "/tmp/sample"
;;                       :dependencies [{:name "clojure-contrib" :group "org.clojure"
;;                                       :version "1.0-SNAPSHOT"}
;;                                      {:name "rome" :group "rome" :version "1.0"}
;;                                      {:name "tagsoup" :group "tagsoup" :version "1.2"}]})