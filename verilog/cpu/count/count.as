ld a=$0
ld b=$1
ld uart=0(+)a
ld a=a(+)b
ld uart=(+)p

