#!/bin/sh
#cd $(readlink -f $(dirname $0))

#java -jar ../build/libs/assembler.jar  "$@"
HERE=$(readlink -f $(dirname $0))

pwd
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/


ls -l $HERE/../build/libs/compiler.jar 

$JAVA_HOME/bin/java -classpath $HERE/../build/libs/compiler.jar  asm/Assembler "$@"
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi
