; IsVar : RETURN_HI : root_function_main___VAR_RETURN_HI
root_function_main___VAR_RETURN_HI: EQU   0
root_function_main___VAR_RETURN_HI: BYTES [0]
; IsVar : RETURN_LO : root_function_main___VAR_RETURN_LO
root_function_main___VAR_RETURN_LO: EQU   1
root_function_main___VAR_RETURN_LO: BYTES [0]
; IsVar : loop : root_function_main___VAR_loop
root_function_main___VAR_loop: EQU   2
root_function_main___VAR_loop: BYTES [0]
; IsVar : NONE : root_function_main___VAR_NONE
root_function_main___VAR_NONE: EQU   3
root_function_main___VAR_NONE: BYTES [0]
; IsVar : UP : root_function_main___VAR_UP
root_function_main___VAR_UP: EQU   4
root_function_main___VAR_UP: BYTES [0]
; IsVar : DOWN : root_function_main___VAR_DOWN
root_function_main___VAR_DOWN: EQU   5
root_function_main___VAR_DOWN: BYTES [0]
; IsVar : RIGHT : root_function_main___VAR_RIGHT
root_function_main___VAR_RIGHT: EQU   6
root_function_main___VAR_RIGHT: BYTES [0]
; IsVar : LEFT : root_function_main___VAR_LEFT
root_function_main___VAR_LEFT: EQU   7
root_function_main___VAR_LEFT: BYTES [0]
; IsVar : DRAW : root_function_main___VAR_DRAW
root_function_main___VAR_DRAW: EQU   8
root_function_main___VAR_DRAW: BYTES [0]
; IsVar : b : root_function_main_whileCond5___VAR_b
root_function_main_whileCond5___VAR_b: EQU   9
root_function_main_whileCond5___VAR_b: BYTES [0]
; IsVar : compoundBlkExpr4_12 : root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12
root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12: EQU   10
root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12: BYTES [0]
; IsVar : compoundBlkExpr4_15 : root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15
root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15: EQU   11
root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15: BYTES [0]
; IsVar : compoundBlkExpr4_18 : root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18
root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18: EQU   12
root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18: BYTES [0]
; IsVar : compoundBlkExpr4_21 : root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21
root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21: EQU   13
root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21: BYTES [0]
; IsVar : compoundBlkExpr3_22 : root_function_main_whileCond5___VAR_compoundBlkExpr3_22
root_function_main_whileCond5___VAR_compoundBlkExpr3_22: EQU   14
root_function_main_whileCond5___VAR_compoundBlkExpr3_22: BYTES [0]
; IsVar : a : root_function_main_whileCond5___VAR_a
root_function_main_whileCond5___VAR_a: EQU   15
root_function_main_whileCond5___VAR_a: BYTES [0]
; IsVar : compoundBlkExpr3_31 : root_function_main_whileCond5___VAR_compoundBlkExpr3_31
root_function_main_whileCond5___VAR_compoundBlkExpr3_31: EQU   16
root_function_main_whileCond5___VAR_compoundBlkExpr3_31: BYTES [0]
PCHITMP = < :ROOT________main_start
PC = > :ROOT________main_start
; (0) ENTER root_function_main @ DefFunction(main,List(),List(DefUint8EqExpr(loop,0), DefUint8EqExpr(NONE,0), DefUint8EqExpr(UP,2), DefUint8EqExpr(DOWN,3), DefUint8EqExpr(RIGHT,5), DefUint8EqExpr(LEFT,4), DefUint8EqExpr(DRAW,10), PutcharVar(NONE), PutcharVar(NONE), WhileCond(_Z,Condition(loop,<=,26),List(DefUint8EqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(RIGHT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(DOWN), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(LEFT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(UP), LetVarEqExpr(b,b - (1)))), DefUint8EqExpr(a,33 + (loop)), PutcharVar(DRAW), PutcharVar(a), Comment(// offset next cycle), PutcharVar(RIGHT), PutcharVar(DOWN), LetVarEqExpr(loop,loop + (1)))))) 2.1
      ROOT________main_start:
      root_function_main___LABEL_START:
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(loop,0) 3.5
             ; (2)   ENTER root_function_main @ 0 <undefined position>
                     ; (3)    ENTER root_function_main @ 0 <undefined position>
                              REGA = 0
                     ; (3)    EXIT  root_function_main @ 0
             ; (2)   EXIT  root_function_main @ 0
             [:root_function_main___VAR_loop] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(loop,0)
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(NONE,0) 4.5
             ; (2)   ENTER root_function_main @ 0 <undefined position>
                     ; (3)    ENTER root_function_main @ 0 <undefined position>
                              REGA = 0
                     ; (3)    EXIT  root_function_main @ 0
             ; (2)   EXIT  root_function_main @ 0
             [:root_function_main___VAR_NONE] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(NONE,0)
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(UP,2) 5.5
             ; (2)   ENTER root_function_main @ 2 <undefined position>
                     ; (3)    ENTER root_function_main @ 2 <undefined position>
                              REGA = 2
                     ; (3)    EXIT  root_function_main @ 2
             ; (2)   EXIT  root_function_main @ 2
             [:root_function_main___VAR_UP] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(UP,2)
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(DOWN,3) 6.5
             ; (2)   ENTER root_function_main @ 3 <undefined position>
                     ; (3)    ENTER root_function_main @ 3 <undefined position>
                              REGA = 3
                     ; (3)    EXIT  root_function_main @ 3
             ; (2)   EXIT  root_function_main @ 3
             [:root_function_main___VAR_DOWN] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(DOWN,3)
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(RIGHT,5) 7.5
             ; (2)   ENTER root_function_main @ 5 <undefined position>
                     ; (3)    ENTER root_function_main @ 5 <undefined position>
                              REGA = 5
                     ; (3)    EXIT  root_function_main @ 5
             ; (2)   EXIT  root_function_main @ 5
             [:root_function_main___VAR_RIGHT] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(RIGHT,5)
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(LEFT,4) 8.5
             ; (2)   ENTER root_function_main @ 4 <undefined position>
                     ; (3)    ENTER root_function_main @ 4 <undefined position>
                              REGA = 4
                     ; (3)    EXIT  root_function_main @ 4
             ; (2)   EXIT  root_function_main @ 4
             [:root_function_main___VAR_LEFT] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(LEFT,4)
      ; (1)  ENTER root_function_main @ DefUint8EqExpr(DRAW,10) 9.5
             ; (2)   ENTER root_function_main @ 10 <undefined position>
                     ; (3)    ENTER root_function_main @ 10 <undefined position>
                              REGA = 10
                     ; (3)    EXIT  root_function_main @ 10
             ; (2)   EXIT  root_function_main @ 10
             [:root_function_main___VAR_DRAW] = REGA
      ; (1)  EXIT  root_function_main @ DefUint8EqExpr(DRAW,10)
      ; (1)  ENTER root_function_main_putcharVar_NONE_ @ PutcharVar(NONE) 11.5
             root_function_main_putcharVar_NONE____LABEL_wait_6:
             PCHITMP = <:root_function_main_putcharVar_NONE____LABEL_transmit_7
             PC = >:root_function_main_putcharVar_NONE____LABEL_transmit_7 _DO
             PCHITMP = <:root_function_main_putcharVar_NONE____LABEL_wait_6
             PC = >:root_function_main_putcharVar_NONE____LABEL_wait_6
             root_function_main_putcharVar_NONE____LABEL_transmit_7:
             UART = [:root_function_main___VAR_NONE]
      ; (1)  EXIT  root_function_main_putcharVar_NONE_ @ PutcharVar(NONE)
      ; (1)  ENTER root_function_main_putcharVar_NONE_ @ PutcharVar(NONE) 12.5
             root_function_main_putcharVar_NONE____LABEL_wait_8:
             PCHITMP = <:root_function_main_putcharVar_NONE____LABEL_transmit_9
             PC = >:root_function_main_putcharVar_NONE____LABEL_transmit_9 _DO
             PCHITMP = <:root_function_main_putcharVar_NONE____LABEL_wait_8
             PC = >:root_function_main_putcharVar_NONE____LABEL_wait_8
             root_function_main_putcharVar_NONE____LABEL_transmit_9:
             UART = [:root_function_main___VAR_NONE]
      ; (1)  EXIT  root_function_main_putcharVar_NONE_ @ PutcharVar(NONE)
      ; (1)  ENTER root_function_main_whileCond5 @ WhileCond(_Z,Condition(loop,<=,26),List(DefUint8EqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(RIGHT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(DOWN), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(LEFT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(UP), LetVarEqExpr(b,b - (1)))), DefUint8EqExpr(a,33 + (loop)), PutcharVar(DRAW), PutcharVar(a), Comment(// offset next cycle), PutcharVar(RIGHT), PutcharVar(DOWN), LetVarEqExpr(loop,loop + (1)))) 14.5
             root_function_main_whileCond5___LABEL_CHECK:
             ; (2)   ENTER root_function_main_whileCond5 @ Condition(loop,<=,26) <undefined position>
                     ; condition :  loop <= 26
                     REGA = [:root_function_main___VAR_loop]
                     REGA = REGA A_MINUS_B_SIGNEDMAG 26 _S
                     REGA = 1
                     REGA = 0 _LT
                     REGA = 0 _EQ
                     REGA = REGA _S
             ; (2)   EXIT  root_function_main_whileCond5 @ Condition(loop,<=,26)
             PCHITMP = <:root_function_main_whileCond5___LABEL_BODY
             PC = >:root_function_main_whileCond5___LABEL_BODY _Z
             PCHITMP = <:root_function_main_whileCond5___LABEL_AFTER
             PC = >:root_function_main_whileCond5___LABEL_AFTER
             root_function_main_whileCond5___LABEL_BODY:
             ; (2)   ENTER root_function_main_whileCond5 @ DefUint8EqExpr(b,10) 16.9
                     ; (3)    ENTER root_function_main_whileCond5 @ 10 <undefined position>
                              ; (4)     ENTER root_function_main_whileCond5 @ 10 <undefined position>
                                        REGA = 10
                              ; (4)     EXIT  root_function_main_whileCond5 @ 10
                     ; (3)    EXIT  root_function_main_whileCond5 @ 10
                     [:root_function_main_whileCond5___VAR_b] = REGA
             ; (2)   EXIT  root_function_main_whileCond5 @ DefUint8EqExpr(b,10)
             ; (2)   ENTER root_function_main_whileCond5_whileCond1 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(RIGHT), LetVarEqExpr(b,b - (1)))) 17.9
                     root_function_main_whileCond5_whileCond1___LABEL_CHECK:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond1 @ Condition(b,>,0) <undefined position>
                              ; condition :  b > 0
                              REGA = [:root_function_main_whileCond5___VAR_b]
                              REGA = REGA A_MINUS_B_SIGNEDMAG 0 _S
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond1 @ Condition(b,>,0)
                     PCHITMP = <:root_function_main_whileCond5_whileCond1___LABEL_BODY
                     PC = >:root_function_main_whileCond5_whileCond1___LABEL_BODY _GT
                     PCHITMP = <:root_function_main_whileCond5_whileCond1___LABEL_AFTER
                     PC = >:root_function_main_whileCond5_whileCond1___LABEL_AFTER
                     root_function_main_whileCond5_whileCond1___LABEL_BODY:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond1_putcharVar_RIGHT_ @ PutcharVar(RIGHT) 18.13
                              root_function_main_whileCond5_whileCond1_putcharVar_RIGHT____LABEL_wait_10:
                              PCHITMP = <:root_function_main_whileCond5_whileCond1_putcharVar_RIGHT____LABEL_transmit_11
                              PC = >:root_function_main_whileCond5_whileCond1_putcharVar_RIGHT____LABEL_transmit_11 _DO
                              PCHITMP = <:root_function_main_whileCond5_whileCond1_putcharVar_RIGHT____LABEL_wait_10
                              PC = >:root_function_main_whileCond5_whileCond1_putcharVar_RIGHT____LABEL_wait_10
                              root_function_main_whileCond5_whileCond1_putcharVar_RIGHT____LABEL_transmit_11:
                              UART = [:root_function_main___VAR_RIGHT]
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond1_putcharVar_RIGHT_ @ PutcharVar(RIGHT)
                     ; (3)    ENTER root_function_main_whileCond5_whileCond1 @ LetVarEqExpr(b,b - (1)) 19.13
                              ; (4)     ENTER root_function_main_whileCond5_whileCond1 @ b - (1) <undefined position>
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond1 @ b <undefined position>
                                                   REGA = [:root_function_main_whileCond5___VAR_b]
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond1 @ b
                                        ; assign clause 1 result to [:root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12] = b 
                                        [:root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12] = REGA
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond1 @ 1 <undefined position>
                                                   REGA = 1
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond1 @ 1
                                        ; concatenate clause 2 to [:root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12] <= - 1
                                        REGC = [:root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12]
                                        [:root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12] = REGC - REGA
                                        ; assigning result back to REGA
                                        REGA = [:root_function_main_whileCond5_whileCond1___VAR_compoundBlkExpr4_12]
                              ; (4)     EXIT  root_function_main_whileCond5_whileCond1 @ b - (1)
                              [:root_function_main_whileCond5___VAR_b] = REGA
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond1 @ LetVarEqExpr(b,b - (1))
                     PCHITMP = <:root_function_main_whileCond5_whileCond1___LABEL_CHECK
                     PC = >:root_function_main_whileCond5_whileCond1___LABEL_CHECK
                     root_function_main_whileCond5_whileCond1___LABEL_AFTER:
             ; (2)   EXIT  root_function_main_whileCond5_whileCond1 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(RIGHT), LetVarEqExpr(b,b - (1))))
             ; (2)   ENTER root_function_main_whileCond5 @ LetVarEqExpr(b,10) 21.9
                     ; (3)    ENTER root_function_main_whileCond5 @ 10 <undefined position>
                              ; (4)     ENTER root_function_main_whileCond5 @ 10 <undefined position>
                                        REGA = 10
                              ; (4)     EXIT  root_function_main_whileCond5 @ 10
                     ; (3)    EXIT  root_function_main_whileCond5 @ 10
                     [:root_function_main_whileCond5___VAR_b] = REGA
             ; (2)   EXIT  root_function_main_whileCond5 @ LetVarEqExpr(b,10)
             ; (2)   ENTER root_function_main_whileCond5_whileCond2 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(DOWN), LetVarEqExpr(b,b - (1)))) 22.9
                     root_function_main_whileCond5_whileCond2___LABEL_CHECK:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond2 @ Condition(b,>,0) <undefined position>
                              ; condition :  b > 0
                              REGA = [:root_function_main_whileCond5___VAR_b]
                              REGA = REGA A_MINUS_B_SIGNEDMAG 0 _S
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond2 @ Condition(b,>,0)
                     PCHITMP = <:root_function_main_whileCond5_whileCond2___LABEL_BODY
                     PC = >:root_function_main_whileCond5_whileCond2___LABEL_BODY _GT
                     PCHITMP = <:root_function_main_whileCond5_whileCond2___LABEL_AFTER
                     PC = >:root_function_main_whileCond5_whileCond2___LABEL_AFTER
                     root_function_main_whileCond5_whileCond2___LABEL_BODY:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond2_putcharVar_DOWN_ @ PutcharVar(DOWN) 23.13
                              root_function_main_whileCond5_whileCond2_putcharVar_DOWN____LABEL_wait_13:
                              PCHITMP = <:root_function_main_whileCond5_whileCond2_putcharVar_DOWN____LABEL_transmit_14
                              PC = >:root_function_main_whileCond5_whileCond2_putcharVar_DOWN____LABEL_transmit_14 _DO
                              PCHITMP = <:root_function_main_whileCond5_whileCond2_putcharVar_DOWN____LABEL_wait_13
                              PC = >:root_function_main_whileCond5_whileCond2_putcharVar_DOWN____LABEL_wait_13
                              root_function_main_whileCond5_whileCond2_putcharVar_DOWN____LABEL_transmit_14:
                              UART = [:root_function_main___VAR_DOWN]
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond2_putcharVar_DOWN_ @ PutcharVar(DOWN)
                     ; (3)    ENTER root_function_main_whileCond5_whileCond2 @ LetVarEqExpr(b,b - (1)) 24.13
                              ; (4)     ENTER root_function_main_whileCond5_whileCond2 @ b - (1) <undefined position>
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond2 @ b <undefined position>
                                                   REGA = [:root_function_main_whileCond5___VAR_b]
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond2 @ b
                                        ; assign clause 1 result to [:root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15] = b 
                                        [:root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15] = REGA
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond2 @ 1 <undefined position>
                                                   REGA = 1
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond2 @ 1
                                        ; concatenate clause 2 to [:root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15] <= - 1
                                        REGC = [:root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15]
                                        [:root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15] = REGC - REGA
                                        ; assigning result back to REGA
                                        REGA = [:root_function_main_whileCond5_whileCond2___VAR_compoundBlkExpr4_15]
                              ; (4)     EXIT  root_function_main_whileCond5_whileCond2 @ b - (1)
                              [:root_function_main_whileCond5___VAR_b] = REGA
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond2 @ LetVarEqExpr(b,b - (1))
                     PCHITMP = <:root_function_main_whileCond5_whileCond2___LABEL_CHECK
                     PC = >:root_function_main_whileCond5_whileCond2___LABEL_CHECK
                     root_function_main_whileCond5_whileCond2___LABEL_AFTER:
             ; (2)   EXIT  root_function_main_whileCond5_whileCond2 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(DOWN), LetVarEqExpr(b,b - (1))))
             ; (2)   ENTER root_function_main_whileCond5 @ LetVarEqExpr(b,10) 26.9
                     ; (3)    ENTER root_function_main_whileCond5 @ 10 <undefined position>
                              ; (4)     ENTER root_function_main_whileCond5 @ 10 <undefined position>
                                        REGA = 10
                              ; (4)     EXIT  root_function_main_whileCond5 @ 10
                     ; (3)    EXIT  root_function_main_whileCond5 @ 10
                     [:root_function_main_whileCond5___VAR_b] = REGA
             ; (2)   EXIT  root_function_main_whileCond5 @ LetVarEqExpr(b,10)
             ; (2)   ENTER root_function_main_whileCond5_whileCond3 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(LEFT), LetVarEqExpr(b,b - (1)))) 27.9
                     root_function_main_whileCond5_whileCond3___LABEL_CHECK:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond3 @ Condition(b,>,0) <undefined position>
                              ; condition :  b > 0
                              REGA = [:root_function_main_whileCond5___VAR_b]
                              REGA = REGA A_MINUS_B_SIGNEDMAG 0 _S
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond3 @ Condition(b,>,0)
                     PCHITMP = <:root_function_main_whileCond5_whileCond3___LABEL_BODY
                     PC = >:root_function_main_whileCond5_whileCond3___LABEL_BODY _GT
                     PCHITMP = <:root_function_main_whileCond5_whileCond3___LABEL_AFTER
                     PC = >:root_function_main_whileCond5_whileCond3___LABEL_AFTER
                     root_function_main_whileCond5_whileCond3___LABEL_BODY:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond3_putcharVar_LEFT_ @ PutcharVar(LEFT) 28.13
                              root_function_main_whileCond5_whileCond3_putcharVar_LEFT____LABEL_wait_16:
                              PCHITMP = <:root_function_main_whileCond5_whileCond3_putcharVar_LEFT____LABEL_transmit_17
                              PC = >:root_function_main_whileCond5_whileCond3_putcharVar_LEFT____LABEL_transmit_17 _DO
                              PCHITMP = <:root_function_main_whileCond5_whileCond3_putcharVar_LEFT____LABEL_wait_16
                              PC = >:root_function_main_whileCond5_whileCond3_putcharVar_LEFT____LABEL_wait_16
                              root_function_main_whileCond5_whileCond3_putcharVar_LEFT____LABEL_transmit_17:
                              UART = [:root_function_main___VAR_LEFT]
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond3_putcharVar_LEFT_ @ PutcharVar(LEFT)
                     ; (3)    ENTER root_function_main_whileCond5_whileCond3 @ LetVarEqExpr(b,b - (1)) 29.13
                              ; (4)     ENTER root_function_main_whileCond5_whileCond3 @ b - (1) <undefined position>
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond3 @ b <undefined position>
                                                   REGA = [:root_function_main_whileCond5___VAR_b]
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond3 @ b
                                        ; assign clause 1 result to [:root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18] = b 
                                        [:root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18] = REGA
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond3 @ 1 <undefined position>
                                                   REGA = 1
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond3 @ 1
                                        ; concatenate clause 2 to [:root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18] <= - 1
                                        REGC = [:root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18]
                                        [:root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18] = REGC - REGA
                                        ; assigning result back to REGA
                                        REGA = [:root_function_main_whileCond5_whileCond3___VAR_compoundBlkExpr4_18]
                              ; (4)     EXIT  root_function_main_whileCond5_whileCond3 @ b - (1)
                              [:root_function_main_whileCond5___VAR_b] = REGA
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond3 @ LetVarEqExpr(b,b - (1))
                     PCHITMP = <:root_function_main_whileCond5_whileCond3___LABEL_CHECK
                     PC = >:root_function_main_whileCond5_whileCond3___LABEL_CHECK
                     root_function_main_whileCond5_whileCond3___LABEL_AFTER:
             ; (2)   EXIT  root_function_main_whileCond5_whileCond3 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(LEFT), LetVarEqExpr(b,b - (1))))
             ; (2)   ENTER root_function_main_whileCond5 @ LetVarEqExpr(b,10) 32.9
                     ; (3)    ENTER root_function_main_whileCond5 @ 10 <undefined position>
                              ; (4)     ENTER root_function_main_whileCond5 @ 10 <undefined position>
                                        REGA = 10
                              ; (4)     EXIT  root_function_main_whileCond5 @ 10
                     ; (3)    EXIT  root_function_main_whileCond5 @ 10
                     [:root_function_main_whileCond5___VAR_b] = REGA
             ; (2)   EXIT  root_function_main_whileCond5 @ LetVarEqExpr(b,10)
             ; (2)   ENTER root_function_main_whileCond5_whileCond4 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(UP), LetVarEqExpr(b,b - (1)))) 33.9
                     root_function_main_whileCond5_whileCond4___LABEL_CHECK:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond4 @ Condition(b,>,0) <undefined position>
                              ; condition :  b > 0
                              REGA = [:root_function_main_whileCond5___VAR_b]
                              REGA = REGA A_MINUS_B_SIGNEDMAG 0 _S
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond4 @ Condition(b,>,0)
                     PCHITMP = <:root_function_main_whileCond5_whileCond4___LABEL_BODY
                     PC = >:root_function_main_whileCond5_whileCond4___LABEL_BODY _GT
                     PCHITMP = <:root_function_main_whileCond5_whileCond4___LABEL_AFTER
                     PC = >:root_function_main_whileCond5_whileCond4___LABEL_AFTER
                     root_function_main_whileCond5_whileCond4___LABEL_BODY:
                     ; (3)    ENTER root_function_main_whileCond5_whileCond4_putcharVar_UP_ @ PutcharVar(UP) 34.13
                              root_function_main_whileCond5_whileCond4_putcharVar_UP____LABEL_wait_19:
                              PCHITMP = <:root_function_main_whileCond5_whileCond4_putcharVar_UP____LABEL_transmit_20
                              PC = >:root_function_main_whileCond5_whileCond4_putcharVar_UP____LABEL_transmit_20 _DO
                              PCHITMP = <:root_function_main_whileCond5_whileCond4_putcharVar_UP____LABEL_wait_19
                              PC = >:root_function_main_whileCond5_whileCond4_putcharVar_UP____LABEL_wait_19
                              root_function_main_whileCond5_whileCond4_putcharVar_UP____LABEL_transmit_20:
                              UART = [:root_function_main___VAR_UP]
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond4_putcharVar_UP_ @ PutcharVar(UP)
                     ; (3)    ENTER root_function_main_whileCond5_whileCond4 @ LetVarEqExpr(b,b - (1)) 35.13
                              ; (4)     ENTER root_function_main_whileCond5_whileCond4 @ b - (1) <undefined position>
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond4 @ b <undefined position>
                                                   REGA = [:root_function_main_whileCond5___VAR_b]
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond4 @ b
                                        ; assign clause 1 result to [:root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21] = b 
                                        [:root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21] = REGA
                                        ; (5)      ENTER root_function_main_whileCond5_whileCond4 @ 1 <undefined position>
                                                   REGA = 1
                                        ; (5)      EXIT  root_function_main_whileCond5_whileCond4 @ 1
                                        ; concatenate clause 2 to [:root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21] <= - 1
                                        REGC = [:root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21]
                                        [:root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21] = REGC - REGA
                                        ; assigning result back to REGA
                                        REGA = [:root_function_main_whileCond5_whileCond4___VAR_compoundBlkExpr4_21]
                              ; (4)     EXIT  root_function_main_whileCond5_whileCond4 @ b - (1)
                              [:root_function_main_whileCond5___VAR_b] = REGA
                     ; (3)    EXIT  root_function_main_whileCond5_whileCond4 @ LetVarEqExpr(b,b - (1))
                     PCHITMP = <:root_function_main_whileCond5_whileCond4___LABEL_CHECK
                     PC = >:root_function_main_whileCond5_whileCond4___LABEL_CHECK
                     root_function_main_whileCond5_whileCond4___LABEL_AFTER:
             ; (2)   EXIT  root_function_main_whileCond5_whileCond4 @ WhileCond(_GT,Condition(b,>,0),List(PutcharVar(UP), LetVarEqExpr(b,b - (1))))
             ; (2)   ENTER root_function_main_whileCond5 @ DefUint8EqExpr(a,33 + (loop)) 38.9
                     ; (3)    ENTER root_function_main_whileCond5 @ 33 + (loop) <undefined position>
                              ; (4)     ENTER root_function_main_whileCond5 @ 33 <undefined position>
                                        REGA = 33
                              ; (4)     EXIT  root_function_main_whileCond5 @ 33
                              ; assign clause 1 result to [:root_function_main_whileCond5___VAR_compoundBlkExpr3_22] = 33 
                              [:root_function_main_whileCond5___VAR_compoundBlkExpr3_22] = REGA
                              ; (4)     ENTER root_function_main_whileCond5 @ loop <undefined position>
                                        REGA = [:root_function_main___VAR_loop]
                              ; (4)     EXIT  root_function_main_whileCond5 @ loop
                              ; concatenate clause 2 to [:root_function_main_whileCond5___VAR_compoundBlkExpr3_22] <= + loop
                              REGC = [:root_function_main_whileCond5___VAR_compoundBlkExpr3_22]
                              [:root_function_main_whileCond5___VAR_compoundBlkExpr3_22] = REGC + REGA
                              ; assigning result back to REGA
                              REGA = [:root_function_main_whileCond5___VAR_compoundBlkExpr3_22]
                     ; (3)    EXIT  root_function_main_whileCond5 @ 33 + (loop)
                     [:root_function_main_whileCond5___VAR_a] = REGA
             ; (2)   EXIT  root_function_main_whileCond5 @ DefUint8EqExpr(a,33 + (loop))
             ; (2)   ENTER root_function_main_whileCond5_putcharVar_DRAW_ @ PutcharVar(DRAW) 39.9
                     root_function_main_whileCond5_putcharVar_DRAW____LABEL_wait_23:
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_DRAW____LABEL_transmit_24
                     PC = >:root_function_main_whileCond5_putcharVar_DRAW____LABEL_transmit_24 _DO
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_DRAW____LABEL_wait_23
                     PC = >:root_function_main_whileCond5_putcharVar_DRAW____LABEL_wait_23
                     root_function_main_whileCond5_putcharVar_DRAW____LABEL_transmit_24:
                     UART = [:root_function_main___VAR_DRAW]
             ; (2)   EXIT  root_function_main_whileCond5_putcharVar_DRAW_ @ PutcharVar(DRAW)
             ; (2)   ENTER root_function_main_whileCond5_putcharVar_a_ @ PutcharVar(a) 40.9
                     root_function_main_whileCond5_putcharVar_a____LABEL_wait_25:
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_a____LABEL_transmit_26
                     PC = >:root_function_main_whileCond5_putcharVar_a____LABEL_transmit_26 _DO
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_a____LABEL_wait_25
                     PC = >:root_function_main_whileCond5_putcharVar_a____LABEL_wait_25
                     root_function_main_whileCond5_putcharVar_a____LABEL_transmit_26:
                     UART = [:root_function_main_whileCond5___VAR_a]
             ; (2)   EXIT  root_function_main_whileCond5_putcharVar_a_ @ PutcharVar(a)
                     ;  offset next cycle
             ; (2)   ENTER root_function_main_whileCond5_putcharVar_RIGHT_ @ PutcharVar(RIGHT) 43.9
                     root_function_main_whileCond5_putcharVar_RIGHT____LABEL_wait_27:
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_RIGHT____LABEL_transmit_28
                     PC = >:root_function_main_whileCond5_putcharVar_RIGHT____LABEL_transmit_28 _DO
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_RIGHT____LABEL_wait_27
                     PC = >:root_function_main_whileCond5_putcharVar_RIGHT____LABEL_wait_27
                     root_function_main_whileCond5_putcharVar_RIGHT____LABEL_transmit_28:
                     UART = [:root_function_main___VAR_RIGHT]
             ; (2)   EXIT  root_function_main_whileCond5_putcharVar_RIGHT_ @ PutcharVar(RIGHT)
             ; (2)   ENTER root_function_main_whileCond5_putcharVar_DOWN_ @ PutcharVar(DOWN) 44.9
                     root_function_main_whileCond5_putcharVar_DOWN____LABEL_wait_29:
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_DOWN____LABEL_transmit_30
                     PC = >:root_function_main_whileCond5_putcharVar_DOWN____LABEL_transmit_30 _DO
                     PCHITMP = <:root_function_main_whileCond5_putcharVar_DOWN____LABEL_wait_29
                     PC = >:root_function_main_whileCond5_putcharVar_DOWN____LABEL_wait_29
                     root_function_main_whileCond5_putcharVar_DOWN____LABEL_transmit_30:
                     UART = [:root_function_main___VAR_DOWN]
             ; (2)   EXIT  root_function_main_whileCond5_putcharVar_DOWN_ @ PutcharVar(DOWN)
             ; (2)   ENTER root_function_main_whileCond5 @ LetVarEqExpr(loop,loop + (1)) 46.9
                     ; (3)    ENTER root_function_main_whileCond5 @ loop + (1) <undefined position>
                              ; (4)     ENTER root_function_main_whileCond5 @ loop <undefined position>
                                        REGA = [:root_function_main___VAR_loop]
                              ; (4)     EXIT  root_function_main_whileCond5 @ loop
                              ; assign clause 1 result to [:root_function_main_whileCond5___VAR_compoundBlkExpr3_31] = loop 
                              [:root_function_main_whileCond5___VAR_compoundBlkExpr3_31] = REGA
                              ; (4)     ENTER root_function_main_whileCond5 @ 1 <undefined position>
                                        REGA = 1
                              ; (4)     EXIT  root_function_main_whileCond5 @ 1
                              ; concatenate clause 2 to [:root_function_main_whileCond5___VAR_compoundBlkExpr3_31] <= + 1
                              REGC = [:root_function_main_whileCond5___VAR_compoundBlkExpr3_31]
                              [:root_function_main_whileCond5___VAR_compoundBlkExpr3_31] = REGC + REGA
                              ; assigning result back to REGA
                              REGA = [:root_function_main_whileCond5___VAR_compoundBlkExpr3_31]
                     ; (3)    EXIT  root_function_main_whileCond5 @ loop + (1)
                     [:root_function_main___VAR_loop] = REGA
             ; (2)   EXIT  root_function_main_whileCond5 @ LetVarEqExpr(loop,loop + (1))
             PCHITMP = <:root_function_main_whileCond5___LABEL_CHECK
             PC = >:root_function_main_whileCond5___LABEL_CHECK
             root_function_main_whileCond5___LABEL_AFTER:
      ; (1)  EXIT  root_function_main_whileCond5 @ WhileCond(_Z,Condition(loop,<=,26),List(DefUint8EqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(RIGHT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(DOWN), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(LEFT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(UP), LetVarEqExpr(b,b - (1)))), DefUint8EqExpr(a,33 + (loop)), PutcharVar(DRAW), PutcharVar(a), Comment(// offset next cycle), PutcharVar(RIGHT), PutcharVar(DOWN), LetVarEqExpr(loop,loop + (1))))
      PCHITMP = <:root_end
      PC = >:root_end
; (0) EXIT  root_function_main @ DefFunction(main,List(),List(DefUint8EqExpr(loop,0), DefUint8EqExpr(NONE,0), DefUint8EqExpr(UP,2), DefUint8EqExpr(DOWN,3), DefUint8EqExpr(RIGHT,5), DefUint8EqExpr(LEFT,4), DefUint8EqExpr(DRAW,10), PutcharVar(NONE), PutcharVar(NONE), WhileCond(_Z,Condition(loop,<=,26),List(DefUint8EqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(RIGHT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(DOWN), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(LEFT), LetVarEqExpr(b,b - (1)))), LetVarEqExpr(b,10), WhileCond(_GT,Condition(b,>,0),List(PutcharVar(UP), LetVarEqExpr(b,b - (1)))), DefUint8EqExpr(a,33 + (loop)), PutcharVar(DRAW), PutcharVar(a), Comment(// offset next cycle), PutcharVar(RIGHT), PutcharVar(DOWN), LetVarEqExpr(loop,loop + (1))))))
root_end:
END
