(ns cork.screw)

(defn read-project
  "Given a filename for a project, returns a map of metadata for it."
  [filename]
  (let [file (java.io.File. filename)]
    (assoc (read-string (slurp (.getAbsolutePath file)))
      :root (.getParent file))))