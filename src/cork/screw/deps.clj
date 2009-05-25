(ns cork.screw.deps
  (:use [cork.screw.utils])
  (:use [clojure.contrib.shell-out :only [sh]])
  (:gen-class))

;; TODO: use a logger instead of println

(def *force-fetch* false)
(def corkscrew-dir (str (System/getProperty "user.home") "/.corkscrew/"))

(defmulti fetch-dependency
  "Takes a list of name, version, type, and url of a dependency. Downloads
 it if necessary and returns a string of where it exists on disk. The string
could refer to a jar file or a directory of the unpacked project."
  #(% 2))

(defn unpack-dependency [dep-file root]
  (println "Unpacking: " dep-file)
  (if (.isDirectory dep-file)
    (sh "cp" "-r" (str dep-file "/src/") root) ;; TODO: write cp -r in Clojure
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
(require 'cork.screw.deps.git)
;; (require 'cork.screw.deps.maven)

