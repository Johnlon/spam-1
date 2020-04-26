
T=$1
if [ "$T" == "" ]
then
    T=test
fi 

cd $(dirname $(readlink -f $T.v))
iverilog -Ttyp -Wall -g2012 -gspecify -grelative-include -o $T.vvp  $T.v 
if [ $? != 0 ] ; then
    echo ERROR exit code iverilog
    exit 1
fi
vvp -N -i $T.vvp
if [ $? != 0 ] ; then
    echo ERROR exit code vvp
    exit 1
fi

