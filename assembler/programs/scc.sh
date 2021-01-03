#!/bin/sh
cd $(readlink -f $(dirname $0))

java -classpath ../build/libs/assembler.jar  scc/SpamCC $1
java -classpath ../build/libs/assembler.jar  asm/Assembler $1.asm

