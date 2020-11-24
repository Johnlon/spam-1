

; Fib using stack for arg passing - but assumes exclusive use of MAR as stac pointer
STACKTOP: EQU $ffff     ; location of top of stack
STACKPTR: EQU $feff     ; location of low byte of stack ptr addressa, top byte is a const, therefore 255 byte stack
STACKPTR_ADDR_HI: EQU <:STACKTOP   ;fixed

[:STACKPTR] = >:STACKTOP        ; ram location

start:      REGA=1
            REGB=1
            PCHITMP = 0

            ; push lop addr to stack
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]

            RAM=>:loop  ; put return address on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            RAM=REGA    ; put ARG on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            PC      = >:send_uart

loop:       REGA=REGA+REGB  _S
            PC = >:start _C

            ; push lop addr to stack
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]

            RAM = >:ret1  ; put return address on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            RAM = REGA    ; put ARG on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            PC = >:send_uart

ret1:       REGB=REGA+REGB _S
            PC = >:start _C

            ; start pushing
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            ; push retun address
            RAM = >:ret2  ; put return address on stack
            MARLO=MARLO - 1
            ; push arg
            RAM = REGB    ; put ARG on stack
            MARLO=MARLO - 1
            ; end pushing
            [:STACKPTR] = MARLO

            PC      = >:send_uart

ret2:       PC      = >:loop

send_uart:  PC      = >:transmit _DO
            PC      = >:send_uart    ;loop wait
transmit:
            ; start popping
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]

            ; pop arg
            MARLO=MARLO + 1
            UART=RAM            ; read ARG from stack

            ; pop return address
            MARLO=MARLO + 1
            PC = RAM            ; read return address from stack

            ; end popping
            [:STACKPTR] = MARLO

end:
END
