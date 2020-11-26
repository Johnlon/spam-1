#!/bin/sh
cd $(readlink -f $(dirname $0))

java -jar ../build/libs/assembler.jar  "$@"
