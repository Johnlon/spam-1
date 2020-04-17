:start

ld a=a(0)a
ld a=$0
ld b=$1
:do
ld uart=0(r)a
ld a=a(+)b
ld pchitmp= hi(:do)
ld pc=lo(:do)


