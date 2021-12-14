root_main_a: EQU 0
root_main_b: EQU 2
root_main_varExprs_d2: EQU 1
root_main_varExprs_d3: EQU 3
; (0) ENTER function main
; (1) ENTER statementVarOp a = Block(Parser (Parser ()^^), Code())
; (2) ENTER varExprs Block(Parser (Parser ()^^), Code()) ~ List()
; (3) ENTER varNExpr 0
REGA = 0
; (3) EXIT  varNExpr 0
[:root_main_varExprs_d2] = REGA
REGA = [:root_main_varExprs_d2]
; (2) EXIT  varExprs Block(Parser (Parser ()^^), Code()) ~ List()
[:root_main_a] = REGA
; (1) EXIT   statementVarOp a = Block(Parser (Parser ()^^), Code())
; (1) ENTER statementVarOp a = Block(Parser (Parser ()^^), Code())
; (2) ENTER varExprs Block(Parser (Parser ()^^), Code()) ~ List((+~Block(Parser (Parser ()^^), Code())))
; (3) ENTER varExprName a
REGA = [:root_main_a]
; (3) EXIT  varExprName a
[:root_main_varExprs_d2] = REGA
; (3) ENTER varNExpr 1
REGA = 1
; (3) EXIT  varNExpr 1
REGB = [:root_main_varExprs_d2]
[:root_main_varExprs_d2] = REGB + REGA
REGA = [:root_main_varExprs_d2]
; (2) EXIT  varExprs Block(Parser (Parser ()^^), Code()) ~ List((+~Block(Parser (Parser ()^^), Code())))
[:root_main_a] = REGA
; (1) EXIT   statementVarOp a = Block(Parser (Parser ()^^), Code())
; (1) ENTER statementVarOp b = Block(Parser (Parser ()^^), Code())
; (2) ENTER varExprs Block(Parser (Parser ()^^), Code()) ~ List((+~Block(Parser (Parser ()^^), Code())))
; (3) ENTER varNExpr 4
REGA = 4
; (3) EXIT  varNExpr 4
[:root_main_varExprs_d2] = REGA
; (3) ENTER varExprs Block(Parser (Parser ()^^), Code()) ~ List((+~Block(Parser (Parser ()^^), Code())))
; (4) ENTER varExprName a
REGA = [:root_main_a]
; (4) EXIT  varExprName a
[:root_main_varExprs_d3] = REGA
; (4) ENTER varNExpr 5
REGA = 5
; (4) EXIT  varNExpr 5
REGB = [:root_main_varExprs_d3]
[:root_main_varExprs_d3] = REGB + REGA
REGA = [:root_main_varExprs_d3]
; (3) EXIT  varExprs Block(Parser (Parser ()^^), Code()) ~ List((+~Block(Parser (Parser ()^^), Code())))
REGB = [:root_main_varExprs_d2]
[:root_main_varExprs_d2] = REGB + REGA
REGA = [:root_main_varExprs_d2]
; (2) EXIT  varExprs Block(Parser (Parser ()^^), Code()) ~ List((+~Block(Parser (Parser ()^^), Code())))
[:root_main_b] = REGA
; (1) EXIT   statementVarOp b = Block(Parser (Parser ()^^), Code())
; (1) ENTER  putchar b"
root_main_putchar_wait_1:
PCHITMP = <:root_main_putchar_transmit_2
PC = >:root_main_putchar_transmit_2 _DO
PCHITMP = <:root_main_putchar_wait_1
PC = <:root_main_putchar_wait_1
root_main_putchar_transmit_2:
UART = [:root_main_b]
; (1) EXIT   putchar b"
; (0) end MAIN
PCHITMP = <:root_end
PC = >:root_end
; (0) EXIT  function main
root_end:
END

