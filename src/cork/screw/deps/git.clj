(ns cork.screw.deps.git
  (:use [clojure.contrib.shell-out]
        [cork.screw utils]))

(defmethod cork.screw.deps/fetch-dependency :git
  [[name version type url]]
  (let [filename (str cork.screw.deps/corkscrew-dir "/git/" name)]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists (java.io.File. filename))))
      (.mkdirs (java.io.File. filename))
      (with-sh-dir filename
                   (sh "git" "clone" url)
                   (sh "git" "checkout" version)
                   (cork.screw.deps/compile-checkout filename)))))
