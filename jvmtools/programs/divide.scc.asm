; IsVar8But : RETURN_HI : root_function_main___VAR_RETURN_HI
root_function_main___VAR_RETURN_HI: EQU   0
root_function_main___VAR_RETURN_HI: BYTES [0]
; IsVar8But : RETURN_LO : root_function_main___VAR_RETURN_LO
root_function_main___VAR_RETURN_LO: EQU   1
root_function_main___VAR_RETURN_LO: BYTES [0]
; IsVar16 : a : root_function_main___VAR_a
root_function_main___VAR_a: EQU   2
root_function_main___VAR_a: BYTES [0, 0]
; IsVar16 : compoundBlkExpr2_6 : root_function_main_Compound2___VAR_compoundBlkExpr2_6
root_function_main_Compound2___VAR_compoundBlkExpr2_6: EQU   4
root_function_main_Compound2___VAR_compoundBlkExpr2_6: BYTES [0, 0]
; IsVar16 : divisor : root_function_main_Compound2_CHAIN0_0___VAR_divisor
root_function_main_Compound2_CHAIN0_0___VAR_divisor: EQU   6
root_function_main_Compound2_CHAIN0_0___VAR_divisor: BYTES [0, 0]
; IsVar16 : dividend : root_function_main_Compound2_CHAIN0_0___VAR_dividend
root_function_main_Compound2_CHAIN0_0___VAR_dividend: EQU   8
root_function_main_Compound2_CHAIN0_0___VAR_dividend: BYTES [0, 0]
; IsVar16 : remainder : root_function_main_Compound2_CHAIN0_0___VAR_remainder
root_function_main_Compound2_CHAIN0_0___VAR_remainder: EQU   10
root_function_main_Compound2_CHAIN0_0___VAR_remainder: BYTES [0, 0]
; IsVar16 : loopVar : root_function_main_Compound2_CHAIN0_0___VAR_loopVar
root_function_main_Compound2_CHAIN0_0___VAR_loopVar: EQU   12
root_function_main_Compound2_CHAIN0_0___VAR_loopVar: BYTES [0, 0]
; IsVar16 : c : root_function_main___VAR_c
root_function_main___VAR_c: EQU   14
root_function_main___VAR_c: BYTES [0, 0]
; IsVar16 : compoundBlkExprL2_11 : root_function_main_ifCond5___VAR_compoundBlkExprL2_11
root_function_main_ifCond5___VAR_compoundBlkExprL2_11: EQU   16
root_function_main_ifCond5___VAR_compoundBlkExprL2_11: BYTES [0, 0]
; IsVar16 : compoundBlkExprB2_12 : root_function_main_ifCond5___VAR_compoundBlkExprB2_12
root_function_main_ifCond5___VAR_compoundBlkExprB2_12: EQU   18
root_function_main_ifCond5___VAR_compoundBlkExprB2_12: BYTES [0, 0]
PCHITMP = < :ROOT________main_start
PC = > :ROOT________main_start
; (0)  ENTER root_function_main @ DefFunction(main,List(),List(DefUint16EqExpr(a,384), DefUint16EqExpr(c,a / (3)), IfCond(_EQ,ConditionComplex(c,==,128),List(LineComment(// pass), HaltVar(c)),List()), LineComment(// fail))) 2.5
       ROOT________main_start:
       root_function_main___LABEL_START:
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(a,384) 3.9
              ; (2)  ENTER root_function_main_Compound1 @ 384 <undefined position>
                     ; (3)  ENTER root_function_main_Compound1 @ 384 <undefined position>
                            REGA = > 384
                            REGD = < 384
                     ; (3)  EXIT  root_function_main_Compound1 @ 384
              ; (2)  EXIT  root_function_main_Compound1 @ 384
              [:root_function_main___VAR_a] = REGA
              [:root_function_main___VAR_a+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(a,384)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(c,a / (3)) 4.9
              ; (2)  ENTER root_function_main_Compound2 @ a / (3) <undefined position>
                     ; (3)  ENTER root_function_main_Compound2 @ a <undefined position>
                            REGA = [:root_function_main___VAR_a]
                            REGD = [:root_function_main___VAR_a + 1]
                     ; (3)  EXIT  root_function_main_Compound2 @ a
                     ; backup the clause 0 result to ram : [:root_function_main_Compound2___VAR_compoundBlkExpr2_6] = a 
                     [:root_function_main_Compound2___VAR_compoundBlkExpr2_6] = REGA
                     [:root_function_main_Compound2___VAR_compoundBlkExpr2_6+1] = REGD
                     ; (3)  ENTER root_function_main_Compound2_CHAIN0_0 @ 3 <undefined position>
                            REGA = > 3
                            REGD = < 3
                     ; (3)  EXIT  root_function_main_Compound2_CHAIN0_0 @ 3
                     ; concatenate clause 1 to ram [:root_function_main_Compound2___VAR_compoundBlkExpr2_6] <= / 3
                     ; DIVIDE OPERATION     quotient = dividend / divisor    << but this algo accumulates the quotient in the dividend location
                     ; setup input params
                         REGC = [:root_function_main_Compound2___VAR_compoundBlkExpr2_6]
                         [:root_function_main_Compound2_CHAIN0_0___VAR_dividend] = REGC
                         REGC = [:root_function_main_Compound2___VAR_compoundBlkExpr2_6+1]
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_dividend + 1)] = REGC
                         [:root_function_main_Compound2_CHAIN0_0___VAR_divisor] = REGA
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_divisor + 1)] = REGD _S
                         PCHITMP = < :root_function_main_Compound2_CHAIN0_0___LABEL_longDivideMethod_7
                         PC      = > :root_function_main_Compound2_CHAIN0_0___LABEL_longDivideMethod_7 ! _Z
                         REGC   =   [(:root_function_main_Compound2_CHAIN0_0___VAR_dividend + 1)] _S
                         PCHITMP = < :root_function_main_Compound2_CHAIN0_0___LABEL_longDivideMethod_7
                         PC      = > :root_function_main_Compound2_CHAIN0_0___LABEL_longDivideMethod_7 ! _Z
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_dividend + 1)] = 0
                         REGC   =   [:root_function_main_Compound2_CHAIN0_0___VAR_dividend]
                         [:root_function_main_Compound2_CHAIN0_0___VAR_dividend] = REGC / REGA
                         PCHITMP = < :root_function_main_Compound2_CHAIN0_0___LABEL_end_10
                         PC      = > :root_function_main_Compound2_CHAIN0_0___LABEL_end_10
                       root_function_main_Compound2_CHAIN0_0___LABEL_longDivideMethod_7:
                     ; lda #0 -- NO NEED IN SPAM1 IMPL
                     ; sta remainder
                         [:root_function_main_Compound2_CHAIN0_0___VAR_remainder] = 0
                     ; sta remainder+1
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_remainder + 1)] = 0
                     ; ldx 16
                         [:root_function_main_Compound2_CHAIN0_0___VAR_loopVar] = 16
                       root_function_main_Compound2_CHAIN0_0___LABEL_divLoop_8:
                     ; asl dividendLo
                         REGC         = [:root_function_main_Compound2_CHAIN0_0___VAR_dividend]
                         REGB         = REGC A_LSR_B 7
                         [:root_function_main_Compound2_CHAIN0_0___VAR_dividend] = REGC A_LSL_B 1
                     ; rol dividendHi+C    rotate in the shifted out bit
                         REGC         = [(:root_function_main_Compound2_CHAIN0_0___VAR_dividend + 1)]
                         REGA       = REGC A_LSR_B 7
                         REGC         = REGC A_LSL_B 1
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_dividend + 1)] = REGC A_PLUS_B REGB; add the carry bit
                     ; rol remainderLo
                         REGC          = [:root_function_main_Compound2_CHAIN0_0___VAR_remainder]
                         REGB          = REGC A_LSR_B 7
                         REGC          = REGC A_LSL_B 1
                         [:root_function_main_Compound2_CHAIN0_0___VAR_remainder] = REGC A_PLUS_B REGA
                     ; rol remainderHi
                         REGC          = [(:root_function_main_Compound2_CHAIN0_0___VAR_remainder + 1)]
                         REGC          = REGC A_LSL_B 1
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_remainder + 1)] = REGC A_PLUS_B REGB
                     ; lda remainder
                         REGC = [:root_function_main_Compound2_CHAIN0_0___VAR_remainder]
                     ; sec -  set carry - SBC uses the inverse of the carry bit so SEC is actualll clearing carry
                         ; NOT_USED = REGA B 0 _S ; clear carry << bit not needed as SPAM has dedicate MINUS without carry in
                     ; sbc divisor
                         REGC = REGC A_MINUS_B [:root_function_main_Compound2_CHAIN0_0___VAR_divisor] _S
                     ; tay - WORKHI is Y REG
                         REGD = REGC
                     ; lda remainder+1
                         REGC = [(:root_function_main_Compound2_CHAIN0_0___VAR_remainder + 1)]
                     ; sbc divisor+1
                         REGC = REGC A_MINUS_B_MINUS_C [(:root_function_main_Compound2_CHAIN0_0___VAR_divisor + 1)] _S
                     ; bcc skip -  if a cleared carry bit means carry occured, then bcc means skip if carry occured
                         PCHITMP = < :root_function_main_Compound2_CHAIN0_0___LABEL_skip_9
                         PC      = > :root_function_main_Compound2_CHAIN0_0___LABEL_skip_9 _C
                     ; sta remainder+1
                         [(:root_function_main_Compound2_CHAIN0_0___VAR_remainder + 1)] = REGC
                     ; sty remainder
                         [:root_function_main_Compound2_CHAIN0_0___VAR_remainder] = REGD
                     ; inc result
                         REGC     = [:root_function_main_Compound2_CHAIN0_0___VAR_dividend]
                         [:root_function_main_Compound2_CHAIN0_0___VAR_dividend] = REGC + 1
                       root_function_main_Compound2_CHAIN0_0___LABEL_skip_9:
                     ; dex
                         REGC    = [:root_function_main_Compound2_CHAIN0_0___VAR_loopVar]
                         [:root_function_main_Compound2_CHAIN0_0___VAR_loopVar] = REGC - 1 _S
                     ; bne divloop
                         PCHITMP = < :root_function_main_Compound2_CHAIN0_0___LABEL_divLoop_8 ; not equal so branch
                         PC      = > :root_function_main_Compound2_CHAIN0_0___LABEL_divLoop_8 ! _Z
                       root_function_main_Compound2_CHAIN0_0___LABEL_end_10:
                         REGC = [:root_function_main_Compound2_CHAIN0_0___VAR_dividend]
                         [:root_function_main_Compound2___VAR_compoundBlkExpr2_6] = REGC
                         REGC = [(:root_function_main_Compound2_CHAIN0_0___VAR_dividend + 1)]
                         [:root_function_main_Compound2___VAR_compoundBlkExpr2_6+1] = REGC
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_Compound2___VAR_compoundBlkExpr2_6]
                     REGD = [:root_function_main_Compound2___VAR_compoundBlkExpr2_6+1]
              ; (2)  EXIT  root_function_main_Compound2 @ a / (3)
              [:root_function_main___VAR_c] = REGA
              [:root_function_main___VAR_c+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(c,a / (3))
       ; (1)  ENTER root_function_main_ifCond5 @ IfCond(_EQ,ConditionComplex(c,==,128),List(LineComment(// pass), HaltVar(c)),List()) 5.9
              root_function_main_ifCond5___LABEL_CHECK:
              ; (2)  ENTER root_function_main_ifCond5 @ ConditionComplex(c,==,128) <undefined position>
                     ; (2)  ENTER root_function_main_ifCond5_Compound3 @ c <undefined position>
                            ; (3)  ENTER root_function_main_ifCond5_Compound3 @ c <undefined position>
                                   REGA = [:root_function_main___VAR_c]
                                   REGD = [:root_function_main___VAR_c + 1]
                            ; (3)  EXIT  root_function_main_ifCond5_Compound3 @ c
                     ; (2)  EXIT  root_function_main_ifCond5_Compound3 @ c
                     [:root_function_main_ifCond5___VAR_compoundBlkExprL2_11]     = REGA
                     [:root_function_main_ifCond5___VAR_compoundBlkExprL2_11 + 1] = REGD
                     ; (2)  ENTER root_function_main_ifCond5_Compound4 @ 128 <undefined position>
                            ; (3)  ENTER root_function_main_ifCond5_Compound4 @ 128 <undefined position>
                                   REGA = > 128
                                   REGD = < 128
                            ; (3)  EXIT  root_function_main_ifCond5_Compound4 @ 128
                     ; (2)  EXIT  root_function_main_ifCond5_Compound4 @ 128
                     [:root_function_main_ifCond5___VAR_compoundBlkExprB2_12]     = REGA
                     [:root_function_main_ifCond5___VAR_compoundBlkExprB2_12 + 1] = REGD
                     ; condition : == c 128
                     ; compare hi
                     REGD = [:root_function_main_ifCond5___VAR_compoundBlkExprL2_11 + 1]
                     REGD = REGD A_MINUS_B_SIGNEDMAG [:root_function_main_ifCond5___VAR_compoundBlkExprB2_12 + 1] _S
                     ; if was == then compare lo 
                     REGA = [:root_function_main_ifCond5___VAR_compoundBlkExprL2_11]
                     REGA = REGA A_MINUS_B [:root_function_main_ifCond5___VAR_compoundBlkExprB2_12] _EQ_S
              ; (2)  EXIT  root_function_main_ifCond5 @ ConditionComplex(c,==,128)
              PCHITMP = <:root_function_main_ifCond5___LABEL_BODY
              PC = >:root_function_main_ifCond5___LABEL_BODY _EQ
              PCHITMP = <:root_function_main_ifCond5___LABEL_ELSE
              PC = >:root_function_main_ifCond5___LABEL_ELSE
              root_function_main_ifCond5___LABEL_BODY:
                     ;  pass
              ; (2)  ENTER root_function_main_ifCond5_MATCH_haltVar_c_ @ HaltVar(c) 7.13
                     MARHI = [:root_function_main___VAR_c + 1]
                     MARLO = [:root_function_main___VAR_c]
                     HALT = 2
              ; (2)  EXIT  root_function_main_ifCond5_MATCH_haltVar_c_ @ HaltVar(c)
              PCHITMP = <:root_function_main_ifCond5___LABEL_AFTER
              PC = >:root_function_main_ifCond5___LABEL_AFTER
              root_function_main_ifCond5___LABEL_ELSE:
              root_function_main_ifCond5___LABEL_AFTER:
       ; (1)  EXIT  root_function_main_ifCond5 @ IfCond(_EQ,ConditionComplex(c,==,128),List(LineComment(// pass), HaltVar(c)),List())
              ;  fail
       PCHITMP = <:root_end
       PC = >:root_end
; (0)  EXIT  root_function_main @ DefFunction(main,List(),List(DefUint16EqExpr(a,384), DefUint16EqExpr(c,a / (3)), IfCond(_EQ,ConditionComplex(c,==,128),List(LineComment(// pass), HaltVar(c)),List()), LineComment(// fail)))
root_end:
END
