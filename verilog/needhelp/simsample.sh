
# simulate using typica timings
iverilog -Ttyp -Wall -g2012 -gspecify -o sample.vvp  sample.v && vvp -i sample.vvp
