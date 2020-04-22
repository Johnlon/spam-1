
T=$1
if [ "$T" == "" ]
then
    T=test
fi 

cd $(dirname $(readlink -f $T.v))
iverilog -Ttyp -Wall -g2012 -gspecify -o $T.vvp  $T.v 
if [ $? != 0 ] ; then
    echo ERROR 
    exit 1
fi
vvp -N -i $T.vvp
if [ $? != 0 ] ; then
    echo ERROR 
    exit 1
fi

