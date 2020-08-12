:start

ld a=a(0)a
ld b=b(0)b
ld c=c(0)c
ld d=d(0)d
ld MARHI=$00

:do

# if no data then skip straight to rx
ld pchitmp=hi(:ifRXready)
# set Z flag if B=0
ld b=b(-)d
ld jmpz=lo(:ifRXready)

# else fall thru to attempting transmit of existing char

:ifTXready

ld pchitmp=hi(:tx)
ld jmpdo=lo(:tx)

ld pchitmp=hi(:do)
ld pc=lo(:do)

:ifRXready

ld pchitmp=hi(:rx)
ld jmpdi=lo(:rx)

ld pchitmp=hi(:do)
ld pc=lo(:do)

:tx

#ld uart=0(r)a
ld uart=0(r)b
ld b=b(0)a

ld pchitmp=hi(:do)
ld pc=lo(:do)

:rx

ld b=uart

ld pchitmp=hi(:end)
ld c=c(L+1)c
ld jmpc=lo(:end)

ld pchitmp=hi(:do)
ld pc=lo(:do)


:end
