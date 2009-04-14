(ns cork.screw.deps.http
  (:use [clojure.contrib.duck-streams]))

(defn sha1-hex
  "Returns the hex representation of a SHA1 hash of a string."
  [s]
  (.toString 
   (BigInteger. 1
	(.digest (java.security.MessageDigest/getInstance "SHA1")
		 (.getBytes s))) 16))

(defn file-name-for [name url]
  (str cork.screw.deps/corkscrew-dir
       "/http/" name "/" (sha1-hex url)))

(defmethod cork.screw.deps/fetch-dependency :http
  [[name version type url]]
  (let [filename (file-name-for name url)]
    (when (or cork.screw.deps/*force-fetch*
              (not (.exists (java.io.File. filename))))
      (with-open [in-stream (.openStream (java.net.URL. url))
                  out-stream (java.io.FileOutputStream. filename)]
        (loop [input (.read in-stream)]
          (when (not (= input -1))
            (.write out-stream input)
            (recur (.read in-stream))))))))

(defmethod cork.screw.deps/unpack-dependency :http
  [[name version type url] root]
  (cork.screw.deps/unpack-jar (file-name-for name url) root))
