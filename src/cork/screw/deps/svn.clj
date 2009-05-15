(ns cork.screw.deps.svn
  (:use [clojure.contrib.shell-out]
        [clojure.contrib.java-utils :only [file]]
        [cork.screw.utils :only [extract-jar]]))

(defmethod cork.screw.deps/fetch-dependency :svn
  [[name version type url]]
  (let [jar-file (file (str cork.screw.deps/corkscrew-dir
                            "svn/" name "/" version "/"))]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists jar-file)))
      (.mkdirs (.getParentFile jar-file))
      (with-sh-dir (.getParent jar-file)
                   (sh "svn" "checkout" "-r" version url ".")
                   (cork.screw.deps/compile-checkout (.getParent jar-file)
                                                     name)))))
