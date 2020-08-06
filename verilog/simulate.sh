
T=$1
if [ "$T" == "" ]
then
    T=test
fi 

T=$(echo $T | sed -e 's/\..*//')

root=/home/john/wslapps/iverilog/iverilog-with-pulldown-issue
root=/home/john/wslapps/iverilog/iverilog/
root=/home/john/wslapps/iverilog/jun2020/
iverilog=$root/driver/iverilog
vvp=$root/vvp/vvp

iverilog=iverilog
vvp=vvp

cd $(dirname $(readlink -f $T.v))
$iverilog -Ttyp -Wall -g2012 -gspecify -grelative-include -o $T.vvp  $T.v 
if [ $? != 0 ] ; then
    echo ERROR exit code iverilog
    exit 1
fi
$vvp -N -i $T.vvp
if [ $? != 0 ] ; then
    echo ERROR exit code vvp
    exit 1
fi

