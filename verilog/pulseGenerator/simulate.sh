
cd $(dirname $(readlink -f $0))
# simulate using typica timings
#iverilog -Ttyp -Wall -g2012 -gspecify test.v && ./a.out
iverilog -Ttyp -Wall -g2012 -gspecify -o test.vvp  test.v && vvp -i test.vvp
