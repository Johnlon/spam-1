A: EQU 0
A1: EQU 1
A2: EQU 255
A3: EQU 256
A4: EQU 65535
A5: EQU 65536
A6: EQU -1

B:   EQU :A1+1+2               ; expect 3
B2:  EQU :A1+1+2               ; expect 4
B4:  EQU :A4+1+2               ; expect 5
BB:  EQU len(:B)+3+4           ; expect 8

L: EQU len(:A)      ;1
L1: EQU len(:A1)    ;1
L2: EQU len(:A2)    ;1
L3: EQU len(:A3)    ;2
L4: EQU len(:A4)    ;2
L5: EQU len(:A5)    ;3
;L5: EQU len(:A6)    ; // illegal
END