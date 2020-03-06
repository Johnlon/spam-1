
# simulate using typica timings
iverilog -Ttyp -Wall -g2012 -gspecify -o testSync.vvp  testSync.v && vvp -i testSync.vvp

