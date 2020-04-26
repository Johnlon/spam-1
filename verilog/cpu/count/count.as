:start

ld a=a(0)a
ld a=$0
ld b=$1
ld [$03]=a
ld a=[$01]
:do
ld uart=0(r)a
ld a=a(+)b

ld pchitmp=$ff
ld jmpdi=$ff

ld pchitmp=hi(:do)
ld pc=lo(:do)

:end
