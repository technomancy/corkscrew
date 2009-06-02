#!/bin/bash

cd `dirname $0`

mvn process-resources assembly:assembly

# Installing jar file
mkdir -p $HOME/.corkscrew
rm $HOME/.corkscrew/corkscrew.jar
cp target/corkscrew*with-dependencies.jar $HOME/.corkscrew/corkscrew.jar
echo "Corkscrew compiled and installed in ~/.corkscrew."

# Installing shell script
if [ -d $HOME/bin ]; then
  cp bin/corkscrew "$HOME/bin"
  echo "The shell script is installed in $HOME/bin/corkscrew."
else
  echo "To use it, copy bin/corkscrew to your path."
fi