(ns cork.screw.deps.git
  (:use [clojure.contrib.shell-out]
        [cork.screw utils]))

(defn file-name-for [name version]
  (str cork.screw.deps/corkscrew-dir "/git/" name))

(defmethod cork.screw.deps/fetch-dependency :git
  [[name version type url]]
  (let [filename (file-name-for name url)]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists (java.io.File. filename))))
      (.mkdirs (java.io.File. filename))
      (with-sh-dir filename
                   (sh "git" "clone" url)
                   (sh "git" "checkout" version)
                   (cork.screw.deps/compile-checkout filename)))))

(defmethod cork.screw.deps/unpack-dependency :git
  [[name version type url] root]
  (extract-jar (file-name-for name url) root))
