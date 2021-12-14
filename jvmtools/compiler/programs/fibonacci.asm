; Fib using registers for arg passing

ZERO: EQU 0

start:      REGA    = 1
            REGB    = 1
            PCHITMP = >:ZERO
            REGC    = REGA

            ; set PC to return address and then call the send_uart loop
            REGD    = >:loop
            PC      = >:send_uart

loop:       REGA    = REGA+REGB  _S
            PC      = >:start _C
            REGC    = REGA

            ; set PC to return address and then call the send_uart loop
            REGD    = >:ret1
            PC      = >:send_uart

ret1:       REGB    = REGA+REGB _S
            PC      = >:start _C
            REGC    = REGB

            ; set PC to return address and then call the send_uart loop
            REGD    = >:loop
            PC      = >:send_uart

send_uart:  PC      = >:transmit _DO
            PC      = >:send_uart    ;loop wait
            
transmit:   UART    = REGC   ; write whatever is in REGC to the UART
            PC      = REGD   ; jump to the location in REGD
end:

END
