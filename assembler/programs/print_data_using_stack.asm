; run and grep "TRANS" to see uart transmit message

STACKTOP: EQU $ffff                 ; location of top of stack
STACKPTR_ADDR_HI: EQU <:STACKTOP    ;const value pointing at pag stack is on
STACKPTR_ADDR_LO: EQU $feff         ;location of low byte of stack ptr addressa, top byte is a const, therefore 255 byte stack

; LOAD TO RAM AT THE CURRENT MEMORY[0] ONWARDSa AND SET THE LABEL TO THE FIRST BYTE
STRING_DATA:     STR     "Hello World\0"

CURCHAR_LO:     BYTES [ > :STRING_DATA ]   ; where to store a pointer into the string initialise to the start of the string
CURCHAR_HI:     BYTES [ < :STRING_DATA ]

            [:STACKPTR_ADDR_LO] = >:STACKTOP        ; ram location

            PCHITMP = 0

loop:
            ; read next char into REGA
            MARHI=[:CURCHAR_HI]
            MARLO=[:CURCHAR_LO]  ; 17
            REGA=RAM _S

            ; goto end if just read a 0 ie last char
            PC = >:end _Z

            ; ===================================BEGIN CALL
            ; ===================================SETUP ARGS
            ; recover stack pointer into MAR
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR_ADDR_LO]

            RAM=>:next_char  ; put return address on stack

            ; move stack ptr
            MARLO=MARLO - 1

            ; put ARG on stack
            RAM=REGA

            ; move and save stack ptr
            [:STACKPTR_ADDR_LO] = MARLO-1
            ; end jump

            ; ===================================DO JUMP
            ; perform jump
            PC      = >:send_uart
            ; ===================================END CALL

next_char:
            REGA=NU B_PLUS_1 [:CURCHAR_LO] _S
            [:CURCHAR_LO]=REGA

            REGA=[:CURCHAR_HI]
            REGA=REGA+1 _C

            [:CURCHAR_HI]=REGA

            PC      = >:loop

send_uart:
            ; ==================================RECOVER ARGS
            ; recover stack ptr and inc
            MARHI=:STACKPTR_ADDR_HI
            MARLO=NU B_PLUS_1 [:STACKPTR_ADDR_LO]

            ; recover 1st arg
            REGA=RAM

            MARLO=MARLO+1

            REGB=RAM

            [:STACKPTR_ADDR_LO]=MARLO
            ; ===================================END RECOVER ARGS

wait_for_tx_ready:
            PC      = >:transmit _DO
            PC      = >:wait_for_tx_ready    ;loop wait

transmit:
            ;MARHI=:STACKPTR_ADDR_HI
            ;MARLO=[:STACKPTR]

            ; write te uart
            UART=REGA

return:
            ; ===================================START DO RETURN
            ; recover return address and save sp
            PC = REGB
            ; ===================================END DO RETURN
end:

END
