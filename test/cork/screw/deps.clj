(ns cork.screw.deps
  (:use [cork.screw deps]
        [cork.screw utils]
        :reload-all)
  (:use [clojure.contrib test-is]))

(require 'cork.screw.deps.svn)
(require 'cork.screw.deps.http)

(def corkscrew-dir "/tmp/corkscrew/")
(def project-dir "/tmp/corkscrew-project")

(defn sample-project [& deps]
  {:name "my-sample"
   :version "1.0"
   :main 'nom.nom.nom
   :root project-dir
   :dependencies deps})

(defn cleanup-dirs [f]
  ;; (.mkdirs (java.io.File. corkscrew-dir))
  ;; (.mkdirs (java.io.File. project-dir))
  (f)
  ;; (delete-file-recursively corkscrew-dir)
  ;; (delete-file-recursively project-dir)
  )

(use-fixtures :each cleanup-dirs)

(deftest test-handle-svn-deps
  (let [project (sample-project
                 ["clojure" "r1343" :svn
                  "http://clojure.googlecode.com/svn/trunk"])]
    (handle-project-dependencies project)
    ;; TODO: ensure the files exist on disk
    ))

(deftest test-handle-http-deps
  (let [project (sample-project
                 ["rome" "1.0" :http
                  "https://rome.dev.java.net/dist/rome-1.0.jar"])]
    (handle-project-dependencies project)
    ;; TODO: ensure the files exist on disk
    ))