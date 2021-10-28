#!/bin/sh
#cd $(readlink -f $(dirname $0))

#java -jar ../build/libs/assembler.jar  "$@"

java -classpath ../build/libs/jvmtools.jar  asm/Assembler "$@"
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi
