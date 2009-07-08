(ns cork.screw.deps.maven
  ;; (:import [org.apache.maven.cli MavenCli])
  (:use [clojure.contrib.shell-out :only [sh]]
        [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [with-preserving-file]]
        [cork.screw.pom :only [write-pom]]))

;; TODO: Shelling out to mvn is laaame. We should eventually be able
;; to call the Maven API from Java, possibly by constructing a
;; MavenCli if all else fails.
(defn handle-dependencies [project]
  (when-not (empty? (:dependencies project))
    (with-preserving-file (file (:root project) "pom.xml")
      (write-pom project)
      ;; (MavenCli/main (make-array String "-f" (str (:root project) "/pom.xml")
      ;;                            "process-resources"))
      (let [result (sh "mvn" "-f" (str (:root project) "/pom.xml")
                       "process-resources" :return-map true)]
        (when (pos? (:exit result))
          (throw (Exception. (:out result))))))))
