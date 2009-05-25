(ns cork.screw.deps.git
  (:use [clojure.contrib.shell-out]
        [cork.screw utils]))

(defmethod cork.screw.deps/fetch-dependency :git
  [[name version type url]]
  (let [dir (str cork.screw.deps/corkscrew-dir "/git/" name)]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists (java.io.File. dir))))
      (.mkdirs (java.io.File. dir))
      (with-sh-dir dir
                   (sh "git" "clone" url)
                   (sh "git" "checkout" version)))
    dir))
