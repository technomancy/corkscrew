(ns cork.screw.test.deps
  (:use [cork.screw deps]
        [cork.screw utils] :reload-all)
  (:use [clojure.contrib test-is with-ns java-utils]))

(require 'cork.screw.deps.svn)
(require 'cork.screw.deps.http)

(with-ns 'cork.screw.deps
         (def corkscrew-dir "/tmp/corkscrew/"))

(def project-dir "/tmp/corkscrew-project")

(defn sample-project [& deps]
  {:name "my-sample"
   :version "1.0"
   :main 'nom.nom.nom
   :root project-dir
   :dependencies deps})

(defn cleanup-dirs [f]
  (.mkdirs (java.io.File. corkscrew-dir))
  (.mkdirs (java.io.File. project-dir))
  (f)
  (try
   (delete-file-recursively corkscrew-dir)
   (delete-file-recursively project-dir)
   (catch Exception e)))

(use-fixtures :each cleanup-dirs)

(deftest test-handle-http-deps
  (let [project (sample-project
                 ["clojure" "1.1-SNAPSHOT" :http
                  "http://p.hagelb.org/clojure.jar"])]
    (handle-project-dependencies project)
    (let [files (file-seq (file (:root project) "target" "dependency"))]
      (doseq [clj-file ["META_INF" "core.clj" "main.clj" "core_print.clj"
                        "my_core.clj" "xml.clj" "zip.clj" "inspector.clj"
                        "set.clj" "parallel.clj" "core_proxy.clj" "genclass.clj"]]
        (is (some #(.contains % clj-file) files))))))

;; (deftest test-handle-svn-deps
;;   (let [project (sample-project
;;                  ["clojure" "r1343" :svn
;;                   "http://clojure.googlecode.com/svn/trunk"])]
;;     (handle-project-dependencies project)
;;     (is (= () (file-seq (file project-dir "target" "dependency"))))))
