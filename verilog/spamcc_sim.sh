#!/bin/bash

echo ARGS "$@"

TEST=$1
if [ "$TEST" == "" ]
then
    TEST='test*.v'
fi 
if [ "$TEST" == "" ]
then
    TEST='demo*.v'
fi 

shift

echo ================ $TEST ===============
root=/home/john/wslapps/iverilog/iverilog/
iverilog=$root/driver/iverilog
vvp=$root/vvp/vvp

#    echo $iverilog

cd $(dirname $(readlink -f $TEST))
filename=$(basename $TEST)
rootname=$(basename $filename .v)

$iverilog -Ttyp -Wall -g2012 -gspecify -grelative-include -o $rootname.vvp  $filename 
if [ $? != 0 ] ; then
    echo ERROR exit code iverilog
    exit 1
fi

$vvp -N -i $rootname.vvp "$@" &

# timeout to prenent orphanned processes when Java test harness goes away

timeout=20
while [ -d /proc/$! -a $timeout > 0 ]; do
    sleep 1
    timeout=$((timeout-1))
done
if [ -d /proc/$! ]; then
    kill -9 $!
fi 

exit 0