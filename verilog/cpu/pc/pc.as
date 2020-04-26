ld a=a(0)a
ld b=b(0)a

:loop
ld PCHITMP=hi(:read)
ld JMPDI=lo(:read)

ld PCHITMP=hi(:write)
ld JMPDO=lo(:write)

ld PCHITMP=hi(:end)
ld PC=lo(:end)

:read
ld a=uart
ld b=b(L+1)b

ld PCHITMP=hi(:loop)
ld PC=lo(:loop)

:write
ld uart=0(R)b

ld PCHITMP=hi(:loop)
ld PC=lo(:loop)

:end
ld PCHITMP=hi(:loop)
ld PC=lo(:loop)


#ld   RAM=$ff
#ld   MARLO=$ff
#ld   MARHI=$ff
#ld   PCHITMP=$ff
#ld   PCLO=$ff
#ld   PC=$ff
#ld   JMPO=$ff
#ld   JMPZ=$ff
#ld   JMPC=$ff
#ld   JMPDO=$ff
#ld   JMPEQ=$ff
#ld   JMPNE=$ff
#ld   JMPGT=$ff
#ld   JMPLT=$ff
