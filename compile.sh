#!/bin/bash

cd `dirname $0`

# Replace ~/src/clojure(-contrib)? with your clojure install location

rm -rf classes/*
unzip -u ../clojure/clojure.jar -d dependencies/
unzip -u ../clojure-contrib/clojure-contrib.jar -d dependencies/

java -cp src/:classes/:dependencies clojure.main -e \
    "(compile 'cork.screw.build) (compile 'cork.screw.deps) (compile 'cork.screw.run)"