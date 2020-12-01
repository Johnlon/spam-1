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

parentProcess=$PPID
{
    myParentProcess=$PPID
    while [ 1 ]; do
        if [ ! -d /proc/$parentProcess ]; then
            echo PARENT DIED SO EXITING
            kill -TERM $myParentProcess
            kill -INT $myParentProcess
            kill -9 $myParentProcess
            exit 1
        fi
        sleep 1
    done
} &

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
#    echo $vvp
$vvp -N -i $rootname.vvp "$@"
if [ $? != 0 ] ; then
    echo ERROR exit code vvp
    exit 1
fi
