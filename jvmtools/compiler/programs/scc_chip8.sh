#!/bin/sh
set -x
cd $(readlink -f $(dirname $0))
scc_file=$1
if [ -z $scc_file ]; then 
    echo "$0: missing arg 'scc_file'"
    exit 1
fi
chip8_prog=$2
if [ -z $chip8_prog ]; then 
    echo "$0: missing arg 'chip8_prog'"
    exit 1
fi

HERE=$(readlink -f $(dirname $0))

set -x
java -DCHIP8_FILENAME=$chip8_prog -classpath $HERE/../build/libs/jvmtools.jar  scc/SpamCC $scc_file
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

java -classpath $HERE/../build/libs/jvmtools.jar  asm/Assembler $scc_file.asm
if [ $? -ne 0 ]; then
    echo error
    exit 1
fi

