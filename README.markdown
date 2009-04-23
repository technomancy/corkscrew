* Corkscrew

Corkscrew is a build tool for Clojure. It's definitely very
proof-of-concept at this stage.

Shell script usage:

  $ corkscrew build [project-dir] [output-jar]
  $ corkscrew deps [project-dir]
  $ corkscrew install [project-dir-or-jar]
  $ corkscrew repl [project-dir-or-jar]

Nice to have:

  $ corkscrew publish [project-dir-or-jar] [repository]
  $ corkscrew build-standalone [project-dir] [output-jar]
  $ corkscrew swank [project-dir] [output-jar]

by Phil Hagelberg
Licensed under the same terms as Clojure
