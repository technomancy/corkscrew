# Corkscrew

http://github.com/technomancy/corkscrew

Corkscrew is a build tool for Clojure. It's definitely very
proof-of-concept at this stage. You define a project.clj file in your
project root, and Corkscrew will provide you with tools to build and
manage your project.

## Project File

Here's a sample project file:

    {:name "my-sample"
     :version "1.0"
     :main 'my-sample
     :dependencies [["clojure" "r1343" :svn
                     "http://clojure.googlecode.com/svn/trunk"]
                    ["clojure-contrib" "r663" :svn
                      "http://clojure-contrib.googlecode.com/svn/trunk"]
                    ["rome" "1.0" :http
                      "http://rome.dev.java.net/dist/rome-1.0.jar"]
                    ["enlive" "95b2558943f50bb9962fe7d500ede353f1b578f0"
                     :git "git://github.com/cgrand/enlive.git"]
                    ["tagsoup" "1.2" :maven "http://mvnrepository.com"]
                    ["jdom" "1.1" :bundled "jars/jdom-1.1.jar"]]}

Dependencies can either be on Maven projects, jar files fetched over
HTTP, or checkouts of projects kept in Git or Subversion repositories.

TODO: Dependencies on Maven projects are not yet implemented.

TODO: Dependencies on projects that require AOT are not yet implemented.

## Usage

    $ corkscrew deps [project-dir]

Attempts to unpack each of the project's dependencies into the
target/dependency directory. If they aren't cached locally, this will
try to fetch them from the Internet.

    $ corkscrew repl [project-dir-or-jar]

Starts a REPL with the project's classpath and dependencies set up.

## Planned Features

    $ corkscrew build [project-dir] [output-jar]

Compiles your source code into the classes/ directory. Creates a jar
file containing bytecode. If your project is an application (has a
"main" function, not a library), then this jar will be executable.

    $ corkscrew build-standalone [project-dir] [output-jar]

Builds the project as a jar that contains all its dependencies,
including Clojure itself.

    $ corkscrew install [project-dir-or-jar]

Installs the project's jar into your local maven repository.

    $ corkscrew publish [project-dir-or-jar] [repository]

Uploads the jar and metadata to a remote repository.

## License

by Phil Hagelberg, (C) 2009

Licensed under the same terms as Clojure.
