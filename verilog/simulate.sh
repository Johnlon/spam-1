
TESTS=$1
if [ "$TESTS" == "" ]
then
    TESTS='test*.v'
fi 

for TEST in $TESTS 
do
    T=$(echo $TEST | sed -e 's/\..*//')

    echo ================ $TEST ===============
    root=/home/john/wslapps/iverilog/iverilog/
    iverilog=$root/driver/iverilog
    vvp=$root/vvp/vvp

    echo $iverilog

    cd $(dirname $(readlink -f $T.v))
    $iverilog -Ttyp -Wall -g2012 -gspecify -grelative-include -o $T.vvp  $T.v 
    if [ $? != 0 ] ; then
        echo ERROR exit code iverilog
        exit 1
    fi
    echo $vvp
    $vvp -N -i $T.vvp
    if [ $? != 0 ] ; then
        echo ERROR exit code vvp
        exit 1
    fi
done
