;;VARIABLE IsVar8 : RETURN_HI
root_function_main___VAR_RETURN_HI: EQU   0
root_function_main___VAR_RETURN_HI: BYTES [0]
;;VARIABLE IsVar8 : RETURN_LO
root_function_main___VAR_RETURN_LO: EQU   1
root_function_main___VAR_RETURN_LO: BYTES [0]
;;VARIABLE IsVar16 : aa
root_function_main_block9___VAR_aa: EQU   2
root_function_main_block9___VAR_aa: BYTES [0, 0]
;;VARIABLE IsVar16 : bb
root_function_main_block9___VAR_bb: EQU   4
root_function_main_block9___VAR_bb: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr4_10
root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10: EQU   6
root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr3_13
root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13: EQU   8
root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13: BYTES [0, 0]
;;VARIABLE IsVar16 : cc
root_function_main_block9___VAR_cc: EQU   10
root_function_main_block9___VAR_cc: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr3_14
root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14: EQU   12
root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14: BYTES [0, 0]
;;VARIABLE IsVar16 : dd
root_function_main_block9___VAR_dd: EQU   14
root_function_main_block9___VAR_dd: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr4_17
root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17: EQU   16
root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17: BYTES [0, 0]
;;VARIABLE IsVar16 : ee
root_function_main_block9___VAR_ee: EQU   18
root_function_main_block9___VAR_ee: BYTES [0, 0]
;;VARIABLE IsVar16 : compoundBlkExpr3_20
root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20: EQU   20
root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20: BYTES [0, 0]
;;VARIABLE IsVar16 : ff
root_function_main_block9___VAR_ff: EQU   22
root_function_main_block9___VAR_ff: BYTES [0, 0]
PCHITMP = < :ROOT________main_start
PC = > :ROOT________main_start
; (0)  ENTER root_function_main @ function_main [4.5]
       ;; DEBUG (4) fun main() {     --------------------------------------------------------
       ROOT________main_start:
       root_function_main___LABEL_START:
       ; (1)  ENTER root_function_main_block9 @ block9 [4.16]
              ;; DEBUG (4) fun main() {     --------------------------------------------------------
              ; (2)  ENTER root_function_main_block9_halt_0 @ halt_0 [6.5]
                     ;; DEBUG (6) halt(0, 0)     --------------------------------------------------------
                     ; Halt: MAR = 0 ; code = 0
                     MARHI = 0
                     MARLO = 0
                     HALT = 0
              ; (2)  EXIT  root_function_main_block9_halt_0 @ halt_0
              ; (2)  ENTER root_function_main_block9 @ uint16 aa = block [8.5]
                     ;; DEBUG (8) uint16 aa = $a2;     --------------------------------------------------------
                     ; (3)  ENTER root_function_main_block9_CompoundAluExpr1 @ 162 [8.17]
                            ;; DEBUG (8) uint16 aa = $a2;     --------------------------------------------------------
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr1 @ 162 [8.17]
                                   ;; DEBUG (8) uint16 aa = $a2;     --------------------------------------------------------
                                   REGA = > 162
                                   REGD = < 162
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr1 @ 162
                     ; (3)  EXIT  root_function_main_block9_CompoundAluExpr1 @ 162
                     [:root_function_main_block9___VAR_aa] = REGA
                     [:root_function_main_block9___VAR_aa+1] = REGD
              ; (2)  EXIT  root_function_main_block9 @ uint16 aa = block
              ; (2)  ENTER root_function_main_block9_haltVar_aa_ @ haltVar_aa_ [9.5]
                     ;; DEBUG (9) halt(aa, 1)     --------------------------------------------------------
                     ; Halt : MAR = root_function_main_block9___VAR_aa ; code = 1
                     MARHI = [:root_function_main_block9___VAR_aa + 1]
                     MARLO = [:root_function_main_block9___VAR_aa]
                     HALT = 1
              ; (2)  EXIT  root_function_main_block9_haltVar_aa_ @ haltVar_aa_
              ; (2)  ENTER root_function_main_block9 @ uint16 bb = block [11.5]
                     ;; DEBUG (11) uint16 bb = $b4;     --------------------------------------------------------
                     ; (3)  ENTER root_function_main_block9_CompoundAluExpr2 @ 180 [11.17]
                            ;; DEBUG (11) uint16 bb = $b4;     --------------------------------------------------------
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr2 @ 180 [11.17]
                                   ;; DEBUG (11) uint16 bb = $b4;     --------------------------------------------------------
                                   REGA = > 180
                                   REGD = < 180
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr2 @ 180
                     ; (3)  EXIT  root_function_main_block9_CompoundAluExpr2 @ 180
                     [:root_function_main_block9___VAR_bb] = REGA
                     [:root_function_main_block9___VAR_bb+1] = REGD
              ; (2)  EXIT  root_function_main_block9 @ uint16 bb = block
              ; (2)  ENTER root_function_main_block9_haltVar_bb_ @ haltVar_bb_ [12.5]
                     ;; DEBUG (12) halt(bb, 2)     --------------------------------------------------------
                     ; Halt : MAR = root_function_main_block9___VAR_bb ; code = 2
                     MARHI = [:root_function_main_block9___VAR_bb + 1]
                     MARLO = [:root_function_main_block9___VAR_bb]
                     HALT = 2
              ; (2)  EXIT  root_function_main_block9_haltVar_bb_ @ haltVar_bb_
              ; (2)  ENTER root_function_main_block9 @ uint16 cc = block [14.5]
                     ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
                     ; (3)  ENTER root_function_main_block9_CompoundAluExpr4 @ aa << (8) + (bb) [14.17]
                            ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3 @ aa << (8) [14.18]
                                   ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
                                   ; (5)  ENTER root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3 @ aa [14.18]
                                          ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
                                          REGA = [:root_function_main_block9___VAR_aa]
                                          REGD = [:root_function_main_block9___VAR_aa + 1]
                                   ; (5)  EXIT  root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3 @ aa
                                   ; backup the clause 0 result to ram : [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10] = aa 
                                   [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10] = REGA
                                   [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10+1] = REGD
                                   ; (5)  ENTER root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3_CHAIN0_0 @ 8 [14.24]
                                          ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
                                          REGA = > 8
                                          REGD = < 8
                                   ; (5)  EXIT  root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3_CHAIN0_0 @ 8
                                   ; apply clause 1 to variable at ram [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10] <= << 8
                                   REGC = [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10]
                                   [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10] = 0
                                   [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10 + 1] = REGC
                                   ; assigning lo result back to REGA and hi to REGD
                                   REGA = [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10]
                                   REGD = [:root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3___VAR_compoundBlkExpr4_10+1]
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr4_CompoundAluExpr3 @ aa << (8)
                            ; backup the clause 0 result to ram : [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13] = aa << (8) 
                            [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13] = REGA
                            [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13+1] = REGD
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr4_CHAIN0_0 @ bb [14.29]
                                   ;; DEBUG (14) uint16 cc = (aa << 8) + bb;     --------------------------------------------------------
                                   REGA = [:root_function_main_block9___VAR_bb]
                                   REGD = [:root_function_main_block9___VAR_bb + 1]
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr4_CHAIN0_0 @ bb
                            ; apply clause 1 to variable at ram [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13] <= + bb
                            REGC = [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13]
                            [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13] = REGC A_PLUS_B REGA _S
                            REGC = [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13 + 1]
                            [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13+1] = REGC A_PLUS_B_PLUS_C REGD
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13]
                            REGD = [:root_function_main_block9_CompoundAluExpr4___VAR_compoundBlkExpr3_13+1]
                     ; (3)  EXIT  root_function_main_block9_CompoundAluExpr4 @ aa << (8) + (bb)
                     [:root_function_main_block9___VAR_cc] = REGA
                     [:root_function_main_block9___VAR_cc+1] = REGD
              ; (2)  EXIT  root_function_main_block9 @ uint16 cc = block
              ; (2)  ENTER root_function_main_block9_haltVar_cc_ @ haltVar_cc_ [15.5]
                     ;; DEBUG (15) halt(cc, 3)     --------------------------------------------------------
                     ; Halt : MAR = root_function_main_block9___VAR_cc ; code = 3
                     MARHI = [:root_function_main_block9___VAR_cc + 1]
                     MARLO = [:root_function_main_block9___VAR_cc]
                     HALT = 3
              ; (2)  EXIT  root_function_main_block9_haltVar_cc_ @ haltVar_cc_
              ; (2)  ENTER root_function_main_block9 @ uint16 dd = block [17.5]
                     ;; DEBUG (17) uint16 dd = cc >> 8;     --------------------------------------------------------
                     ; (3)  ENTER root_function_main_block9_CompoundAluExpr5 @ cc >> (8) [17.17]
                            ;; DEBUG (17) uint16 dd = cc >> 8;     --------------------------------------------------------
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr5 @ cc [17.17]
                                   ;; DEBUG (17) uint16 dd = cc >> 8;     --------------------------------------------------------
                                   REGA = [:root_function_main_block9___VAR_cc]
                                   REGD = [:root_function_main_block9___VAR_cc + 1]
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr5 @ cc
                            ; backup the clause 0 result to ram : [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14] = cc 
                            [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14] = REGA
                            [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14+1] = REGD
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr5_CHAIN0_0 @ 8 [17.23]
                                   ;; DEBUG (17) uint16 dd = cc >> 8;     --------------------------------------------------------
                                   REGA = > 8
                                   REGD = < 8
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr5_CHAIN0_0 @ 8
                            ; apply clause 1 to variable at ram [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14] <= >> 8
                            REGC = [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14 + 1]
                            [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14 + 1] = 0
                            [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14] = REGC
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14]
                            REGD = [:root_function_main_block9_CompoundAluExpr5___VAR_compoundBlkExpr3_14+1]
                     ; (3)  EXIT  root_function_main_block9_CompoundAluExpr5 @ cc >> (8)
                     [:root_function_main_block9___VAR_dd] = REGA
                     [:root_function_main_block9___VAR_dd+1] = REGD
              ; (2)  EXIT  root_function_main_block9 @ uint16 dd = block
              ; (2)  ENTER root_function_main_block9_haltVar_dd_ @ haltVar_dd_ [18.5]
                     ;; DEBUG (18) halt(dd, 4)     --------------------------------------------------------
                     ; Halt : MAR = root_function_main_block9___VAR_dd ; code = 4
                     MARHI = [:root_function_main_block9___VAR_dd + 1]
                     MARLO = [:root_function_main_block9___VAR_dd]
                     HALT = 4
              ; (2)  EXIT  root_function_main_block9_haltVar_dd_ @ haltVar_dd_
              ; (2)  ENTER root_function_main_block9 @ uint16 ee = block [20.5]
                     ;; DEBUG (20) uint16 ee = (aa << 8);     --------------------------------------------------------
                     ; (3)  ENTER root_function_main_block9_CompoundAluExpr7 @ aa << (8) [20.17]
                            ;; DEBUG (20) uint16 ee = (aa << 8);     --------------------------------------------------------
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6 @ aa << (8) [20.18]
                                   ;; DEBUG (20) uint16 ee = (aa << 8);     --------------------------------------------------------
                                   ; (5)  ENTER root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6 @ aa [20.18]
                                          ;; DEBUG (20) uint16 ee = (aa << 8);     --------------------------------------------------------
                                          REGA = [:root_function_main_block9___VAR_aa]
                                          REGD = [:root_function_main_block9___VAR_aa + 1]
                                   ; (5)  EXIT  root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6 @ aa
                                   ; backup the clause 0 result to ram : [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17] = aa 
                                   [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17] = REGA
                                   [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17+1] = REGD
                                   ; (5)  ENTER root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6_CHAIN0_0 @ 8 [20.24]
                                          ;; DEBUG (20) uint16 ee = (aa << 8);     --------------------------------------------------------
                                          REGA = > 8
                                          REGD = < 8
                                   ; (5)  EXIT  root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6_CHAIN0_0 @ 8
                                   ; apply clause 1 to variable at ram [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17] <= << 8
                                   REGC = [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17]
                                   [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17] = 0
                                   [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17 + 1] = REGC
                                   ; assigning lo result back to REGA and hi to REGD
                                   REGA = [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17]
                                   REGD = [:root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6___VAR_compoundBlkExpr4_17+1]
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr7_CompoundAluExpr6 @ aa << (8)
                     ; (3)  EXIT  root_function_main_block9_CompoundAluExpr7 @ aa << (8)
                     [:root_function_main_block9___VAR_ee] = REGA
                     [:root_function_main_block9___VAR_ee+1] = REGD
              ; (2)  EXIT  root_function_main_block9 @ uint16 ee = block
              ; (2)  ENTER root_function_main_block9_haltVar_ee_ @ haltVar_ee_ [21.5]
                     ;; DEBUG (21) halt(ee, 5)     --------------------------------------------------------
                     ; Halt : MAR = root_function_main_block9___VAR_ee ; code = 5
                     MARHI = [:root_function_main_block9___VAR_ee + 1]
                     MARLO = [:root_function_main_block9___VAR_ee]
                     HALT = 5
              ; (2)  EXIT  root_function_main_block9_haltVar_ee_ @ haltVar_ee_
              ; (2)  ENTER root_function_main_block9 @ uint16 ff = block [23.5]
                     ;; DEBUG (23) uint16 ff = ee + bb;     --------------------------------------------------------
                     ; (3)  ENTER root_function_main_block9_CompoundAluExpr8 @ ee + (bb) [23.17]
                            ;; DEBUG (23) uint16 ff = ee + bb;     --------------------------------------------------------
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr8 @ ee [23.17]
                                   ;; DEBUG (23) uint16 ff = ee + bb;     --------------------------------------------------------
                                   REGA = [:root_function_main_block9___VAR_ee]
                                   REGD = [:root_function_main_block9___VAR_ee + 1]
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr8 @ ee
                            ; backup the clause 0 result to ram : [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20] = ee 
                            [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20] = REGA
                            [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20+1] = REGD
                            ; (4)  ENTER root_function_main_block9_CompoundAluExpr8_CHAIN0_0 @ bb [23.22]
                                   ;; DEBUG (23) uint16 ff = ee + bb;     --------------------------------------------------------
                                   REGA = [:root_function_main_block9___VAR_bb]
                                   REGD = [:root_function_main_block9___VAR_bb + 1]
                            ; (4)  EXIT  root_function_main_block9_CompoundAluExpr8_CHAIN0_0 @ bb
                            ; apply clause 1 to variable at ram [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20] <= + bb
                            REGC = [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20]
                            [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20] = REGC A_PLUS_B REGA _S
                            REGC = [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20 + 1]
                            [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20+1] = REGC A_PLUS_B_PLUS_C REGD
                            ; assigning lo result back to REGA and hi to REGD
                            REGA = [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20]
                            REGD = [:root_function_main_block9_CompoundAluExpr8___VAR_compoundBlkExpr3_20+1]
                     ; (3)  EXIT  root_function_main_block9_CompoundAluExpr8 @ ee + (bb)
                     [:root_function_main_block9___VAR_ff] = REGA
                     [:root_function_main_block9___VAR_ff+1] = REGD
              ; (2)  EXIT  root_function_main_block9 @ uint16 ff = block
              ; (2)  ENTER root_function_main_block9_haltVar_ff_ @ haltVar_ff_ [24.5]
                     ;; DEBUG (24) halt(ff, 6)     --------------------------------------------------------
                     ; Halt : MAR = root_function_main_block9___VAR_ff ; code = 6
                     MARHI = [:root_function_main_block9___VAR_ff + 1]
                     MARLO = [:root_function_main_block9___VAR_ff]
                     HALT = 6
              ; (2)  EXIT  root_function_main_block9_haltVar_ff_ @ haltVar_ff_
       ; (1)  EXIT  root_function_main_block9 @ block9
       PCHITMP = <:root_end
       PC = >:root_end
; (0)  EXIT  root_function_main @ function_main
root_end:
MARHI=255
MARLO=255
HALT=255
END
