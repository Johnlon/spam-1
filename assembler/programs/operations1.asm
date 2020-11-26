VAL1: EQU 1
start: ;comment
VAL2: EQU $ff
VAL3: EQU $abcd
VAL4: EQU :VAL1
label: REGA=1

; assign ram
[:label]=REGB
RAM=REGB
[:label]=REGB _S
RAM=REGB _S


REGA=REGB
REGA=REGA + [:label]
REGA=REGB + REGC _S
REGA=REGB + REGC


; imediate values
REGA=12
REGA=12 _S
REGA=$ff
REGA=$ff _S
; imediate values by labels
REGA=:label
REGA=:label _S

; ram access by register
REGA=RAM
REGA=RAM _S
; ram access direct
REGA=[12]
REGA=[12] _S
REGA=[$ff]
REGA=[$ff] _S
REGA=[:label]
REGA=[:label] _S
;ops
END
