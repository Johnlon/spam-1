#!/bin/sh
cd $(readlink -f $(dirname $0))

java -classpath ../build/libs/assembler.jar  scc/SpamCC $1
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi
java -classpath ../build/libs/assembler.jar  asm/Assembler $1.asm
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

