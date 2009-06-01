(ns cork.screw.test.maven
  (:use [cork.screw.deps.maven] :recload-all)
  (:use [clojure.contrib test-is]
        [clojure.xml]))

(deftest test-pom
  (is (= "<?xml version='1.0' encoding='UTF-8'?>
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd'>
<modelVersion>
4.0.0
</modelVersion>
<parent>
<artifactId>
clojure-pom
</artifactId>
<groupId>
org.clojure
</groupId>
<version>
1.0-SNAPSHOT
</version>
</parent>
<artifactId>
my-sample
</artifactId>
<name>
my-sample
</name>
<groupId>
my-sample
</groupId>
<version>
1.0
</version>
<dependencies>
<dependency>
<artifactId>
clojure
</artifactId>
<groupId>
org.clojure
</groupId>
<version>
1.0.0
</version>
</dependency>
<dependency>
<artifactId>
tagsoup
</artifactId>
<groupId>
tagsoup
</groupId>
<version>
1.2
</version>
</dependency>
</dependencies>
</project>
" (with-out-str
    (emit (pom-for {:name "my-sample"
                    :version "1.0"
                    :main 'my-sample
                    :dependencies [["clojure" "1.0.0" "org.clojure"]
                                   ["tagsoup" "1.2"]]}))))))