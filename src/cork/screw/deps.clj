(ns cork.screw.deps
  (:use [cork.screw])
  (:use [clojure.contrib.shell-out])
  (:gen-class))

(def *force-fetch* false)
(def corkscrew-dir (str (System/getProperty "user.home") "/.corkscrew/"))

(defmulti fetch-dependency #(% 2))
(defmulti unpack-dependency (fn [dep root] (dep 2)))

(defn unpack-jar [location root]
  ;; TODO: use built-in JarFile class, but it seems to suck. =\
  (with-sh-dir root (sh "unzip" "-u" location "-d" "classes")))

(defn handle-package-dependencies [package]
  (try
   (doseq [dependency (:dependencies package)]
     (fetch-dependency dependency)
     (unpack-dependency dependency (:root package)))
   ;; (catch Exception e
   ;;   ;; TODO: be more specific about failure.
   ;;   (println "Could not handle dependency: "
   ;;            (:name package) " " (:version package)))
   ))

(defn -main
  "Unpack all the "
  ([] (-main "."))
  ([target-dir]
     (handle-package-dependencies
      (read-package (str target-dir "/package.clj")))))

(require 'cork.screw.deps.http)
(require 'cork.screw.deps.git)
;; (require 'cork.screw.deps.svn)
;; (require 'cork.screw.deps.maven)
;; (require 'cork.screw.deps.bundled)