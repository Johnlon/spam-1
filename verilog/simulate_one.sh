
#!/bin/bash

echo ARGS "$@"

TEST=$1
if [ "$TEST" == "" ]
then
    #TEST='test*.v'
    TEST='test.v'
fi 
if [ "$TEST" == "" ]
then
    #TEST='demo*.v'
    TEST='demo.v'
fi 

shift

#parentProcess=$PPID
#{
#    myParentProcess=$PPID
#    while [ 1 ]; do
#        if [ ! -d /proc/$parentProcess ]; then
#            echo PARENT DIED SO EXITING
#            kill -TERM $myParentProcess
#            kill -INT $myParentProcess
#            kill -9 $myParentProcess
#            exit 1
#        fi
#        sleep 1
#    done
#} &

echo ================ $TEST ===============
root=/home/john/wslapps/iverilog/iverilog/
iverilog=$root/driver/iverilog
vvp=$root/vvp/vvp

#    echo $iverilog

cd $(dirname $(readlink -f $TEST))
filename=$(basename $TEST)
rootname=$(basename $filename .v)

FLAGS="$VVPEXTRA  -Ttyp -Wall -g2012 -gspecify -grelative-include "

# just run preprocessor and leave on disk for inspection if theres an error in the following full blown compile step
$iverilog $FLAGS -E -o icarus.out  $filename 
if [ $? != 0 ] ; then
    echo ERROR exit code iverilog macro processing
    exit 1
fi

# full blown compile
$iverilog $FLAGS -o $rootname.vvp  $filename 
if [ $? != 0 ] ; then
    echo ERROR exit code iverilog
    exit 1
fi

#
#    echo $vvp
$vvp -M/home/john/OneDrive/simplecpu/verilog/cpu/vpi -msleep  -N -i $rootname.vvp "$@"
if [ $? != 0 ] ; then
    echo ERROR exit code vvp
    exit 1
fi
