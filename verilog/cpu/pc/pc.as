ld a=a(0)a
ld b=b(0)a

:loop
ld PCHITMP=hi(:write)
ld JMPDO=lo(:write)

ld PCHITMP=hi(:loop)
ld PC=lo(:loop)

:write
ld b=b(R+1)b
ld uart=0(R)b

ld PCHITMP=hi(:loop)
ld PC=lo(:loop)
:end

