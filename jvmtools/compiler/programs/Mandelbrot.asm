
; needed for verilog sim
MARLO=0
MARHI=0
REGA=0
REGB=0
REGC=0
REGD=0

; =============================

MAND_WIDTH: EQU 32
MAND_HEIGHT: EQU 22

mand_value: BYTES[0]

; white, red, green, blue
; orange, lt red, brown, dk gray
; md gray, lt green, lt blue, lt gray
; putple, yellow, black
color_codes: BYTES[
   $05, $1C, $1E, $1F,   
   $81, $96, $95, $97,  
   $98, $99, $9A, $9B, 
   $9C, $9E, $90      
  ]


REGA = 0 ; x
REGB = 0 ; y


loop:
  JMP mand_get
  mand_get_ret:


  REGC=[:mand_value]

  MAR_INDEX color_codes REGC
  REGC=RAM

  SEND_UART REGC
  
  ; inc X
  REGA = REGA + 1

check_x:

  ; if x = MAND_WIDTH then echo \n
  NOOP = REGA A_MINUS_B :MAND_WIDTH _S
  JMP_NOT next_loop _Z 
  
  SEND_UART '\n'
  REGA = 0

  ; inc Y
  REGB = REGB + 1

  ; if y = MAND_HEIGHT then exit
  NOOP = REGB A_MINUS_B :MAND_HEIGHT _S
  JMP_NOT next_loop _Z 
  JMP prog_end

next_loop:
  JMP loop

prog_end:
  MARLO=[:V_QUOTIENT]
  MARHI=[:V_QUOTIENT+1]
  HALT = 0



; calc mand and leave in 8.8 rep at mand_value
; args REGA=x REGB=y
mand_get:

  MAND_XMIN:     EQU $FD80 ; -2.5
  MAND_XMAX:     EQU $0380 ; 3.5
  MAND_YMIN:     EQU $FF00 ; -1
  MAND_YMAX:     EQU $0200 ; 2
  MAND_MAX_IT:   EQU 15 

  mand_x0:       BYTES [ 0, 0 ]
  mand_y0:       BYTES [ 0, 0 ]
  mand_x:        BYTES [ 0, 0 ]
  mand_y:        BYTES [ 0, 0 ]
  mand_x2:       BYTES [ 0, 0 ]
  mand_y2:       BYTES [ 0, 0 ]
  mand_xtemp:    BYTES [ 0, 0 ]

  FP_A:          BYTES [ 0, 0 ]
  FP_B:          BYTES [ 0, 0 ]
  FP_C:          BYTES [ 0, 0 ]
  FP_R:          BYTES [ 0, 0 ]

  STASHA:        BYTES [ 0 ]
  STASHB:        BYTES [ 0 ]

  ;MULT_TMP:      BYTES [ 0, 0 , 0 ] ; LO HI OVERFLOW


  ; C = x * MAND_MAX
  fp_multiply_8_16  FP_C REGA MAND_XMAX

  ; A = C
  var_cp FP_A FP_C
  
  ; B = MAND_WIDTH
  var_eq_const FP_B MAND_WIDTH

  ; A = A/B ---- NEEDS SHORT OR SOMETING??? NO COS RIGHT IS 8BIT
  fp_divide_16_16  FP_A FP_A FP_B a_over_b

  ; C =  A+ min scaled X
  var_eq_var_plus_const FP_C FP_A MAND_XMIN
  

  [:mand_value] = 2
  JMP mand_get_ret

.include division.asm
.include fp_multiply.asm


END
.macro var_eq_var_plus_var RESULT V1 V2
  REGC      = [:V1]
  REGC      = REGC A_PLUS_B [:V2] _S
  [:RESULT] = REGC

  REGC      = [:V1+1]
  REGC      = REGC A_PLUS_B_PLUS_C [:V2+1] 
  [:RESULT+1] = REGC
.endmacro

.macro var_eq_var_plus_const RESULT V K
  REGC      = [:V]
  REGC      = REGC A_PLUS_B >:K _S
  [:RESULT] = REGC

  REGC      = [:V+1]
  REGC      = REGC A_PLUS_B_PLUS_C <:K
  [:RESULT+1] = REGC
.endmacro


.macro var_cp to from
   REGC = [ :from ]
   [ :to ] = REGC

   REGC = [ :from + 1]
   [ :to + 1 ] = REGC
.endmacro

.macro var_eq_const to const
   [ :to ] = > :const
   [ :to + 1 ] = < :const
.endmacro

.macro JMP  label
  PCHITMP = < :label
  PC      = > :label
.endmacro

.macro JMP_COND  label cond
  PCHITMP = < :label
  PC      = > :label cond
.endmacro

.macro JMP_NOT  label cond
  PCHITMP = < :label
  PC      = > :label ! cond
.endmacro


.macro MAR_INDEX  label offset_reg
  MARLO = offset_reg A_PLUS_B >:label _S
  MARHI = <:label
  MARHI = MARHI A_PLUS_B_PLUS_C 0
.endmacro

.macro SEND_UART  value
  PCHITMP = < :send_uart_loop
  send_uart_loop:
  PC      = > :send_uart_loop ! _DO
  UART = value
.endmacro

