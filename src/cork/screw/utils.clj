(ns cork.screw.utils
  (:use [clojure.contrib.java-utils :only [file]]
        [clojure.contrib.seq-utils :only [flatten]])
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

(defn copy-between-streams
  "Copy all data between in and out."
  [in out]
  (try
   (let [bytes (make-array Byte/TYPE 1000)]
     (loop [byte-count (.read in bytes)]
       (when (not= -1 byte-count)
         (.write out bytes 0 byte-count)
         (recur (.read in bytes)))))
   (finally
    (.close in)
    (.close out))))

(defn extract-jar
  "Unpacks jar-file into target-dir. jar-file can be a JarFile
  instance or a path to a jar file on disk."
  [jar-file target-dir]
  (println jar-file)
  (let [jar (if (isa? jar-file JarFile)
              jar-file
              (JarFile. jar-file true))
        entries (enumeration-seq (.entries jar))
        target-file #(str target-dir "/" (.getName %))]
    ;; First make all the directories
    (doseq [entry entries :when (.isDirectory entry)]
      (println "Making dir: " (target-file entry))
      (.mkdirs (java.io.File. (target-file entry))))
    ;; Then write the files
    (doseq [entry entries :when (not (.isDirectory entry))]
      (println "Writing: " (target-file entry))
      (copy-between-streams (.getInputStream jar entry)
                            (java.io.FileOutputStream. (target-file entry))))))

(defn read-project
  "Given a filename for a project, returns a map of metadata for it."
  [filename]
  (let [file (java.io.File. filename)]
    (assoc (read-string (slurp (.getAbsolutePath file)))
      :root (.getParent file))))