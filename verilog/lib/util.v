
`ifndef UTIL_V
`define UTIL_V

`timescale 1ms/1ms

package util;

    task sleep(integer ms);
        time entry;
    begin
        $sleep(ms);
    end
    endtask

endpackage: util

`endif 
