REGA=REGA A_PLUS_B 1
; directives

SOMEADDR: EQU (%1010 + $f + 1+2+(:LABEL2+33)) ; ignore comments

L2: EQU (1+2+(:SOMEADDR+33)) ; ignore comments
TOP: EQU <:SOMEADDR ; ignore comments
BOT: EQU >:SOMEADDR

; code
            REGA = :L2 _S
LABEL1:     REGA = REGB _A_S
            REGA = REGB _A
            REGA = REGB _S
            REGA = REGB _A_S
            REGA = REGB
            REGA = REGB A_PLUS_B REGC
            REGB = REGC A_PLUS_B [1000] _C_S
            REGA = REGB + 123
            REGA = REGB + REGC
            REGA = REGB - REGC
            REGA = REGB / REGC
            REGA = REGB *LO REGC
            REGA = REGB *HI REGC
            REGA = REGB % REGC
            REGA = REGB & REGC
            REGA = REGB | REGC
            REGA = REGB ^ REGC
            REGA = REGB !& REGC
            REGA = REGB << REGC
            REGA = REGB >> REGC
            REGA = REGB >>> REGC
            REGA = REGB >>> 15+1
            MARHI = REGB >>> 15+1
            MARHI = 15+1
            REGA= RAM
            ;[1234] = REGA >>> [1] ; illegal
LABEL2:
LABEL3:
            REGA=:TOP ; value of define
            REGA=:SOMEADDR  ; should fail!!
            REGA=<:LABEL1 ; address of label
A: EQU 2 ; override illegal for now - make this fail
            REGA=1

END