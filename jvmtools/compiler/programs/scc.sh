#!/bin/sh
cd $(readlink -f $(dirname $0))
scc_file=$1
if [ -z $scc_file ]; then 
    echo "$0: missing arg 'scc_file'"
    exit 1
fi

HERE=$(readlink -f $(dirname $0))

pwd
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/

$JAVA_HOME/bin/java -classpath $HERE/../build/libs/compiler.jar  scc/SpamCC $scc_file
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

$JAVA_HOME/bin/java -classpath $HERE/../build/libs/compiler.jar  asm/Assembler $scc_file.asm
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

