(ns cork.screw)

(defn read-package
  "Given a filename for a package, returns a map of metadata for it."
  [filename]
  (let [file (java.io.File. filename)]
    (assoc (read-string (slurp (.getAbsolutePath file)))
      :root (.getParent file))))