; https://youtu.be/BWQDAKTLFXk?t=815

; =============================

; calc mand and leave in 8.8 rep at mand_value
; args REGA=x REGB=y
mand_get:

  MAND_XMIN:     EQU $FD80 ; -2.5
  MAND_XMAX:     EQU $0380 ; 3.5
  MAND_YMIN:     EQU $FF00 ; -1
  MAND_YMAX:     EQU $0200 ; 2
  MAND_MAX_IT:   EQU 14 

  mand_x0:       BYTES [ 0, 0 ]
  mand_y0:       BYTES [ 0, 0 ]
  mand_x:        BYTES [ 0, 0 ]
  mand_y:        BYTES [ 0, 0 ]
  mand_x2:       BYTES [ 0, 0 ]
  mand_y2:       BYTES [ 0, 0 ]
  mand_xtemp:    BYTES [ 0, 0 ]
  mand_xy:       BYTES [ 0, 0 ]
  mand_value:    BYTES [ 0 ]

  mand_px:       BYTES [ 0 ]
  mand_py:       BYTES [ 0 ]

  mand_sum2:     BYTES [ 0, 0 ]

  FP_A:          BYTES [ 0, 0 ]
  FP_B:          BYTES [ 0, 0 ]
  FP_R:          BYTES [ 0, 0 ]

  [:mand_px] = REGA
  [:mand_py] = REGB

  ;MULT_TMP:      BYTES [ 0, 0 , 0 ] ; LO HI OVERFLOW


  ; === calc x0
  ; C = x * MAND_XMAX
  fp_multiply_8_16_vc  FP_B mand_px :MAND_XMAX
putc '\n' 
putc 32 
putc 'p'
putc 'x'
putc ':'
putc '$'
puth [:mand_px]
putc '*'
putc 'x'
putc 'm'
putc 'a'
putc 'x'
putc ':'
putc '$'
puth <:MAND_XMAX
puth >:MAND_XMAX
putc '=' 
putc '>' 
putc '$'
puth [:FP_B+1]
puth [:FP_B]


  ; A = (x * MAND_XMAX) / MAN_WIDTH
  fp_divide_16_8_vvc  FP_A FP_B MAND_WIDTH

putc 32 
putc '/' 
putc 32 
putc 'w'
putc 'i'
putc 'd'
putc ':'
putc '$'
puth :MAND_WIDTH

putc '=' 
putc '>' 
putc 32 
putc 'x'
putc 'd'
putc 'i'
putc 'v'
putc ':'
putc '$'
puth [:FP_A+1]
puth [:FP_A]

  ; X0 =  (x * MAND_XMAX / MAN_WIDTH) + min scaled X 
  var_eq_var_plus_const mand_x0 FP_A MAND_XMIN

putc 32 
putc '+'
putc 32 
putc 'x'
putc 'm'
putc 'i'
putc 'n'
putc ':'
putc '$'
puth <:MAND_XMIN
puth >:MAND_XMIN

putc '=' 
putc '>' 
putc 32 
putc 'x'
putc '0'
putc ':'
putc '$'
puth [:mand_x0+1]
puth [:mand_x0]
  
  ; === calc y0
  ; C = y * MAND_YMAX
  fp_multiply_8_16_vc  FP_B mand_py :MAND_YMAX
putc '\n' 
putc 32 
putc 'p'
putc 'y'
putc ':'
putc '$'
puth [:mand_py]
putc '*'
putc 'y'
putc 'm'
putc 'a'
putc 'x'
putc ':'
putc '$'
puth <:MAND_YMAX
puth >:MAND_YMAX
putc '=' 
putc '>' 
putc '$'
puth [:FP_B+1]
puth [:FP_B]

  ; A = (y * MAND_XMAX) / MAN_HEIGHT
  fp_divide_16_8_vvc  FP_A FP_B MAND_HEIGHT

putc 32 
putc '/' 
putc 32 
putc 'h'
putc 'g'
putc 't'
putc ':'
putc '$'
puth :MAND_HEIGHT

putc '=' 
putc '>' 
putc 32 
putc 'y'
putc 'd'
putc 'i'
putc 'v'
putc ':'
putc '$'
puth [:FP_A+1]
puth [:FP_A]


  ; X0 =  ((y * MAND_XMAX) / MAN_HEIGHT) +  min scaled Y
  var_eq_var_plus_const mand_y0 FP_A MAND_YMIN
putc 32 
putc '+'
putc 32 
putc 'y'
putc 'm'
putc 'i'
putc 'n'
putc ':'
putc '$'
puth <:MAND_YMIN
puth >:MAND_YMIN

putc '=' 
putc '>' 
putc 32 
putc 'y'
putc '0'
putc ':'
putc '$'
puth [:mand_y0+1]
puth [:mand_y0]
  
  
  ; loop 
  [:mand_x] = 0
  [:mand_x+1] = 0
  [:mand_y] = 0
  [:mand_y+1] = 0
  [:mand_value] = 0

  ; BEGIN INNER LOOP ===========================
  mand_loop:

putc '\n'
putc 32 
putc 32 
putc 'i'
putc ':'
putc '$'
puth [:mand_value]
putc 32

    REGA          = [:mand_value]
    NOOP          = REGA - :MAND_MAX_IT _S
    JMP_COND mand_break _Z


    fp_multiply_16_16_vv mand_x2 mand_x mand_x 
    fp_multiply_16_16_vv mand_y2 mand_y mand_y 

putc 32 
putc 'x'
putc ':'
putc '$'
puth [:mand_x+1]
puth [:mand_x]
putc 32
putc 'x'
putc '2'
putc ':'
putc '$'
puth [:mand_x2+1]
puth [:mand_x2]
putc 32
putc 'y'
putc ':'
putc '$'
puth [:mand_y+1]
puth [:mand_y]
putc 32
putc 'y'
putc '2'
putc ':'
putc '$'
puth [:mand_y2+1]
puth [:mand_y2]
putc 32

    var_eq_var_plus_var mand_sum2 mand_x2 mand_y2

putc 's'
putc '2'
putc ':'
putc '$'
puth [:mand_sum2+1]
puth [:mand_sum2]
putc 32

    ; IF X*X+Y*Y > 4 THEN BREAK
    REGA = [:mand_sum2+1]
    NOOP = REGA - $04 _S 
    REGA = [:mand_sum2+0]
    NOOP = REGA - $00 _EQ_S


    PCHITMP = <:mand_break 
    PC      = >:mand_break _GT

    var_eq_var_minus_var mand_xtemp mand_x2 mand_y2
putc 32
putc 'x'
putc '2'
putc '-'
putc 'y'
putc '2'
putc '='
putc '>'
putc '$'
puth [:mand_xtemp+1]
puth [:mand_xtemp]

    var_eq_var_plus_var mand_xtemp mand_xtemp mand_x0
putc 32
putc '+'
putc 'x'
putc '0'
putc '='
putc '>'
putc '$'
puth [:mand_xtemp+1]
puth [:mand_xtemp]
putc 32

    fp_multiply_16_16_vv mand_xy mand_x mand_y 
putc 32
putc 32
putc 'x'
putc '*'
putc 'y'
putc '='
putc '>'
putc '$'
puth [:mand_xy+1]
puth [:mand_xy]

    times2 mand_xy
putc 32
putc '*'
putc '2'
putc '='
putc '>'
putc '$'
puth [:mand_xy+1]
puth [:mand_xy]

    var_eq_var_plus_var mand_y mand_xy mand_y0
putc 32
putc '+'
putc 'y'
putc '0'
putc '='
putc '>'
putc 'y'
putc ':'
putc '$'
puth [:mand_y+1]
puth [:mand_y]
    
    var_eq_var mand_x mand_xtemp

    ; i ++ 
    REGA = [:mand_value]
    [:mand_value] = REGA + 1

    PCHITMP = <:mand_loop 
    PC      = >:mand_loop

  mand_break:
  ; END INNER LOOP ===========================

  REGA = [:mand_value]
  [:mand_value] = REGA - 1

  ; restore reg
  REGA = [:mand_px]
  REGB = [:mand_py]


  JMP mand_get_ret


