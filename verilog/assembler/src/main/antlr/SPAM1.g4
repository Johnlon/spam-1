grammar SPAM1;

prog: line line* EOF;

line: statement EOL | EOL;

statement:
      label                                       # LabelInstruction
    | NAME 'EQU' expr                            # EquInstruction
    | label? target  '=' adev ALUOP FLAG? bdev    # AssignABInstruction
    | label? target  '=' adev FLAG?               # AssignAInstruction
    | label? target  '=' bdevOnly FLAG?           # AssignBInstruction
    ;

adev: REGA | REGB | REGC | REGD | MARLO | MARHI | UART | NU;

bdev:
   bdevDevices      #BDevDevice
 | ramDirect        #BDevRAMDirect
 | expr             #BDevExpr
 ;

bdevOnly:
   RAM              #BDevOnlyRAMRegister
 | ramDirect        #BDevOnlyRAMDirect
 | expr             #BDevOnlyExpr
 ;

bdevDevices: REGA | REGB | REGC | REGD | MARLO | MARHI  | RAM | NU;

target:
   (REGA | REGB | REGC | REGD | MARLO | MARHI | UART | RAM | PC | PCLO | PCHITMP | NU)  #TargetDevice
 | ramDirect                                                                            #TargetRamDirect
 ;

//  only immediate compile time values here
expr:
   number            #Num
 | '**'              #PC
 | NAME        #Name
 | LABEL_REF         #Var
 | expr '+' expr     #Plus
 | '(' expr ')'      #Parens
 | '<' expr          #LoByte
 | '>' expr          #HiHyte
;


ALUOP: 'AND' | 'OR' | 'TIMES' | 'PLUS' | 'PLUSC'; // more
FLAG: '\'S';


PCHITMP: P C H I T M P;
PCLO: P C L O;
PC: P C;
RAM: R A M;
REGA: R E G A;
REGB: R E G B;
REGC: R E G C;
REGD: R E G D;
MARLO: M A R L O;
MARHI: M A R H I;
UART: U A R T;
NU: N U;

NAME: [a-zA-Z][0-9a-zA-Z_]*;

label : NAME ':';
LABEL_REF: ':' NAME;

ramDirect:    '[' expr ']';

number: HEX
      | OCT
      | BIN
      | INT
      ;

STRING: '"' ~["]* '"' ;
INT: DIGIT+ ;
HEX: '$' [0-9a-fA-F]+ ;
OCT: '@' [0-7]+ ;
BIN: '%' [01]+ ;
CHAR: '\'' . ;

EOL
   : ('\r'? '\n') +
   ;

WS: [ \t\r\n]+ -> skip ;



COMMENT
   : ';' ~ [\r\n]* ->  channel(HIDDEN) //skip
   ;

fragment LETTER: [a-zA-Z] ;
fragment DIGIT: [0-9] ;

fragment A
   : ('a' | 'A')
   ;

fragment B
   : ('b' | 'B')
   ;



fragment C
   : ('c' | 'C')
   ;


fragment D
   : ('d' | 'D')
   ;


fragment E
   : ('e' | 'E')
   ;


fragment F
   : ('f' | 'F')
   ;


fragment G
   : ('g' | 'G')
   ;


fragment H
   : ('h' | 'H')
   ;


fragment I
   : ('i' | 'I')
   ;


fragment J
   : ('j' | 'J')
   ;


fragment K
   : ('k' | 'K')
   ;


fragment L
   : ('l' | 'L')
   ;


fragment M
   : ('m' | 'M')
   ;


fragment N
   : ('n' | 'N')
   ;


fragment O
   : ('o' | 'O')
   ;


fragment P
   : ('p' | 'P')
   ;


fragment Q
   : ('q' | 'Q')
   ;


fragment R
   : ('r' | 'R')
   ;


fragment S
   : ('s' | 'S')
   ;


fragment T
   : ('t' | 'T')
   ;


fragment U
   : ('u' | 'U')
   ;


fragment V
   : ('v' | 'V')
   ;


fragment W
   : ('w' | 'W')
   ;


fragment X
   : ('x' | 'X')
   ;


fragment Y
   : ('y' | 'Y')
   ;


fragment Z
   : ('z' | 'Z')
   ;

