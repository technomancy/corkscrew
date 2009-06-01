(ns cork.screw.test.deps
  (:use [cork.screw deps]
        [cork.screw utils] :reload-all)
  (:use [clojure.contrib test-is with-ns java-utils]))

(with-ns 'cork.screw.deps
  (def corkscrew-dir "/tmp/corkscrew/"))

(def project-dir "/tmp/corkscrew-project")

(defn cleanup-dirs [f]
  (.mkdirs (java.io.File. corkscrew-dir))
  (.mkdirs (java.io.File. project-dir))
  (f)
  (try
   (delete-file-recursively corkscrew-dir)
   (delete-file-recursively project-dir)
   (catch Exception e)))

(use-fixtures :each cleanup-dirs)

(deftest test-handle-svn-deps
  (let [project {:name "my-sample"
                 :version "1.0"
                 :main 'nom.nom.nom
                 :root project-dir
                 :source-dependencies [["clojure-contrib" "r150" :svn
                                        "http://clojure-contrib.googlecode.com/svn/trunk"]]}]
    (.mkdirs (java.io.File. (:root project)))
    (handle-project-dependencies project)
    (is (= '("def.clj" "duck_streams.clj" "enum.clj" "except.clj" "fcase.clj" "gen_interface.clj"
             "import_static.clj" "javalog.clj" "lazy_seqs.clj" "lib.clj" "memoize.clj" "mmap.clj"
             "ns_utils.clj" "pred.clj" "seq_utils.clj" "sql.clj" "str_utils.clj" "string.clj"
             "test_is.clj" "trace.clj" "xml.clj" "zip_filter.clj")
           (map #(.getName %) (sort (filter #(re-find #"\.clj$" (.getName %))
                                            (file-seq (file project-dir)))))))))

(deftest test-handle-git-deps
  (let [project {:name "my-sample"
                 :version "1.0"
                 :main 'nom.nom.nom
                 :root project-dir
                 :source-dependencies [["enlive" "95b2558943f50bb9962fe7d500ede353f1b578f0"
                                        :git "git://github.com/cgrand/enlive.git"]]}]
    (.mkdirs (java.io.File. (:root project)))
    (handle-project-dependencies project)
    (is (= '("enlive_html.clj" "examples-with-ring.clj" "examples.clj" "state_machine.clj"
             "insertion_point.clj" "xml.clj")
           (map #(.getName %) (sort (filter #(re-find #"\.clj$" (.getName %))
                                            (file-seq (file project-dir)))))))))

