
TESTS=$1
if [ "$TESTS" == "" ]
then
    TESTS='test*.v'
fi 
if [ "$TESTS" == "" ]
then
    TESTS='demo*.v'
fi 


for TEST in $TESTS 
do

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
    $vvp -N -i $rootname.vvp
    if [ $? != 0 ] ; then
        echo ERROR exit code vvp
        exit 1
    fi
done
