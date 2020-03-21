
T=$1
[ -n $T ] || T=test

cd $(dirname $(readlink -f $T.v))
iverilog -Ttyp -Wall -g2012 -gspecify -o $T.vvp  $T.v 
[ $? == 0 ] || (echo ERROR && exit 1)
vvp -i $T.vvp
[ $? == 0 ] || (echo ERROR && exit 1)

