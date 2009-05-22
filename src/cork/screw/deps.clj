(ns cork.screw.deps
  (:use [cork.screw.utils])
  (:use [clojure.contrib.shell-out :only [with-sh-dir sh]]
        [clojure.contrib.java-utils :only [file]])
  (:gen-class))

(def *force-fetch* false)
(def corkscrew-dir (str (System/getProperty "user.home") "/.corkscrew/"))

(defmulti fetch-dependency #(% 2))

(defn compile-checkout
  "Compiles the dependency checkout if necessary and returns a file
pointing to either the jar or the source root."
  [root name]
  (println "Compiling: " root)
  (with-sh-dir root
               (cond
                 (.exists (file root "build.xml"))
                 (do (sh "ant")
                     (file root (str name ".jar")))

                 (.exists (file root "pom.xml"))
                 (do (sh "mvn compile")
                     (file root (str "target/" name ".jar")))

                 true
                 (file root "src/"))))

(defn unpack-dependency [dep-file root]
  (println "Unpacking: " dep-file)
  (if (.isDirectory dep-file)
    (sh "cp" "-r" dep-file root) ;; TODO: write this in Clojure
    (extract-jar dep-file root)))

(defn handle-project-dependencies [project]
  (doseq [dependency (:dependencies project)]
    (println "Handling: " dependency)
    (unpack-dependency (fetch-dependency dependency)
                       (str (:root project) "/target/dependency/"))))

(defn -main
  "Fetch and unpack all the dependencies."
  ([] (-main "."))
  ([target-dir]
     (handle-project-dependencies
      (read-project (str target-dir "/project.clj")))))

(require 'cork.screw.deps.http)
(require 'cork.screw.deps.svn)
;; (require 'cork.screw.deps.git)
;; (require 'cork.screw.deps.maven)

