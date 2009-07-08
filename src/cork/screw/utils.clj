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

(defn copy-file
  "Recursively copy file or directory source to target. Both arguments
  may be java.io.Files or strings."
  [source target]
  ;; TODO: preserve permissions?
  (let [source (file source)
        target (file target)]
    (if (.isDirectory source)
      (do (.mkdirs target)
          (doseq [child (.listFiles source)]
            (copy-file child (file target (.getName child)))))
      (copy source target))))

(defn read-project
  "Given a filename for a project, returns a map of metadata for it."
  [project-file]
  (assoc (read-string (slurp (.getAbsolutePath project-file)))
    :root (.getAbsolutePath (.getParentFile project-file))))

(defmacro with-preserving-file
  "Execute body and preserve the contents of file."
  [file-name & body]
  `(let [temp-file# (java.io.File/createTempFile "pom-" ".xml")]
     (try
      (when (.exists ~file-name)
        (copy-file ~file-name temp-file#))
      ~@body
      (finally
       (if (.exists temp-file#)
         (copy-file temp-file# ~file-name)
         (.delete (file temp-file#)))))))
