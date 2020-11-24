; run and grep "TRANS" to see uart transmit message

STACKTOP: EQU $ffff     ; location of top of stack
STACKPTR: EQU $feff     ; location of low byte of stack ptr addressa, top byte is a const, therefore 255 byte stack
STACKPTR_ADDR_HI: EQU <:STACKTOP   ;fixed
[:STACKPTR] = >:STACKTOP        ; ram location

CURCHAR_LO: EQU 0
CURCHAR_HI: EQU (:CURCHAR_LO+1)

; LOAD TO RAM AT THE CURRENT MEMORY[0] ONWARDSa AND SET THE LABEL TO THE FIRST BYTE
STRING_DATA:     STR     "Hello World\0"

            PCHITMP = 0

            [:CURCHAR_HI] = < :STRING_DATA
            [:CURCHAR_LO] = > :STRING_DATA

loop:
            ; read next char into REGA
            MARHI=[:CURCHAR_HI]
            MARLO=[:CURCHAR_LO]
            REGA=RAM _S

            ; goto end if printed last char
            PC = >:end _Z

            ; ===================================BEGIN CALL
            ; ===================================SETUP ARGS
            ; recover stack pointer into MAR
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]

            RAM=>:loop  ; put return address on stack

            ; move stack ptr
            MARLO=MARLO - 1

            ; put ARG on stack
            RAM=REGA

            ; move and save stack ptr
            [:STACKPTR] = MARLO-1
            ; end jump

            ; ===================================DO JUMP
            ; perform jump
            PC      = >:send_uart
            ; ===================================END CALL


send_uart:
            ; ==================================RECOVER ARGS
            ; recover stack ptr
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]

            ; move sp
            [:STACKPTR]=MARLO+1

            ; recover 1st arg
            REGA=RAM
            ; ===================================END RECOVER ARGS

wait_for_tx_ready:
            PC      = >:transmit _DO
            PC      = >:wait_for_tx_ready    ;loop wait

transmit:
            ; write te byute
            UART=RAM

return:
            ; ===================================START DO RETURN
            ; recover move and save sp
            MARHI=:STACKPTR_ADDR_HI
            MARLO=[:STACKPTR]
            [:STACKPTR]=MARLO+1

            PC = RAM
            ; ===================================END DO RETURN
end:

END
