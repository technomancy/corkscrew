(ns cork.screw.deps.git
  (:use [clojure.contrib shell-out java-utils]
        [cork.screw utils]))

;; TODO: abstract things so code can be shared between VCSes
(defmethod cork.screw.deps/fetch-source-dependency :git
  [[name version type url]]
  (let [git-dir (str cork.screw.deps/corkscrew-dir "/git/")
        dir (str git-dir name)]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists (file dir))))
      (.mkdirs (file git-dir))
      (with-sh-dir git-dir
                   (sh "git" "clone" url)
                   (sh "git" "checkout" version)))
    dir))
