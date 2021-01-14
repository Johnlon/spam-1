root_function_main_a: EQU 0
; (0) ENTER root_function_main @ function
      ; (1)  ENTER root_function_main @ statementVar
             [:root_function_main_a] = 10
      ; (1)  EXIT  root_function_main @ statementVar
      ; (1)  ENTER root_function_main_whileCond1 @ whileCond
             root_function_main_whileCond1__2__check:
             ; (2)   ENTER root_function_main_whileCond1 @ condition
                     REGA = [:root_function_main_a]
                     REGA = REGA PASS_A 0 _S
             ; (2)   EXIT  root_function_main_whileCond1 @ condition
             PCHITMP = <:root_function_main_whileCond1__2__top
             PC = >:root_function_main_whileCond1__2__top _GT
             PCHITMP = <:root_function_main_whileCond1__2__bot
             PC = >:root_function_main_whileCond1__2__bot
             root_function_main_whileCond1__2__top:
             ; (2)   ENTER root_function_main_whileCond1 @ statementEqVarOpConst
                     REGA = [:root_function_main_a]
                     REGA = REGA - 1
                     [:root_function_main_a] = REGA
             ; (2)   EXIT  root_function_main_whileCond1 @ statementEqVarOpConst
             ; (2)   ENTER root_function_main_whileCond1_putcharN @ statementPutcharName
                     root_function_main_whileCond1_putcharN__wait_3:
                     PCHITMP = <:root_function_main_whileCond1_putcharN__transmit_4
                     PC = >:root_function_main_whileCond1_putcharN__transmit_4 _DO
                     PCHITMP = <:root_function_main_whileCond1_putcharN__wait_3
                     PC = <:root_function_main_whileCond1_putcharN__wait_3
                     root_function_main_whileCond1_putcharN__transmit_4:
                     UART = [:root_function_main_a]
             ; (2)   EXIT  root_function_main_whileCond1_putcharN @ statementPutcharName
             PCHITMP = <:root_function_main_whileCond1__2__check
             PC = >:root_function_main_whileCond1__2__check
             root_function_main_whileCond1__2__bot:
      ; (1)  EXIT  root_function_main_whileCond1 @ whileCond
      PCHITMP = <:root_end
      PC = >:root_end
; (0) EXIT  root_function_main @ function
root_end:
      PCHITMP = <$BEAF
      PC = >$BEAF

END

