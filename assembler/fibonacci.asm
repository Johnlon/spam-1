; Fib using registers for arg passing

ZERO: EQU 0

; LOAD TO RAM AT THE CURRENT MEMORY[0] ONWARDSa AND SET THE LABEL TO THE FIRST BYTE
STRING:     STR     "ABC\n\0\u0000"
BYTE_ARR:   BYTES   [170 $aa %10101010] // parse as hex bytes and then treat as per STR
STRING_L:   EQU     len(:STRING)
BYTE_ARR_L: EQU     len(:BYTE_ARR)

start:      REGA=1
            REGB=1
            PCHITMP = >:ZERO
            REGC=REGA
            REGD=>:loop
            PC      = >:send_uart

loop:       REGA=REGA+REGB  _S
            PC = >:start _C
            REGC=REGA
            REGD=>:ret1
            PC      = >:send_uart

ret1:       REGB=REGA+REGB _S
            PC      = >:start _C
            REGC=REGB
            REGD=>:ret2
            PC      = >:send_uart

ret2:       PC      = >:loop

send_uart:  PC      = >:transmit _DO
            PC      = >:send_uart    ;loop wait
transmit:   UART=REGC
            PC = REGD
end:

END
