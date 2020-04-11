#!/usr/bin/python3 
import re
import sys


lines = {}
labels = {}
assembled = {}

sp_reg = [
     "RAM",
     "MARLO",
     "MARHI",
     "UART",
     "PCHITMP",
     "PCLO",
     "PC",
     "JMPO",
     "JMPZ",
     "JMPC",
     "JMPDI",
     "JMPDO",
     "JMPEQ",
     "JMPNE",
     "JMPGT",
     "JMPLT"
];

gp_reg = []
for reg in range(16):
     r = chr(ord('A')+ reg)
     gp_reg.append(r)

device=[]
for d in sp_reg:
    device.append(d)
for d in gp_reg:
    device.append(d)

def rot(bits):
    low = bits > 7
    return ((bits << 1)& 0xf) + low

def deviceId(dev):
    return device.index(dev.upper())

def regId(dev):
    return gp_reg.index(dev.upper())

Z=True
# OP, NEEDL, NEEDR
alu_ops = [
    ["0",False,False],
    ["L","L",Z],
    ["R","R",Z],
    ["-L","-L",Z],
    ["-R",Z,Z],
    ["L+1",Z,Z],
    ["R+1",Z,Z],
    ["L-1",Z,Z],
    ["R-1",Z,Z],
    ["+",Z,Z],
    ["-",Z,Z],
    ["R-L",Z,Z],
    ["-!",Z,Z],
    ["*HI",Z,Z],
    ["*LO",Z,Z],
    ["/",Z,Z],
    ["%",Z,Z],
    ["<<",Z,Z],
    [">>>",Z,Z],
    [">>" ,Z,Z],
    ["ROL",Z,Z],
    ["ROR",Z,Z],
    ["AND",Z,Z],
    ["OR",Z,Z],
    ["XOR",Z,Z],
    ["NOT A",Z,Z],
    ["NOT B",Z,Z],
    ["+BCD",Z,Z],
    ["-BCD",Z,Z],

]

def checkDest(x):
     if not x.upper() in sp_reg:
          print("ERROR '{}' is not a device".format(x) )

def isSPReg(x):
     return x.upper() in sp_reg

def isGPReg(x):
     return x.upper() in gp_reg

def trySPReg(x):
     x = x.upper()
     if isSPReg(x):
          return ["SPREG", x]
     return None

def tryGPReg(x):
     x = x.upper()
     if isGPReg(x):
          return ["GPREG", x]
     return None

def isAluOp(x):
    return x in [ op for op, nl, nr in alu_ops ]

def aluOpId(x):
    aluopid = [ op for op, nl, nr in alu_ops ].index(x)
    if aluopid is None:
        error("{} is not an alu op".format(x))
    return aluopid


def tryDevice(x):
     r = tryGPReg(x)
     if r is not None:
        return r

     r = trySPReg(x)
     if r is not None:
        return r

     return None

def error(s):
     print("ERROR " + s)
     exit(1)

def tryUart(x):
     x=x.upper()
     if x == "UART":
          return ["SPREG","UART"]
     return None

def tryRam(x):
     x=x.upper()
     if x == "[]" or x == "[MAR]":
          return ["SPREG", "RAM[MAR]"]     
     return None

# is this []
def tryZP(x):
     x = x.upper()
     p = re.compile("^\[(.+)\]$")
     r = p.match(x)
     if r:
          k = tryConst(r.groups(1)[0])
          if k is not None:
               return ["RAM[ZP]", "RAM[ZP]", k]
     return None

def hi(l):
     return (l >> 8) & 0xff

def lo(l):
     return l & 0xff

def tryHiLoFn(x):
     x = x.upper()
     
     p = re.compile("^HI\((.+)\)$")
     r = p.match(x)
     if r:
          label = r.groups(1)[0]
          if label in labels:
               print(str(labels))
               addr = labels[label]
               return ["CONST", hi(addr)]
     
     p = re.compile("^LO\((.+)\)$")
     r = p.match(x)
     if r:
          label = r.groups(1)[0]
          if label in labels:
               addr = labels[label]
               return ["CONST", lo(addr)]
     
     return None

def tryAlu(x,dest):
     x = x.upper()

     #r = tryGPReg(x)
     #if r:
     #   return ["ALU", [0, "R", x]]
     
     p = re.compile("^(\w*)\s*\((.+)\)\s*(\w+)$")
     r = p.match(x)
     if r:
        l = r.groups(1)[0]
        op = r.groups(1)[1]
        r = r.groups(1)[2]

        if not isGPReg(r):
            error("right side of alu op must be a general purpose reg, but got '{}'".format(l))

        if not isAluOp(op):
            error("not an alu op, got '{}'".format(op))

        if isGPReg(l) and dest != l:
            # only permitted for R-L op
            if op != "-":
                error("left side of alu op must be same as destination '{}', but was '{}'".format(dest, l))
            op="R-L"
            oldr=r
            r=l
            l=oldr
    
        if isSPReg(l):
            l="force X to 0"

        return ["ALU",[l,op,r]]
     
     return None



def tryConst(x):
     x=x.upper()

     ph = re.compile("^\$([A-F0-9][A-F0-9]?)$")
     #ph = re.compile("^\$([A-F0-9][A-F0-9])$")
     r = ph.match(x)
     if r:
          return ["CONST", int(r.groups(1)[0],16)]
     
     pd = re.compile("^\$([0-9]{1,3})D$")
     r = pd.match(x)
     if r:
          dc = r.groups(1)[0]
          d=int(dc)
          if d>255:
               error("constant '{}' to big for 8 bits".format(dc))
          else:
               return ["CONST", d]
     
     pd = re.compile("^\$([01][01][01][01][01][01][01][01])B$")
     r = pd.match(x)
     if r:
          dc = r.groups(1)[0]
          d=int(dc,2)
          if d>255:
               error("constant '{}' to big for 8 bits".format(dc))
          else:
               return ["CONST", d]

     return None

def isGPRegType(s):
    return s == "GPREG"

def isSPRegType(s):
    return s == "SPREG"

def checkOp(codeline, line, op, lineno):
     ph = re.compile("^#CHECKOP (.*)$")
     r = ph.match(line)
     if r:
          expected=int(r.groups(1)[0],16)
          if expected != op:
            error("failed: wrong op, expected {}, got {} at {} for {}".format(expected, op, lineno, codeline))



verbose = False

def prt(s):
    if verbose:
        print(s)

if len(sys.argv) > 1:
    sourcecode = sys.argv[1]
else:
    sourcecode = "code.as"

with open(sourcecode, "r") as fp:
    line = fp.readline()
    address = 0
    lineno = 0
    while line:
        op=None
        lineno += 1
        line = line.strip()

        def printCode():
            print("{:5d} - addr {:4d}   : {}".format(lineno, address, line),  flush=True)
        def printPass():
            print("{:5d} -             : {}".format(lineno, line),  flush=True)

        if line.startswith("#"):
            printPass()
        elif line == "":
            printPass()
        elif line.startswith(":"):
            printCode()
            labels[line.upper()] = address
            lines[address] = [address, line, lineno]
        else:
            printCode()
            lines[address] = [address, line, lineno]

            [typ, rest] = re.split("\s+", line, 1)
            if typ.upper() != "LD":
                error("\tIllegal  =" + line)
           
            [left, right] = re.split("\s*=\s*", rest, 1)

            dest = tryDevice(left)
            if dest is not None:
                  prt("got dest sp reg {}".format(dest))
            else:
                 dest = tryZP(left)
                 if dest is not None:
                       prt("got dest zp {}".format(dest))
                 else:
                      dest=[None, "not recognised dest '{}'".format(left)]

            src = tryZP(right)
            if src is not None:
                  prt("got zp d{}".format(src))
            else:
                 src = tryConst(right)
                 if src is not None:
                       prt("got const d{}".format(src))
                 else:
                      src = tryRam(right)
                      if src is not None:
                            prt("got ram source {}".format(src))
                      else:
                           src = tryUart(right)
                           if src is not None:
                                 prt("got uart source {}".format(src))
                           else:
                               src = tryAlu(right, dest[1])
                               if src is not None:
                                    prt("got alu {} from label".format(src))
                               else:
                                   src = tryGPReg(right)
                                   if src is not None:
                                        prt("got gp reg source {}".format(src))
                                   else:
                                       src = tryHiLoFn(right)
                                       if src is not None:
                                             prt("got const d{} from label".format(src))
                                       else:
                                            src = [None, "not recognised source '{}'".format(right)]

            srcTyp=src[0]
            srcVal=src[1]

            destTyp=dest[0]
            destVal=dest[1]

            prt("\tDEST=" + str(dest))
            prt("\tSRC =" + str(src))

            if not srcTyp:
                 error("bad source " + str(src))

            if not destTyp:
                 error("bad dest " + str(dest))

            #print( typX, "is const" , typY == "CONST")
            ##print( typX, "is ram mar" , typY == "RAM[MAR]")
            #print( typX, "is ram zp" , typY == "RAM[ZP]")
            prt( "\t{}, {} <= {} {}".format(destTyp, destVal, srcTyp, srcVal))
            
            aluopid=0
            aluop=""

            h=""
            l=""

            if destTyp in ["GPREG", "SPREG" ]  and srcTyp == "CONST":
                op=0
                h="{0:03b}{1:05b}".format(op, rot(deviceId(destVal)))
                l="{:08b}".format(srcVal)
            elif destTyp in [ "GPREG", "SPREG" ] and srcVal == "RAM[MAR]":
                op=1
                h="{0:03b}{1:05b}".format(op, rot(deviceId(destVal)))
                l="{0:08b}".format(0)
            elif destTyp in [ "GPREG", "SPREG" ] and srcVal == "RAM[ZP]":
                op=2
                #print("SRCVAL:" + str(src))
                try:
                    sRamZpTyp, sRamZpVal, [ sConst, const] =src
                except ValueError as e:
                    error("Invalid RHS '{}' ", right)

                h="{0:03b}{1:05b}".format(op, rot(deviceId(destVal)))
                l="{0:08b}".format(const)
            elif destTyp in [ "GPREG", "SPREG" ] and srcVal == "UART":
                op=3
                h="{0:03b}{1:05b}".format(op, rot(deviceId(destVal)))
                l="{0:08b}".format(0)
            elif destTyp == "SPREG" and srcTyp == "ALU":
                op=4
                #print("SRCVAL:" + str(src))
                try:
                    sAlu, [ x, aluop, y] =src
                except ValueError as e:
                    error("Invalid RHS '{}' ", right)

                regY=regId(y)
                
                aluopid=aluOpId(aluop)

                aluop4=aluopid>>4
                aluop3210=aluopid & 0xf
                destReg=deviceId(destVal)
                if destReg > 15:
                    error("illegal - LHS {} must be a general purpose reg".format(destVal))

                h="{:03b}{:04b}{:01b}".format(op, destReg, aluop4)
                l="{:04b}{:04b}".format(aluop3210, regY)

            elif destTyp == "GPREG" and srcTyp == "ALU":
                op=5

                aluop=srcVal[1]
                regY=regId(srcVal[2])
                
                aluopid=aluOpId(aluop)

                aluop4=aluopid>>4
                aluop3210=aluopid & 0xf
                destReg=regId(destVal)
                if destReg > 15:
                    error("illegal - LHS {} must be a general purpose reg".format(destVal))

                h="{:03b}{:04b}{:01b}".format(op, destReg, aluop4)
                l="{:04b}{:04b}".format(aluop3210, regY)
            elif destTyp == "RAM[ZP]" and srcTyp == "GPREG":
                op=6
                try:
                    sGPReg, srcReg =src
                except ValueError as e:
                    error("Invalid RHS '{}' ", right)
                try:
                    sRamZpTyp, sRamZpVal, [ sConst, zpAddr] =dest
                except ValueError as e:
                    error("Invalid LHS '{}' ", left)

                h="{0:03b}{1:04b}0".format(op, regId(srcReg))
                l="{0:08b}".format(zpAddr)

            elif destVal == "RAM[ZP]" and srcVal == "UART":
                op=7
                zpAddr=dest[2][1]
                h="{0:03b}00000".format(op)
                l="{0:08b}".format(zpAddr)

            else:
                print("decoded   dest : {}".format(dest))
                print("decoded    src : {}".format(src))
                error("Not recognised : {}".format(line))
            
            if verbose:
                print("\tH:L = " + h + " " + l)
                print("\tALUOP  = " + str(aluop))
                print("\tOP  = " + str(op))

            address += 1
      
        nextline = fp.readline()

        checkOp(line, nextline, op, lineno)

        line = nextline

if True:
    print("==================")
    print("LINES")
    for l in lines:
        addr, line, lineno = lines[l]
        print("{:5d} ADDR {:04x} : {}".format(lineno, addr, line))
    print("==================")

    print("LABELS")
    print(labels)
    print("==================")

