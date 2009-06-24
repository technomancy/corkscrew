(ns cork.screw.deps.maven
  (:import [org.apache.maven.cli MavenCli])
  (:use [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [with-preserving-file]]
        [cork.screw.pom :only [write-pom]]))

(defn handle-dependencies [project]
  (when-not (empty? (:dependencies project))
    (with-preserving-file (file (:root project) "pom.xml")
      (write-pom project)
      (MavenCli/main (into-array ["-e" "-f" (str (:root project) "/pom.xml") "process-resources"])
                     (org.codehaus.classworlds.ClassWorld. "plexus.core"
                                                           (.getContextClassLoader
                                                            (Thread/currentThread)))))))

;; mvn shell script launched with:
;; -Dclassworlds.conf=/usr/share/maven2/bin/m2.conf

