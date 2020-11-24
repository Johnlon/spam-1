; run and grep "TRANS" to see uart transmit message

; LOAD TO RAM AT THE CURRENT MEMORY[0] ONWARDSa AND SET THE LABEL TO THE FIRST BYTE
STRING_DATA:     STR     "Hello World\0"

            PCHITMP = 0

            MARHI= < :STRING_DATA
            MARLO= > :STRING_DATA

loop:
          ; push lop addr to stack
           MARHI=:STACKPTR_ADDR_HI
           MARLO=[:STACKPTR]

           RAM=>:loop  ; put return address on stack
           MARLO=MARLO - 1
           [:STACKPTR] = MARLO

           RAM=REGA    ; put ARG on stack
           MARLO=MARLO - 1
           [:STACKPTR] = MARLO



            REGC=RAM _S
            PC = >:end _Z

            REGD=>:loop

            MARLO = MARLO + 1 _S
            MARHI = MARHI + 1 _C

            PC      = >:send_uart


send_uart:  PC      = >:transmit _DO
            PC      = >:send_uart    ;loop wait

transmit:   UART=REGC
            PC = REGD
end:

END
