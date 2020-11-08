
grammar SPAM1;

@header{
    import java.util.*;
}

@members {
    List<Long> bytes=new ArrayList<Long>();
    List lines=new ArrayList();
    int addr=0;

    interface Instruction {};
}



//compile: 'A' {System.out.println("START");} ;
compile: line (EOL EOL* line?)* EOL*;

line: (label WS* comment?| equ WS* comment?| comment | label? WS* (instruction) WS* comment?);

instruction: (instruction_both | instruction_passright | instruction_passleft);

instruction_both: target WS* '=' WS* adev WS+ op WS+ bdev;
instruction_passleft: target WS* '=' WS* passa;
instruction_passright: target WS* '=' WS* passb;
equ: label WS* 'EQU' WS+ immed16;

comment : COMMENT;

adev: REGA | REGB | REGC | REGD | MARLO | MARHI | UART | NU;
bdev: REGA | REGB | REGC | REGD | MARLO | MARHI | immed8 | RAM | ram_direct | NU;
target: REGA | REGB | REGC | REGD | MARLO | MARHI | UART | RAM | ram_direct | PC | PCLO | PCHITMP | NU;

immed8: v=(IMMED_DEC3 | IMMED_HEX2 |IMMED_LABEL)
 { System.out.println("IMMED " + $v.text); };

immed16: (immed8 | IMMED_DEC5 | IMMED_HEX4 |IMMED_LABEL);

ram_direct:  RAM_ADDR_DEC | RAM_ADDR_HEX| RAM_ADDR_LABEL;

ALUOP: 'AND' | 'OR' | 'TIMES' | 'PLUS' | 'PLUSC'; // more
ALUOPS: ALUOP FLAG;

op:  ALUOP | ALUOPS;

FLAG: '\'S';

passa:adev FLAG?;
passb: (immed8 | RAM | ram_direct) FLAG?;

label : LABEL;

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

LABEL: NAME ':';
LABEL_REF: ':' NAME;

NAME: [a-zA-Z][0-9a-zA-Z_]*;

DEC: ('0'..'9');
HEX: [0-9a-fA-F];

HEX2: HEX HEX?;
HEX4: HEX HEX? HEX? HEX?;
DEC3 : DEC DEC? DEC? {
       int i = Integer.parseInt(getText());
       if (i > 255) throw new RuntimeException(getText() + " is greater than " + 255);
};

DEC5 : DEC DEC? DEC? DEC? DEC? {
       int i = Integer.parseInt(getText());
       if (i > 65535) throw new RuntimeException(getText() + " is greater than " + 65535);
};
IMMED_LABEL: '#' LABEL_REF;
IMMED_DEC3:  '#' DEC3 ;
IMMED_HEX2:  '#$' HEX2  ;
IMMED_DEC5:  '#' DEC5 ;
IMMED_HEX4:  '#$' HEX4  ;

RAM_ADDR_DEC:    '[' IMMED_DEC5 ']';
RAM_ADDR_HEX:    '[' IMMED_HEX4 ']';
RAM_ADDR_LABEL:  '[' IMMED_LABEL ']';


WS
    :   (' ' | '\t')// -> channel(HIDDEN)
    ;

EOL
   : [\r\n] +
   ;


COMMENT
   : ';' ~ [\r\n]*// -> skip
   ;

STRING
   : '"' ~ ["]* '"'
   ;



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

