
:begin
#2
ld a=$22
#CHECKOP 0
ld a=[]
#CHECKOP 1
ld a=[$00d]
#CHECKOP 2
ld a=UART
#CHECKOP 3
ld marlo=0(+)B
#CHECKOP 4
ld A=A(+)B
#CHECKOP 5
ld [$22]=A
#CHECKOP 6
ld [$22]=UART
#CHECKOP 7





ld marhi=[$00d]
ld marhi=[$22]
ld marhi=[$255d]
ld marlo=$11
#7
ld marhi=$22
ld a=$22d
ld a=$10000001b
:label
ld marhi=uart
ld marhi=[]
ld marhi=[MAR]
ld [$00d]=UART
# This one requires ALU and PASS_A
ld [$00]=A
ld [$00d]=A

jmpz lo(:label)
#20
ld pchitmp=lo(:label)


# synthesize  "jmpz :label" as "ld pchitmp, hi(:label)   &   jmpz lo(:label)"

#24
# Should fail but don't
ld A=B
# Translate to "ALU X = PASS_A X X"

ld B=A
# Translate to "ALU X = PASS_B X Y"

ld A=(-)B
# Translate to "ALU X = MINUS_B X B"

ld A=A(-)B
ld A=A(-R)B
ld A=A(<<)B
# Translate to "ALU X = A_LSHIFT_B X Y"

ld A=B(-)A
# !!! Not illegal because alu directly supports this

#ld A=B(*HI)A
# !!! ILLEGAL


ld MARLO=0(-)A
# Translate to "ALU MAR = MINUS_B ? Y" FORCE_X_TO_0

ld [$00]=A 
# Translate to "ALU [$00] = PASS_A X ? " FORCE_PASSX

# Do Fail
#ld [$00]=MARLO 

#mar=$2211  shortform to set hi and lo
#ram=$33              using MAR then write $33 into RAM
#ram[MAR]=$33         same as above
#ram[$11]=$33         Set MARHI=0 and MARLO=$11 then write $33 into RAM at $0011 - no way to write immediate using ZP
#ram[$2211]=$33       Set MAR then write $33 into RAM

