;;VARIABLE IsVar8 : RETURN_HI
root_function_main___VAR_RETURN_HI: EQU   0
root_function_main___VAR_RETURN_HI: BYTES [0]
;;VARIABLE IsVar8 : RETURN_LO
root_function_main___VAR_RETURN_LO: EQU   1
root_function_main___VAR_RETURN_LO: BYTES [0]
;;VARIABLE IsVar16 : aa
root_function_main___VAR_aa: EQU   2
root_function_main___VAR_aa: BYTES [0, 0]
;;VARIABLE IsVar16 : bb
root_function_main___VAR_bb: EQU   4
root_function_main___VAR_bb: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr3_9
root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9: EQU   6
root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr2_12
root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12: EQU   8
root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12: BYTES [0, 0]
;;VARIABLE IsVar16 : cc
root_function_main___VAR_cc: EQU   10
root_function_main___VAR_cc: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr2_13
root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13: EQU   12
root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13: BYTES [0, 0]
;;VARIABLE IsVar16 : dd
root_function_main___VAR_dd: EQU   14
root_function_main___VAR_dd: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr3_16
root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16: EQU   16
root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16: BYTES [0, 0]
;;VARIABLE IsVar16 : ee
root_function_main___VAR_ee: EQU   18
root_function_main___VAR_ee: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr2_19
root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19: EQU   20
root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19: BYTES [0, 0]
;;VARIABLE IsVar16 : ff
root_function_main___VAR_ff: EQU   22
root_function_main___VAR_ff: BYTES [0, 0]
PCHITMP = < :ROOT________main_start
PC = > :ROOT________main_start
; (0)  ENTER root_function_main @ DefFunction(main,List(),List(Halt(0,0), DefUint16EqExpr(aa,162), HaltVar(aa,1), DefUint16EqExpr(bb,180), HaltVar(bb,2), DefUint16EqExpr(cc,aa << (8) + (bb)), HaltVar(cc,3), DefUint16EqExpr(dd,cc >> (8)), HaltVar(dd,4), DefUint16EqExpr(ee,aa << (8)), HaltVar(ee,5), DefUint16EqExpr(ff,ee + (bb)), HaltVar(ff,6))) 4.5
       ;; DEBUG (4) fun main() {     --------------------------------------------------------
       ROOT________main_start:
       root_function_main___LABEL_START:
       ; (1)  ENTER root_function_main_halt_0 @ Halt(0,0) 6.5
              ;; DEBUG (6) halt(0, 0)     --------------------------------------------------------
              ; Halt: MAR = 0 ; code = 0
              MARHI = 0
              MARLO = 0
              HALT = 0
       ; (1)  EXIT  root_function_main_halt_0 @ Halt(0,0)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(aa,162) 8.5
              ;; DEBUG (8) uint16 aa = $a2;     --------------------------------------------------------
              ; (2)  ENTER root_function_main_CompoundAluExpr1 @ 162 <undefined position>
                     
                     ; (3)  ENTER root_function_main_CompoundAluExpr1 @ 162 <undefined position>
                            
                            REGA = > 162
                            REGD = < 162
                     ; (3)  EXIT  root_function_main_CompoundAluExpr1 @ 162
              ; (2)  EXIT  root_function_main_CompoundAluExpr1 @ 162
              [:root_function_main___VAR_aa] = REGA
              [:root_function_main___VAR_aa+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(aa,162)
       ; (1)  ENTER root_function_main_haltVar_aa_ @ HaltVar(aa,1) 9.5
              ;; DEBUG (9) halt(aa, 1)     --------------------------------------------------------
              ; Halt : MAR = root_function_main___VAR_aa ; code = 1
              MARHI = [:root_function_main___VAR_aa + 1]
              MARLO = [:root_function_main___VAR_aa]
              HALT = 1
       ; (1)  EXIT  root_function_main_haltVar_aa_ @ HaltVar(aa,1)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(bb,180) 11.5
              ;; DEBUG (11) uint16 bb = $b4;     --------------------------------------------------------
              ; (2)  ENTER root_function_main_CompoundAluExpr2 @ 180 <undefined position>
                     
                     ; (3)  ENTER root_function_main_CompoundAluExpr2 @ 180 <undefined position>
                            
                            REGA = > 180
                            REGD = < 180
                     ; (3)  EXIT  root_function_main_CompoundAluExpr2 @ 180
              ; (2)  EXIT  root_function_main_CompoundAluExpr2 @ 180
              [:root_function_main___VAR_bb] = REGA
              [:root_function_main___VAR_bb+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(bb,180)
       ; (1)  ENTER root_function_main_haltVar_bb_ @ HaltVar(bb,2) 12.5
              ;; DEBUG (12) halt(bb, 2)     --------------------------------------------------------
              ; Halt : MAR = root_function_main___VAR_bb ; code = 2
              MARHI = [:root_function_main___VAR_bb + 1]
              MARLO = [:root_function_main___VAR_bb]
              HALT = 2
       ; (1)  EXIT  root_function_main_haltVar_bb_ @ HaltVar(bb,2)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(cc,aa << (8) + (bb)) 14.5
              ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
              ; (2)  ENTER root_function_main_CompoundAluExpr4 @ aa << (8) + (bb) <undefined position>
                     
                     ; (3)  ENTER root_function_main_CompoundAluExpr4_CompoundAluExpr3 @ aa << (8) <undefined position>
                            
                            ; (4)  ENTER root_function_main_CompoundAluExpr4_CompoundAluExpr3 @ aa <undefined position>
                                   
                                   REGA = [:root_function_main___VAR_aa]
                                   REGD = [:root_function_main___VAR_aa + 1]
                            ; (4)  EXIT  root_function_main_CompoundAluExpr4_CompoundAluExpr3 @ aa
                            ; backup the clause 0 result to ram : [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9] = aa 
                            [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9] = REGA
                            [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9+1] = REGD
                            ; (4)  ENTER root_function_main_CompoundAluExpr4_CompoundAluExpr3_CHAIN0_0 @ 8 <undefined position>
                                   
                                   REGA = > 8
                                   REGD = < 8
                            ; (4)  EXIT  root_function_main_CompoundAluExpr4_CompoundAluExpr3_CHAIN0_0 @ 8
                            ; apply clause 1 to variable at ram [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9] <= << 8
                            REGC = [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9]
                            [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9] = 0
                            [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9 + 1] = REGC
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9]
                            REGD = [:root_function_main_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr3_9+1]
                     ; (3)  EXIT  root_function_main_CompoundAluExpr4_CompoundAluExpr3 @ aa << (8)
                     ; backup the clause 0 result to ram : [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12] = aa << (8) 
                     [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12] = REGA
                     [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12+1] = REGD
                     ; (3)  ENTER root_function_main_CompoundAluExpr4_CHAIN0_0 @ bb <undefined position>
                            
                            REGA = [:root_function_main___VAR_bb]
                            REGD = [:root_function_main___VAR_bb + 1]
                     ; (3)  EXIT  root_function_main_CompoundAluExpr4_CHAIN0_0 @ bb
                     ; apply clause 1 to variable at ram [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12] <= + bb
                     REGC = [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12]
                     [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12] = REGC A_PLUS_B REGA _S
                     REGC = [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12 + 1]
                     [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12+1] = REGC A_PLUS_B_PLUS_C REGD
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12]
                     REGD = [:root_function_main_CompoundAluExpr4___VAR_compoundBlkExpr2_12+1]
              ; (2)  EXIT  root_function_main_CompoundAluExpr4 @ aa << (8) + (bb)
              [:root_function_main___VAR_cc] = REGA
              [:root_function_main___VAR_cc+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(cc,aa << (8) + (bb))
       ; (1)  ENTER root_function_main_haltVar_cc_ @ HaltVar(cc,3) 15.5
              ;; DEBUG (15) halt(cc, 3)     --------------------------------------------------------
              ; Halt : MAR = root_function_main___VAR_cc ; code = 3
              MARHI = [:root_function_main___VAR_cc + 1]
              MARLO = [:root_function_main___VAR_cc]
              HALT = 3
       ; (1)  EXIT  root_function_main_haltVar_cc_ @ HaltVar(cc,3)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(dd,cc >> (8)) 17.5
              ;; DEBUG (17) uint16 dd = cc >> 8;     --------------------------------------------------------
              ; (2)  ENTER root_function_main_CompoundAluExpr5 @ cc >> (8) <undefined position>
                     
                     ; (3)  ENTER root_function_main_CompoundAluExpr5 @ cc <undefined position>
                            
                            REGA = [:root_function_main___VAR_cc]
                            REGD = [:root_function_main___VAR_cc + 1]
                     ; (3)  EXIT  root_function_main_CompoundAluExpr5 @ cc
                     ; backup the clause 0 result to ram : [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13] = cc 
                     [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13] = REGA
                     [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13+1] = REGD
                     ; (3)  ENTER root_function_main_CompoundAluExpr5_CHAIN0_0 @ 8 <undefined position>
                            
                            REGA = > 8
                            REGD = < 8
                     ; (3)  EXIT  root_function_main_CompoundAluExpr5_CHAIN0_0 @ 8
                     ; apply clause 1 to variable at ram [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13] <= >> 8
                     REGC = [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13 + 1]
                     [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13 + 1] = 0
                     [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13] = REGC
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13]
                     REGD = [:root_function_main_CompoundAluExpr5___VAR_compoundBlkExpr2_13+1]
              ; (2)  EXIT  root_function_main_CompoundAluExpr5 @ cc >> (8)
              [:root_function_main___VAR_dd] = REGA
              [:root_function_main___VAR_dd+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(dd,cc >> (8))
       ; (1)  ENTER root_function_main_haltVar_dd_ @ HaltVar(dd,4) 18.5
              ;; DEBUG (18) halt(dd, 4)     --------------------------------------------------------
              ; Halt : MAR = root_function_main___VAR_dd ; code = 4
              MARHI = [:root_function_main___VAR_dd + 1]
              MARLO = [:root_function_main___VAR_dd]
              HALT = 4
       ; (1)  EXIT  root_function_main_haltVar_dd_ @ HaltVar(dd,4)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(ee,aa << (8)) 20.5
              ;; DEBUG (20) uint16 ee = (aa << 8);     --------------------------------------------------------
              ; (2)  ENTER root_function_main_CompoundAluExpr7 @ aa << (8) <undefined position>
                     
                     ; (3)  ENTER root_function_main_CompoundAluExpr7_CompoundAluExpr6 @ aa << (8) <undefined position>
                            
                            ; (4)  ENTER root_function_main_CompoundAluExpr7_CompoundAluExpr6 @ aa <undefined position>
                                   
                                   REGA = [:root_function_main___VAR_aa]
                                   REGD = [:root_function_main___VAR_aa + 1]
                            ; (4)  EXIT  root_function_main_CompoundAluExpr7_CompoundAluExpr6 @ aa
                            ; backup the clause 0 result to ram : [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16] = aa 
                            [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16] = REGA
                            [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16+1] = REGD
                            ; (4)  ENTER root_function_main_CompoundAluExpr7_CompoundAluExpr6_CHAIN0_0 @ 8 <undefined position>
                                   
                                   REGA = > 8
                                   REGD = < 8
                            ; (4)  EXIT  root_function_main_CompoundAluExpr7_CompoundAluExpr6_CHAIN0_0 @ 8
                            ; apply clause 1 to variable at ram [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16] <= << 8
                            REGC = [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16]
                            [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16] = 0
                            [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16 + 1] = REGC
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16]
                            REGD = [:root_function_main_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr3_16+1]
                     ; (3)  EXIT  root_function_main_CompoundAluExpr7_CompoundAluExpr6 @ aa << (8)
              ; (2)  EXIT  root_function_main_CompoundAluExpr7 @ aa << (8)
              [:root_function_main___VAR_ee] = REGA
              [:root_function_main___VAR_ee+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(ee,aa << (8))
       ; (1)  ENTER root_function_main_haltVar_ee_ @ HaltVar(ee,5) 21.5
              ;; DEBUG (21) halt(ee, 5)     --------------------------------------------------------
              ; Halt : MAR = root_function_main___VAR_ee ; code = 5
              MARHI = [:root_function_main___VAR_ee + 1]
              MARLO = [:root_function_main___VAR_ee]
              HALT = 5
       ; (1)  EXIT  root_function_main_haltVar_ee_ @ HaltVar(ee,5)
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(ff,ee + (bb)) 23.5
              ;; DEBUG (23) uint16 ff = ee + bb;     --------------------------------------------------------
              ; (2)  ENTER root_function_main_CompoundAluExpr8 @ ee + (bb) <undefined position>
                     
                     ; (3)  ENTER root_function_main_CompoundAluExpr8 @ ee <undefined position>
                            
                            REGA = [:root_function_main___VAR_ee]
                            REGD = [:root_function_main___VAR_ee + 1]
                     ; (3)  EXIT  root_function_main_CompoundAluExpr8 @ ee
                     ; backup the clause 0 result to ram : [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19] = ee 
                     [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19] = REGA
                     [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19+1] = REGD
                     ; (3)  ENTER root_function_main_CompoundAluExpr8_CHAIN0_0 @ bb <undefined position>
                            
                            REGA = [:root_function_main___VAR_bb]
                            REGD = [:root_function_main___VAR_bb + 1]
                     ; (3)  EXIT  root_function_main_CompoundAluExpr8_CHAIN0_0 @ bb
                     ; apply clause 1 to variable at ram [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19] <= + bb
                     REGC = [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19]
                     [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19] = REGC A_PLUS_B REGA _S
                     REGC = [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19 + 1]
                     [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19+1] = REGC A_PLUS_B_PLUS_C REGD
                     ; assigning lo result back to REGA and hi to REGD
                     REGA = [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19]
                     REGD = [:root_function_main_CompoundAluExpr8___VAR_compoundBlkExpr2_19+1]
              ; (2)  EXIT  root_function_main_CompoundAluExpr8 @ ee + (bb)
              [:root_function_main___VAR_ff] = REGA
              [:root_function_main___VAR_ff+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(ff,ee + (bb))
       ; (1)  ENTER root_function_main_haltVar_ff_ @ HaltVar(ff,6) 24.5
              ;; DEBUG (24) halt(ff, 6)     --------------------------------------------------------
              ; Halt : MAR = root_function_main___VAR_ff ; code = 6
              MARHI = [:root_function_main___VAR_ff + 1]
              MARLO = [:root_function_main___VAR_ff]
              HALT = 6
       ; (1)  EXIT  root_function_main_haltVar_ff_ @ HaltVar(ff,6)
       PCHITMP = <:root_end
       PC = >:root_end
; (0)  EXIT  root_function_main @ DefFunction(main,List(),List(Halt(0,0), DefUint16EqExpr(aa,162), HaltVar(aa,1), DefUint16EqExpr(bb,180), HaltVar(bb,2), DefUint16EqExpr(cc,aa << (8) + (bb)), HaltVar(cc,3), DefUint16EqExpr(dd,cc >> (8)), HaltVar(dd,4), DefUint16EqExpr(ee,aa << (8)), HaltVar(ee,5), DefUint16EqExpr(ff,ee + (bb)), HaltVar(ff,6)))
root_end:
MARHI=255
MARLO=255
HALT=255
END
