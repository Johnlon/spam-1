.include fp_multiply.asm

ret: WORD 0

a: WORD $0200
b: WORD $0200
fp_multiply_16_16 ret a b 
MARLO = [:ret+0]
MARHI = [:ret+1]
CHECK MARLO $00
CHECK MARHI $04

c: WORD $0280
d: WORD $0380
fp_multiply_16_16 ret c d 
MARLO = [:ret+0]
MARHI = [:ret+1]
CHECK MARLO $C0
CHECK MARHI $08

e: WORD $FE00 ; -2
f: WORD $FD00 ; -3
fp_multiply_16_16 ret e f 
MARLO = [:ret+0]
MARHI = [:ret+1]
CHECK MARLO $00
CHECK MARHI $06 

g: WORD $FE00 ; -2
h: WORD $0301 ; 3
fp_multiply_16_16 ret g h 
MARLO = [:ret+0]
MARHI = [:ret+1]
CHECK MARLO $FE
CHECK MARHI $F9 

g1: WORD $FE00 ; -2
h1: WORD $0300 ; 3
fp_multiply_16_16 ret g1 h1 
MARLO = [:ret+0]
MARHI = [:ret+1]
CHECK MARLO $00
CHECK MARHI $FA 


i: WORD (($0280 ^ $ffff) + 1) ; -2.5   (manually calc by doing two comp of 2.5)
j: WORD $0380
fp_multiply_16_16 ret i j 
MARLO = [:ret+0]
MARHI = [:ret+1]
; calc as -8.75 using 2's comp
CHECK MARLO $40
CHECK MARHI $F7 


HALT=0

error:
  HALT=1

END



.macro CHECK ACTUAL EXPECTED
  NOOP = ACTUAL - EXPECTED _S
  PCHITMP = <:error
  PC      = >:error ! _Z
.endmacro
