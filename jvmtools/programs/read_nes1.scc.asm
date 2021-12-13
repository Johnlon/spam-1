; IsVar8But : RETURN_HI : root_function_main___VAR_RETURN_HI
root_function_main___VAR_RETURN_HI: EQU   0
root_function_main___VAR_RETURN_HI: BYTES [0]
; IsVar8But : RETURN_LO : root_function_main___VAR_RETURN_LO
root_function_main___VAR_RETURN_LO: EQU   1
root_function_main___VAR_RETURN_LO: BYTES [0]
; IsVar16 : a : root_function_main___VAR_a
root_function_main___VAR_a: EQU   2
root_function_main___VAR_a: BYTES [0, 0]
PCHITMP = < :ROOT________main_start
PC = > :ROOT________main_start
; (0)  ENTER root_function_main @ DefFunction(main,List(),List(DefUint16EqExpr(a,readport(Gamepad1)), HaltVar(a,0))) 3.5
       ROOT________main_start:
       root_function_main___LABEL_START:
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(a,readport(Gamepad1)) 4.9
              ; (2)  ENTER root_function_main_CompoundAluExpr1 @ readport(Gamepad1) <undefined position>
                     ; (3)  ENTER root_function_main_CompoundAluExpr1 @ readport(Gamepad1) 4.20
                            PORTSEL = :PORT_RD_Gamepad1
                            REGA = PORT
                            REGD = 0
                     ; (3)  EXIT  root_function_main_CompoundAluExpr1 @ readport(Gamepad1)
              ; (2)  EXIT  root_function_main_CompoundAluExpr1 @ readport(Gamepad1)
              [:root_function_main___VAR_a] = REGA
              [:root_function_main___VAR_a+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(a,readport(Gamepad1))
       ; (1)  ENTER root_function_main_haltVar_a_ @ HaltVar(a,0) 5.9
              ; Halt : MAR = root_function_main___VAR_a ; code = 0
              MARHI = [:root_function_main___VAR_a + 1]
              MARLO = [:root_function_main___VAR_a]
              HALT = 0
       ; (1)  EXIT  root_function_main_haltVar_a_ @ HaltVar(a,0)
       PCHITMP = <:root_end
       PC = >:root_end
; (0)  EXIT  root_function_main @ DefFunction(main,List(),List(DefUint16EqExpr(a,readport(Gamepad1)), HaltVar(a,0)))
root_end:
MARHI=255
MARLO=255
HALT=255
END
