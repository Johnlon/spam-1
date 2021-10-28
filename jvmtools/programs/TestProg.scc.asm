; IsVar8But : RETURN_HI : root_function_main___VAR_RETURN_HI
root_function_main___VAR_RETURN_HI: EQU   0
root_function_main___VAR_RETURN_HI: BYTES [0]
; IsVar8But : RETURN_LO : root_function_main___VAR_RETURN_LO
root_function_main___VAR_RETURN_LO: EQU   1
root_function_main___VAR_RETURN_LO: BYTES [0]
; IsVar16 : aa : root_function_main___VAR_aa
root_function_main___VAR_aa: EQU   2
root_function_main___VAR_aa: BYTES [0, 0]
; IsVar16 : bb : root_function_main___VAR_bb
root_function_main___VAR_bb: EQU   4
root_function_main___VAR_bb: BYTES [0, 0]
; IsVar16 : compoundBlkExpr3_9 : root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9
root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9: EQU   6
root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9: BYTES [0, 0]
; IsVar16 : compoundBlkExpr2_13 : root_function_main_Compound4___VAR_compoundBlkExpr2_13
root_function_main_Compound4___VAR_compoundBlkExpr2_13: EQU   8
root_function_main_Compound4___VAR_compoundBlkExpr2_13: BYTES [0, 0]
; IsVar16 : cc : root_function_main___VAR_cc
root_function_main___VAR_cc: EQU   10
root_function_main___VAR_cc: BYTES [0, 0]
; IsVar16 : compoundBlkExpr2_14 : root_function_main_Compound5___VAR_compoundBlkExpr2_14
root_function_main_Compound5___VAR_compoundBlkExpr2_14: EQU   12
root_function_main_Compound5___VAR_compoundBlkExpr2_14: BYTES [0, 0]
; IsVar16 : dd : root_function_main___VAR_dd
root_function_main___VAR_dd: EQU   14
root_function_main___VAR_dd: BYTES [0, 0]
; IsVar16 : compoundBlkExpr3_18 : root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18
root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18: EQU   16
root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18: BYTES [0, 0]
; IsVar16 : ee : root_function_main___VAR_ee
root_function_main___VAR_ee: EQU   18
root_function_main___VAR_ee: BYTES [0, 0]
; IsVar16 : compoundBlkExpr2_22 : root_function_main_Compound8___VAR_compoundBlkExpr2_22
root_function_main_Compound8___VAR_compoundBlkExpr2_22: EQU   20
root_function_main_Compound8___VAR_compoundBlkExpr2_22: BYTES [0, 0]
; IsVar16 : ff : root_function_main___VAR_ff
root_function_main___VAR_ff: EQU   22
root_function_main___VAR_ff: BYTES [0, 0]
PCHITMP = < :ROOT________main_start
PC = > :ROOT________main_start
; (0)  ENTER root_function_main @ DefFunction(main,List(),List(Halt(0,0), DefUint16EqExpr(aa,162), HaltVar(aa,1), DefUint16EqExpr(bb,180), HaltVar(bb,2), DefUint16EqExpr(cc,aa << (8) + (bb)), HaltVar(cc,3), DefUint16EqExpr(dd,cc >> (8)), HaltVar(dd,4), DefUint16EqExpr(ee,aa << (8)), HaltVar(ee,5), DefUint16EqExpr(ff,ee + (bb)), HaltVar(ff,6))) 4.5
       ROOT________main_start:
       root_function_main___LABEL_START:
       ; (1)  ENTER root_function_main_halt_0 @ Halt(0,0) 6.5
              ; Halt: MAR = 0 ; code = 0
              MARHI = 0
              MARLO = 0
              HALT = 0
       ; (1)  EXIT  root_function_main_halt_0 @ Halt(0,0)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(aa,162) 8.5
              ; (2)  ENTER root_function_main_Compound1 @ 162 <undefined position>
                     ; (3)  ENTER root_function_main_Compound1 @ 162 <undefined position>
                            REGA = > 162
                            REGD = < 162
                     ; (3)  EXIT  root_function_main_Compound1 @ 162
              ; (2)  EXIT  root_function_main_Compound1 @ 162
              [:root_function_main___VAR_aa] = REGA
              [:root_function_main___VAR_aa+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(aa,162)
       ; (1)  ENTER root_function_main_haltVar_aa_ @ HaltVar(aa,1) 9.5
              ; Halt : MAR = root_function_main___VAR_aa ; code = 1
              MARHI = [:root_function_main___VAR_aa + 1]
              MARLO = [:root_function_main___VAR_aa]
              HALT = 1
       ; (1)  EXIT  root_function_main_haltVar_aa_ @ HaltVar(aa,1)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(bb,180) 11.5
              ; (2)  ENTER root_function_main_Compound2 @ 180 <undefined position>
                     ; (3)  ENTER root_function_main_Compound2 @ 180 <undefined position>
                            REGA = > 180
                            REGD = < 180
                     ; (3)  EXIT  root_function_main_Compound2 @ 180
              ; (2)  EXIT  root_function_main_Compound2 @ 180
              [:root_function_main___VAR_bb] = REGA
              [:root_function_main___VAR_bb+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(bb,180)
       ; (1)  ENTER root_function_main_haltVar_bb_ @ HaltVar(bb,2) 12.5
              ; Halt : MAR = root_function_main___VAR_bb ; code = 2
              MARHI = [:root_function_main___VAR_bb + 1]
              MARLO = [:root_function_main___VAR_bb]
              HALT = 2
       ; (1)  EXIT  root_function_main_haltVar_bb_ @ HaltVar(bb,2)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(cc,aa << (8) + (bb)) 14.5
              ; (2)  ENTER root_function_main_Compound4 @ aa << (8) + (bb) <undefined position>
                     ; (3)  ENTER root_function_main_Compound4_Compound3 @ aa << (8) <undefined position>
                            ; (4)  ENTER root_function_main_Compound4_Compound3 @ aa <undefined position>
                                   REGA = [:root_function_main___VAR_aa]
                                   REGD = [:root_function_main___VAR_aa + 1]
                            ; (4)  EXIT  root_function_main_Compound4_Compound3 @ aa
                            ; backup the clause 0 result to ram : [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9] = aa 
                            [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9] = REGA
                            [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9+1] = REGD
                            ; (4)  ENTER root_function_main_Compound4_Compound3_CHAIN0_0 @ 8 <undefined position>
                                   REGA = > 8
                                   REGD = < 8
                            ; (4)  EXIT  root_function_main_Compound4_Compound3_CHAIN0_0 @ 8
                            ; apply clause 1 to variable at ram [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9] <= << 8
                            root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_shiftLoop_10:
                                PORTA = 1
                            ; === is loop done?
                              ; if REGD != 0 then do a shift
                            NOOP = REGD A_MINUS_B 0 _S
                            PCHITMP = < :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_doShift_11
                            PC = > :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_doShift_11 _NE
                              ; if REGA != 0 then do a shift
                            NOOP = REGA A_MINUS_B 0 _S
                            PC = > :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_doShift_11 _NE
                              ; else no more shifting so jump to end
                            PCHITMP = < :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_endShiftLoop_12
                            PC = > :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_endShiftLoop_12
                            root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_doShift_11:
                            ; count down loop
                            REGA = REGA A_MINUS_B 1 _S
                            REGD = REGD A_MINUS_B_MINUS_C 0
                            ; do one shift of low byte to left
                              ; REGB = 1 if top bit of low byte is 1
                            REGC = [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9]
                            REGB = REGC >> 7
                            [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9] = REGC A_LSL_B 1
                            ; LSL hi byte and or in the carry
                            REGC = [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9+1]
                            REGC = REGC A_LSL_B 1
                            [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9+1] = REGC  | REGB
                            ; loop again
                            PCHITMP = < :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_shiftLoop_10
                            PC = > :root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_shiftLoop_10
                            root_function_main_Compound4_Compound3_CHAIN0_0___LABEL_endShiftLoop_12:
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9]
                            REGD = [:root_function_main_Compound4_Compound3___VAR_compoundBlkExpr3_9+1]
                     ; (3)  EXIT  root_function_main_Compound4_Compound3 @ aa << (8)
                     ; backup the clause 0 result to ram : [:root_function_main_Compound4___VAR_compoundBlkExpr2_13] = aa << (8) 
                     [:root_function_main_Compound4___VAR_compoundBlkExpr2_13] = REGA
                     [:root_function_main_Compound4___VAR_compoundBlkExpr2_13+1] = REGD
                     ; (3)  ENTER root_function_main_Compound4_CHAIN0_0 @ bb <undefined position>
                            REGA = [:root_function_main___VAR_bb]
                            REGD = [:root_function_main___VAR_bb + 1]
                     ; (3)  EXIT  root_function_main_Compound4_CHAIN0_0 @ bb
                     ; apply clause 1 to variable at ram [:root_function_main_Compound4___VAR_compoundBlkExpr2_13] <= + bb
                     REGC = [:root_function_main_Compound4___VAR_compoundBlkExpr2_13]
                     [:root_function_main_Compound4___VAR_compoundBlkExpr2_13] = REGC A_PLUS_B REGA _S
                     REGC = [:root_function_main_Compound4___VAR_compoundBlkExpr2_13 + 1]
                     [:root_function_main_Compound4___VAR_compoundBlkExpr2_13+1] = REGC A_PLUS_B_PLUS_C REGD
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_Compound4___VAR_compoundBlkExpr2_13]
                     REGD = [:root_function_main_Compound4___VAR_compoundBlkExpr2_13+1]
              ; (2)  EXIT  root_function_main_Compound4 @ aa << (8) + (bb)
              [:root_function_main___VAR_cc] = REGA
              [:root_function_main___VAR_cc+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(cc,aa << (8) + (bb))
       ; (1)  ENTER root_function_main_haltVar_cc_ @ HaltVar(cc,3) 15.5
              ; Halt : MAR = root_function_main___VAR_cc ; code = 3
              MARHI = [:root_function_main___VAR_cc + 1]
              MARLO = [:root_function_main___VAR_cc]
              HALT = 3
       ; (1)  EXIT  root_function_main_haltVar_cc_ @ HaltVar(cc,3)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(dd,cc >> (8)) 17.5
              ; (2)  ENTER root_function_main_Compound5 @ cc >> (8) <undefined position>
                     ; (3)  ENTER root_function_main_Compound5 @ cc <undefined position>
                            REGA = [:root_function_main___VAR_cc]
                            REGD = [:root_function_main___VAR_cc + 1]
                     ; (3)  EXIT  root_function_main_Compound5 @ cc
                     ; backup the clause 0 result to ram : [:root_function_main_Compound5___VAR_compoundBlkExpr2_14] = cc 
                     [:root_function_main_Compound5___VAR_compoundBlkExpr2_14] = REGA
                     [:root_function_main_Compound5___VAR_compoundBlkExpr2_14+1] = REGD
                     ; (3)  ENTER root_function_main_Compound5_CHAIN0_0 @ 8 <undefined position>
                            REGA = > 8
                            REGD = < 8
                     ; (3)  EXIT  root_function_main_Compound5_CHAIN0_0 @ 8
                     ; apply clause 1 to variable at ram [:root_function_main_Compound5___VAR_compoundBlkExpr2_14] <= >> 8
                     root_function_main_Compound5_CHAIN0_0___LABEL_shiftLoop_15:
                     ; is loop done?
                     PCHITMP = < :root_function_main_Compound5_CHAIN0_0___LABEL_doShift_16
                     NOOP = REGD A_MINUS_B 0 _S
                     PC = > :root_function_main_Compound5_CHAIN0_0___LABEL_doShift_16 _NE
                     NOOP = REGA A_MINUS_B 0 _S
                     PC = > :root_function_main_Compound5_CHAIN0_0___LABEL_doShift_16 _NE
                     PCHITMP = < :root_function_main_Compound5_CHAIN0_0___LABEL_endShiftLoop_17
                     PC = > :root_function_main_Compound5_CHAIN0_0___LABEL_endShiftLoop_17
                     root_function_main_Compound5_CHAIN0_0___LABEL_doShift_16:
                     ; count down loop
                     REGA = REGA A_MINUS_B 1 _S
                     REGD = REGD A_MINUS_B_MINUS_C 0
                     ; do one shift
                     REGC = [:root_function_main_Compound5___VAR_compoundBlkExpr2_14 + 1]
                     REGB = REGC << 7
                     [:root_function_main_Compound5___VAR_compoundBlkExpr2_14 + 1] = REGC A_LSR_B 1 _S
                     ; LSR load lo byte and or in the carry
                     REGC = [:root_function_main_Compound5___VAR_compoundBlkExpr2_14]
                     REGC = REGC A_LSR_B 1
                     [:root_function_main_Compound5___VAR_compoundBlkExpr2_14] = REGC  | REGB
                     ; loop again
                     PCHITMP = < :root_function_main_Compound5_CHAIN0_0___LABEL_shiftLoop_15
                     PC = > :root_function_main_Compound5_CHAIN0_0___LABEL_shiftLoop_15
                     root_function_main_Compound5_CHAIN0_0___LABEL_endShiftLoop_17:
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_Compound5___VAR_compoundBlkExpr2_14]
                     REGD = [:root_function_main_Compound5___VAR_compoundBlkExpr2_14+1]
              ; (2)  EXIT  root_function_main_Compound5 @ cc >> (8)
              [:root_function_main___VAR_dd] = REGA
              [:root_function_main___VAR_dd+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(dd,cc >> (8))
       ; (1)  ENTER root_function_main_haltVar_dd_ @ HaltVar(dd,4) 18.5
              ; Halt : MAR = root_function_main___VAR_dd ; code = 4
              MARHI = [:root_function_main___VAR_dd + 1]
              MARLO = [:root_function_main___VAR_dd]
              HALT = 4
       ; (1)  EXIT  root_function_main_haltVar_dd_ @ HaltVar(dd,4)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(ee,aa << (8)) 20.5
              ; (2)  ENTER root_function_main_Compound7 @ aa << (8) <undefined position>
                     ; (3)  ENTER root_function_main_Compound7_Compound6 @ aa << (8) <undefined position>
                            ; (4)  ENTER root_function_main_Compound7_Compound6 @ aa <undefined position>
                                   REGA = [:root_function_main___VAR_aa]
                                   REGD = [:root_function_main___VAR_aa + 1]
                            ; (4)  EXIT  root_function_main_Compound7_Compound6 @ aa
                            ; backup the clause 0 result to ram : [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18] = aa 
                            [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18] = REGA
                            [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18+1] = REGD
                            ; (4)  ENTER root_function_main_Compound7_Compound6_CHAIN0_0 @ 8 <undefined position>
                                   REGA = > 8
                                   REGD = < 8
                            ; (4)  EXIT  root_function_main_Compound7_Compound6_CHAIN0_0 @ 8
                            ; apply clause 1 to variable at ram [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18] <= << 8
                            root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_shiftLoop_19:
                                PORTA = 1
                            ; === is loop done?
                              ; if REGD != 0 then do a shift
                            NOOP = REGD A_MINUS_B 0 _S
                            PCHITMP = < :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_doShift_20
                            PC = > :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_doShift_20 _NE
                              ; if REGA != 0 then do a shift
                            NOOP = REGA A_MINUS_B 0 _S
                            PC = > :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_doShift_20 _NE
                              ; else no more shifting so jump to end
                            PCHITMP = < :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_endShiftLoop_21
                            PC = > :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_endShiftLoop_21
                            root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_doShift_20:
                            ; count down loop
                            REGA = REGA A_MINUS_B 1 _S
                            REGD = REGD A_MINUS_B_MINUS_C 0
                            ; do one shift of low byte to left
                              ; REGB = 1 if top bit of low byte is 1
                            REGC = [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18]
                            REGB = REGC >> 7
                            [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18] = REGC A_LSL_B 1
                            ; LSL hi byte and or in the carry
                            REGC = [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18+1]
                            REGC = REGC A_LSL_B 1
                            [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18+1] = REGC  | REGB
                            ; loop again
                            PCHITMP = < :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_shiftLoop_19
                            PC = > :root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_shiftLoop_19
                            root_function_main_Compound7_Compound6_CHAIN0_0___LABEL_endShiftLoop_21:
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18]
                            REGD = [:root_function_main_Compound7_Compound6___VAR_compoundBlkExpr3_18+1]
                     ; (3)  EXIT  root_function_main_Compound7_Compound6 @ aa << (8)
              ; (2)  EXIT  root_function_main_Compound7 @ aa << (8)
              [:root_function_main___VAR_ee] = REGA
              [:root_function_main___VAR_ee+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(ee,aa << (8))
       ; (1)  ENTER root_function_main_haltVar_ee_ @ HaltVar(ee,5) 21.5
              ; Halt : MAR = root_function_main___VAR_ee ; code = 5
              MARHI = [:root_function_main___VAR_ee + 1]
              MARLO = [:root_function_main___VAR_ee]
              HALT = 5
       ; (1)  EXIT  root_function_main_haltVar_ee_ @ HaltVar(ee,5)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(ff,ee + (bb)) 23.5
              ; (2)  ENTER root_function_main_Compound8 @ ee + (bb) <undefined position>
                     ; (3)  ENTER root_function_main_Compound8 @ ee <undefined position>
                            REGA = [:root_function_main___VAR_ee]
                            REGD = [:root_function_main___VAR_ee + 1]
                     ; (3)  EXIT  root_function_main_Compound8 @ ee
                     ; backup the clause 0 result to ram : [:root_function_main_Compound8___VAR_compoundBlkExpr2_22] = ee 
                     [:root_function_main_Compound8___VAR_compoundBlkExpr2_22] = REGA
                     [:root_function_main_Compound8___VAR_compoundBlkExpr2_22+1] = REGD
                     ; (3)  ENTER root_function_main_Compound8_CHAIN0_0 @ bb <undefined position>
                            REGA = [:root_function_main___VAR_bb]
                            REGD = [:root_function_main___VAR_bb + 1]
                     ; (3)  EXIT  root_function_main_Compound8_CHAIN0_0 @ bb
                     ; apply clause 1 to variable at ram [:root_function_main_Compound8___VAR_compoundBlkExpr2_22] <= + bb
                     REGC = [:root_function_main_Compound8___VAR_compoundBlkExpr2_22]
                     [:root_function_main_Compound8___VAR_compoundBlkExpr2_22] = REGC A_PLUS_B REGA _S
                     REGC = [:root_function_main_Compound8___VAR_compoundBlkExpr2_22 + 1]
                     [:root_function_main_Compound8___VAR_compoundBlkExpr2_22+1] = REGC A_PLUS_B_PLUS_C REGD
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_Compound8___VAR_compoundBlkExpr2_22]
                     REGD = [:root_function_main_Compound8___VAR_compoundBlkExpr2_22+1]
              ; (2)  EXIT  root_function_main_Compound8 @ ee + (bb)
              [:root_function_main___VAR_ff] = REGA
              [:root_function_main___VAR_ff+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(ff,ee + (bb))
       ; (1)  ENTER root_function_main_haltVar_ff_ @ HaltVar(ff,6) 24.5
              ; Halt : MAR = root_function_main___VAR_ff ; code = 6
              MARHI = [:root_function_main___VAR_ff + 1]
              MARLO = [:root_function_main___VAR_ff]
              HALT = 6
       ; (1)  EXIT  root_function_main_haltVar_ff_ @ HaltVar(ff,6)
       PCHITMP = <:root_end
       PC = >:root_end
; (0)  EXIT  root_function_main @ DefFunction(main,List(),List(Halt(0,0), DefUint16EqExpr(aa,162), HaltVar(aa,1), DefUint16EqExpr(bb,180), HaltVar(bb,2), DefUint16EqExpr(cc,aa << (8) + (bb)), HaltVar(cc,3), DefUint16EqExpr(dd,cc >> (8)), HaltVar(dd,4), DefUint16EqExpr(ee,aa << (8)), HaltVar(ee,5), DefUint16EqExpr(ff,ee + (bb)), HaltVar(ff,6)))
root_end:
END
