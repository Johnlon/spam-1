
set -e
cd $(dirname $(readlink -f $0))
# simulate using typica timings
iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp  testChained.v && vvp -i test.vvp
echo $?
