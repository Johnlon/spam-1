:start

ld a=a(0)a
:loop
ld a=a(L+1)b

#
#ld a=$0
#ld b=$1
#:do
#ld a=a(+)b
#
ld pchitmp=hi(:loop)
ld pc=lo(:loop)
#
#:end
