
cd $(dirname $(readlink -f $0))
# simulate using typica timings
iverilog -Ttyp -Wall -g2012 -gspecify -o testSync.vvp  testSync.v && vvp -i testSync.vvp

