
T=$1
if [ "$T" == "" ]
then
    T=test
fi 

cd $(dirname $(readlink -f $T.v))
iverilog -Ttyp -Wall -g2012 -gspecify -o $T.vvp  $T.v 
[ $? == 0 ] || (echo ERROR && exit 1)
vvp -i $T.vvp
[ $? == 0 ] || (echo ERROR && exit 1)

