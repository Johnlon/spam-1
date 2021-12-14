#!/bin/sh
cd $(readlink -f $(dirname $0))
scc_file=$1
if [ -z $scc_file ]; then 
    echo "$0: missing arg 'scc_file'"
    exit 1
fi

java -classpath ../build/libs/jvmtools.jar  scc/SpamCC $scc_file
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

java -classpath ../build/libs/jvmtools.jar  asm/Assembler $scc_file.asm
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

