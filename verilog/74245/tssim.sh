
# simulate using typica timings
iverilog -Ttyp -Wall -g2012 -gspecify -o tstest.vvp  tstest.v && vvp -i tstest.vvp
