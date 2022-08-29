
;
; DIVISION FUNCTION AND CALLING MACRO
;
STASHA:        BYTE 0
STASHB:        BYTE 0

divide_return: WORD 0
V_DIVIDEND:    WORD $ff80
V_DIVISOR:     BYTE 3
V_QUOTIENT:    WORD 0
tmpDividendLo: BYTE 0
tmpDividendHi: BYTE 0
tmpDivisorLo:  BYTE 0
tmpDivisorHi:  BYTE 0
tmpRemainderLo:BYTE 0
tmpRemainderHi:BYTE 0
divideSign:    BYTE 0
divideLoopVar: BYTE 0

; ==================================================================================

division_16_16:

; 16 BIT DIVIDE OPERATION     
;                      quotient = dividend / divisor    <<  this algo accumulates the quotient in the dividend location
;                      dividend input is in V_DIVIDEND
;                      divisor input is V_DIVISOR
;                      quotient result V_QUOTIENT
;                      remainder (mod) is written to the var tmpRemainderLo
;                      return address in :divide_return
;
; setup input params
    ; load dividend lo and hi var from var V_DIVIDEND
    REGA = [:V_DIVIDEND]
    [:tmpDividendLo] = REGA
    REGA = [:V_DIVIDEND+1]
    [:tmpDividendHi] = REGA

    ; load divisor lo and hi var from var V_DIVISOR
    REGA = [:V_DIVISOR]
    [:tmpDivisorLo] = REGA
    REGA = [:V_DIVISOR+1]
    [:tmpDivisorHi] = REGA _S ; NOTE !! sets Z f the top byte of divisor is 0; Z used n 8 bit optimisation below

  divide_check_fast_method:
; FAST MODE CHECK: if both are +ve and 8 bit args then use direct ALU op
;   skip FAST MODE if divisor > 8 bit
    PCHITMP = < :divide_long_method
    PC      = > :divide_long_method ! _Z ; assumes Z FLAG WAS SET ABOVE divisor=HI
;   skip FAST MODE if dividend > 8 bit
    REGA   =   [:tmpDividendHi] _S ; sets Z if top byte of dividend is 0
    PCHITMP = < :divide_long_method
    PC      = > :divide_long_method ! _Z

  divide_fast_method:
; FAST MODE POSITIVE AND < 256 : Pure 8 bit execution
    [:tmpDividendHi] = 0
    [:tmpRemainderHi] = 0
    REGA   =   [:tmpDividendLo]
    REGB   =   [:tmpDivisorLo]
    [:tmpDividendLo] = REGA / REGB 
    [:tmpRemainderLo] = REGA % REGB 
    PCHITMP = < :divide_end_label
    PC      = > :divide_end_label

; SLOW MODE: 16 bit - using complicated algo I don't understand fully
  divide_long_method:

; https://electronics.stackexchange.com/questions/270920/binary-division-with-signed-numbers
; Signed integer divide is almost always done by taking absolute values, dividing, and then correcting the signs of quotient and remainder, or at least was in earlier CPUs.
; Algo:work out sign of result then convert to abs values for division and then convert back to signed
; Prep:work out sign of result
    REGA        = [:tmpDividendHi]
    REGA        = REGA A_AND_B 128
    REGB        = [:tmpDivisorHi]
    REGB        = REGB A_AND_B 128
    [:divideSign]      = REGA A_XOR_B REGB ; set 0 if they are same sign ie +ve result
    ;HALT         = [:divideSign]
    
; Prep:work out abs value of dividend
  divide_prep_dividend:
    REGA       = [:tmpDividendHi] _S
    PCHITMP     = < :divide_prep_divisor
    PC          = > :divide_prep_divisor ! _N ; jump next if is positive
    ; 2's complement
    REGA         = [:tmpDividendLo]
    REGA         = REGA A_XOR_B $ff
    [:tmpDividendLo] = REGA A_PLUS_B 1 _S
    REGA         = [:tmpDividendHi]
    REGA         = REGA A_XOR_B $ff
    [:tmpDividendHi] = REGA A_PLUS_B_PLUS_C 0

    ;MARHI         = [:tmpDividendHi]
    ;MARLO         = [:tmpDividendLo]
    ;HALT          = 99

    
; Prep:work out abs value of divisor
  divide_prep_divisor:
    REGA       = [:tmpDivisorHi] _S
    PCHITMP     = < :divide_division
    PC          = > :divide_division ! _N ; jump next if is positive
    ; 2's complement
    REGA         = [:tmpDivisorLo]
    REGA         = REGA A_XOR_B $ff
    [:tmpDivisorLo] = REGA A_PLUS_B 1 _S
    REGA         = [:tmpDivisorHi]
    REGA         = REGA A_XOR_B $ff
    [:tmpDivisorHi] = REGA A_PLUS_B_PLUS_C 0
    
    ;MARHI         = [:tmpDivisorHi]
    ;MARLO         = [:tmpDivisorLo]
    ;HALT          = 99

  divide_division:
; lda #0 -- NO NEED FOR LDA 0 IN SPAM1 IMPL AS I CAN DIRECTLY ASSIGN VARS BELOW

; sta remainder
    [:tmpRemainderLo] = 0

; sta remainder+1
    [:tmpRemainderHi] = 0

; ldx 16
    [:divideLoopVar] = 16

  divide_loop_label:

; asl dividendLo
    REGA         = [:tmpDividendLo]
    REGB         = REGA A_LSR_B 7 ; move carried out bit into RHS of byte
    [:tmpDividendLo] = REGA A_LSL_B 1

; rol dividendHi+C    rotate in the shifted out bit
    REGA         = [:tmpDividendHi]
    REGD       = REGA A_LSR_B 7
    REGA         = REGA A_LSL_B 1 ; sets the flags for the rol remainder block to consume
    [:tmpDividendHi] = REGA A_PLUS_B REGB; add the carry bit

; rol remainderLo
    REGA          = [:tmpRemainderLo]
    REGB          = REGA A_LSR_B 7 ; move carried out bit into RHS of byte
    REGA          = REGA A_LSL_B 1 ; sets the flags for the rol remainder block to consume
    [:tmpRemainderLo] = REGA A_PLUS_B REGD

; rol remainderHi
    REGA          = [:tmpRemainderHi]
    REGA          = REGA A_LSL_B 1 ; sets the flags for the rol remainder block to consume
    [:tmpRemainderHi] = REGA A_PLUS_B REGB


; lda remainder
    REGA = [:tmpRemainderLo]

; sec -  set carry - SBC uses the inverse of the carry bit so SEC is actually clearing carry
    ; NOT_USED = REGD B 0 _S ; clear carry << bit not needed as SPAM has dedicate MINUS without carry in

; sbc divisor
    REGA = REGA A_MINUS_B [:tmpDivisorLo] _S

; tay
    REGC = REGA

; lda remainder+1
    REGA = [:tmpRemainderHi]

; sbc divisor+1
    REGA = REGA A_MINUS_B_MINUS_C [:tmpDivisorHi] _S

; bcc skip -  if a cleared carry bit means carry occurred, then bcc means skip if carry occurred
    PCHITMP = < :divide_skip_label
    PC      = > :divide_skip_label _C

; sta remainder+1
    [:tmpRemainderHi] = REGA

; sty remainder
    [:tmpRemainderLo] = REGC

; inc result
    REGA     = [:tmpDividendLo]
    [:tmpDividendLo] = REGA + 1

  divide_skip_label:

; dex
    REGA    = [:divideLoopVar]
    [:divideLoopVar] = REGA - 1 _S

; bne divloop
    PCHITMP = < :divide_loop_label ; not equal so branch
    PC      = > :divide_loop_label ! _Z

  ; done long division so now adjust sign if necessary
  divide_end_long_label:
    
    ; fix sign of result - if result should be neg then 2's comp the result which is held in the dividend reg's
    NOOP    = [:divideSign] _S
    PCHITMP = < :divide_end_label
    PC      = > :divide_end_label ! _N ; jump to end if is +ve
    REGA         = [:tmpDividendLo]
    REGA         = REGA A_XOR_B $ff
    [:tmpDividendLo] = REGA A_PLUS_B 1 _S
    REGA         = [:tmpDividendHi]
    REGA         = REGA A_XOR_B $ff
    [:tmpDividendHi] = REGA A_PLUS_B_PLUS_C 0
    

  divide_end_label:

    REGA = [:tmpDividendLo]
    [:V_QUOTIENT] = REGA
    REGA = [:tmpDividendHi]
    [:V_QUOTIENT+1] = REGA

    PCHITMP = [:divide_return+1]
    PC      = [:divide_return]


; ============================================================================================

; wraps divide function with calling convention and return location
; Q = dividend / divisor
; vvk - var var const
.macro fp_divide_16_16_vvc quotient dividend divisor 
  ; stash x and y registers
  [:STASHA] = REGA
  [:STASHB] = REGB

  ; set up args
  REGA = [:dividend]
  [:V_DIVIDEND] = REGA
  REGA = [:dividend+1]
  [:V_DIVIDEND+1] = REGA

  REGA = >:divisor
  [:V_DIVISOR] = REGA
  REGA = <:divisor
  [:V_DIVISOR+1] = REGA

  ; return address
  [:divide_return+0] = >:after_division__LOCAL__
  [:divide_return+1] = <:after_division__LOCAL__

  ; call
  PCHITMP = < :division_16_16
  PC      = > :division_16_16 
  after_division__LOCAL__:

  ; get result
  REGA = [:V_QUOTIENT]
  [:quotient] = REGA

  REGA = [:V_QUOTIENT+1]
  [:quotient+1] = REGA

  ; restore registers
  REGA = [:STASHA]
  REGB = [:STASHB]

.endmacro

.macro fp_divide_16_8_vvc quotient dividend divisor 
  ; stash x and y registers
  [:STASHA] = REGA
  [:STASHB] = REGB

  ; set up args
  REGA = [:dividend]
  [:V_DIVIDEND] = REGA
  REGA = [:dividend+1]
  [:V_DIVIDEND+1] = REGA

  REGA = >:divisor
  [:V_DIVISOR] = REGA
  [:V_DIVISOR+1] = 0

  ; return address
  [:divide_return+0] = >:after_division__LOCAL__
  [:divide_return+1] = <:after_division__LOCAL__

  ; call
  PCHITMP = < :division_16_16
  PC      = > :division_16_16 
  after_division__LOCAL__:

  ; get result
  REGA = [:V_QUOTIENT]
  [:quotient] = REGA

  REGA = [:V_QUOTIENT+1]
  [:quotient+1] = REGA

  ; restore registers
  REGA = [:STASHA]
  REGB = [:STASHB]

.endmacro

