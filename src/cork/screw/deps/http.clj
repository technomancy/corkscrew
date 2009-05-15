(ns cork.screw.deps.http
  (:use [clojure.contrib duck-streams shell-out]
        [cork.screw utils]))

(defn sha1-hex
  "Returns the hex representation of a SHA1 hash of a string."
  [s]
  (.toString
   (BigInteger.
    1 (.digest (java.security.MessageDigest/getInstance "SHA1")
               (.getBytes s))) 16))

(defmethod cork.screw.deps/fetch-dependency :http
  [[name version type url]]
  (let [filename (str cork.screw.deps/corkscrew-dir
                      "/http/" name "/" (sha1-hex url))
        bytes (make-array Byte/TYPE 1000)]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists (java.io.File. filename))))
      (.mkdirs (.getParentFile (java.io.File. filename)))
      (with-open [in-stream (.openStream (java.net.URL. url))
                  out-stream (java.io.FileOutputStream. filename)]
        (loop [input (.read in-stream)]
          (when (not (= input -1))
            (.write out-stream input)
            (recur (.read in-stream))))))
    (java.io.File. filename)))
