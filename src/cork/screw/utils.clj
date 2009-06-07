(ns cork.screw.utils
  (:use [clojure.contrib.java-utils :only [file]]
        [clojure.contrib.seq-utils :only [flatten]]
        [clojure.contrib.duck-streams :only [copy]])
  (:import (java.util.jar JarFile)))

(defn delete-file
  "Delete file f. Raise an exception if it fails."
  [f]
  (or (.delete (file f))
      (throw (java.io.IOException. (str "Couldn't delete " f)))))

(defn delete-file-recursively
  "Delete file f. If it's a directory, recursively delete all its
  contents. Raise an exception if any deletion fails."
  [f]
  (let [f (file f)]
    (when (.isDirectory f)
      (doseq [child (.listFiles f)]
          (delete-file-recursively child)))
    (delete-file f)))

(defn read-project
  "Given a filename for a project, returns a map of metadata for it."
  [project-file]
  (assoc (read-string (slurp (.getAbsolutePath project-file)))
    :root (.getAbsolutePath (.getParentFile project-file))))