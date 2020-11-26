; Fib using stack for arg passing - but assumes exclusive use of MAR as stac pointer

ZERO: EQU 0
STACKTOP: EQU $ffff     ; location of top of stack
STACKPTR: EQU $feff     ; location of low byte of stack ptr addressa, top byte is a const, therefore 255 byte stack
STACKPTR_ADDR_HI: EQU <:STACKTOP   ;fixed

[:STACKPTR] = >:STACKTOP        ; ram location

start:      REGA=1
            REGB=1
            PCHITMP = >:ZERO

            ; push lop addr to stack
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            RAM=>:loop  ; put return address on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
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

            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            RAM = REGA    ; put ARG on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            PC = >:send_uart

ret1:       REGB=REGA+REGB _S
            PC = >:start _C

            ; push lop addr to stack
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            RAM = >:ret2  ; put return address on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            RAM = REGB    ; put ARG on stack
            MARLO=MARLO - 1
            [:STACKPTR] = MARLO

            PC      = >:send_uart

ret2:       PC      = >:loop

send_uart:  PC      = >:transmit
            PC      = >:send_uart    ;loop wait
transmit:
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            MARLO=MARLO + 1
            UART=RAM            ; read ARG from stack
            [:STACKPTR] = MARLO

            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            MARLO=MARLO + 1
            PC = RAM            ; read return address from stack
            [:STACKPTR] = MARLO

end:
END
