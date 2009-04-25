(ns cork.screw.deps.svn
  (:use [clojure.contrib.shell-out]
        [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [extract-jar]]))

(defn file-name-for [name version]
  (str cork.screw.deps/corkscrew-dir
       "svn/" name "/" version "/" name ".jar"))

(defmethod cork.screw.deps/fetch-dependency :svn
  [[name version type url]]
  (let [jar-file (file (file-name-for name version))]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists jar-file)))
      (.mkdirs (.getParentFile jar-file))
      (with-sh-dir (.getParent jar-file)
                   (sh "svn" "checkout" "-r" version url ".")
                   (cork.screw.deps/compile-checkout (.getParent jar-file))))))

(defmethod cork.screw.deps/unpack-dependency :svn
  [[name version type url] root]
  (extract-jar (file-name-for name url)
               (str root "/dependencies/")))
