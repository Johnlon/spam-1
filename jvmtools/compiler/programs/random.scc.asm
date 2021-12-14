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
; (0)  ENTER root_function_main @ DefFunction(main,List(),List(DefUint16EqExpr(a,Random()), HaltVar(a))) 2.5
       ROOT________main_start:
       root_function_main___LABEL_START:
       ; (1)  ENTER root_function_main @ DefUint16EqExpr(a,Random()) 3.9
              ; (2)  ENTER root_function_main_Compound1 @ Random() <undefined position>
                     ; (3)  ENTER root_function_main_Compound1_random_ @ Random() 3.20
                            REGA = RAND
                            REGD = 0
                     ; (3)  EXIT  root_function_main_Compound1_random_ @ Random()
              ; (2)  EXIT  root_function_main_Compound1 @ Random()
              [:root_function_main___VAR_a] = REGA
              [:root_function_main___VAR_a+1] = REGD
       ; (1)  EXIT  root_function_main @ DefUint16EqExpr(a,Random())
       ; (1)  ENTER root_function_main_haltVar_a_ @ HaltVar(a) 4.9
              MARHI = [:root_function_main___VAR_a + 1]
              MARLO = [:root_function_main___VAR_a]
              HALT = 2
       ; (1)  EXIT  root_function_main_haltVar_a_ @ HaltVar(a)
       PCHITMP = <:root_end
       PC = >:root_end
; (0)  EXIT  root_function_main @ DefFunction(main,List(),List(DefUint16EqExpr(a,Random()), HaltVar(a)))
root_end:
END
