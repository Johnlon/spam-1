
TESTS=$1
if [ "$TESTS" == "" ]
then
    TESTS='test*.v'
fi 

for TEST in $TESTS 
do

    echo ================ $TEST 
    root=/home/john/wslapps/iverilog/iverilog/
    iverilog=$root/driver/iverilog
    vvp=$root/vvp/vvp

#    echo $iverilog

    cd $(dirname $(readlink -f $TEST))
    filename=$(basename $TEST)
    rootname=$(basename $filename .v)

    /home/john/wslapps/verilator/verilator/bin/verilator --sv --lint-only --language 1800-2012  +systemverilogext+v --relative-includes --Wno-MULTITOP --Wno-STMTDLY +1800-2012ext+v $filename
    if [ $? != 0 ] ; then
        echo ERROR 
        exit 1
    fi
    echo $filename ok
done
