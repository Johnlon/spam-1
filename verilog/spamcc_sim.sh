#!/bin/bash
echo running $0

set -x

echo ARGS "$@"

timeout=$1
shift

VERILOG=$1
shift

ROM=$1
shift

if [ "$timeout" == "" ]; then
    echo "Missing timeout"
    echo "$0 timeout verilog rom"
    exit 1
fi

if [ "$VERILOG" == "" ]; then
    echo "Missing verilog file"
    echo "$0 timeout verilog rom"
    exit 1
fi

if [ ! -f $ROM ]; then
    echo "missing ROM file"
    echo "$0 timeout verilog rom"
    exit 1
fi

if [ ! -f "$VERILOG" ]; then
    echo "verilog not found : "  $VERILOG
    echo "$0 timeout verilog rom"
    exit 1
fi

if [ ! -f "$ROM" ]; then
    echo "rom not found : "  $ROM
    echo "$0 timeout verilog rom"
    exit 1
fi

VERILOG=$(readlink -f $VERILOG)
ROM=$(readlink -f $ROM)

echo ================ VERILOG : $VERILOG 
echo ================ ROM     : $ROM 

root=/home/john/wslapps/iverilog/iverilog/
iverilog=$root/driver/iverilog
vvp=$root/vvp/vvp

#    echo $iverilog

cd $(dirname $(readlink -f $VERILOG))
pwd

filename=$(basename $VERILOG)
rootname=$(basename $filename .v)

$iverilog -Ttyp -Wall -g2012 -gspecify -grelative-include -o $rootname.vvp $filename 
if [ $? != 0 ] ; then
    echo ERROR exit code iverilog
    exit 1
fi


if [ -z $UART_MODE ] ; then
UART_MODE=2
fi

#vvp -N -i $rootname.vvp "$@" &

$vvp -N -i $rootname.vvp +rom=$ROM +uart_out_mode=$UART_MODE &

# timeout to prenent orphanned processes when Java test harness goes away

while [ -d /proc/$! -a $timeout -gt 0 ]; do
    sleep 1
    timeout=$((timeout-1))
    echo sim has $timeout seconds remaining
done

if [ -d /proc/$! ]; then
    kill -9 $!
fi 

echo EXIT SIM
exit 0
