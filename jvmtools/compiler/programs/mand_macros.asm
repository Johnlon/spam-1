

.macro var_eq_var RESULT V1
  REGC      = [:V1]
  [:RESULT] = REGC

  REGC      = [:V1+1]
  [:RESULT+1] = REGC
.endmacro

.macro var_eq_var_plus_var RESULT V1 V2
  REGC      = [:V1]
  REGC      = REGC A_PLUS_B [:V2] _S
  [:RESULT] = REGC

  REGC      = [:V1+1]
  REGC      = REGC A_PLUS_B_PLUS_C [:V2+1] 
  [:RESULT+1] = REGC
.endmacro

.macro var_eq_var_minus_var RESULT V1 V2
  REGC      = [:V1]
  REGC      = REGC A_MINUS_B [:V2] _S
  [:RESULT] = REGC

  REGC      = [:V1+1]
  REGC      = REGC A_MINUS_B_MINUS_C [:V2+1] 
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

.macro times2 V
  REGA = [:V]
  [:V] = REGA *LO 2 _S

  REGA = [:V+1]
  REGA = REGA *LO 2
  REGA = REGA A_PLUS_B_PLUS_C 0
  [:V+1] = REGA

.endmacro
  
.macro SEND_UART  value
  PCHITMP = < :send_uart_loop__#__
  send_uart_loop__#__:
  PC      = > :send_uart_loop__#__ ! _DO
  UART = value
.endmacro


.macro putc V
;  SEND_UART 'c'
;  SEND_UART V
.endmacro

.macro puth V
;  SEND_UART 'h'
;  SEND_UART V
.endmacro

.macro puts V
;  SEND_UART 's'
;  SEND_UART V
.endmacro

.macro putic V
  SEND_UART 'c'
  SEND_UART V
.endmacro

.macro putih V
  SEND_UART 'h'
  SEND_UART V
.endmacro

.macro putia V
  SEND_UART 'a'
  SEND_UART V
.endmacro

.macro putiA V
  SEND_UART 'A'
  SEND_UART V
.endmacro

