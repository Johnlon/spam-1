#!/usr/bin/python 
import re


lines = {}
labels = {}
assembled = {}

devices = [
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

registers = []

for reg in range(16):
     r = chr(ord('A')+ reg)
     registers.append(r)
     devices.append(r)


def checkDest(x):
     if not x.upper() in devices:
          print("ERROR '{}' is not a device".format(x) )

def isDevice(x):
     return x.upper() in devices

def tryDevice(x):
     x = x.upper()
     if x in devices:
          return x
     else:
          return None

def tryReg(x):
     x = x.upper()
     if x in registers:
          return x
     else:
          return None


def error(s):
     print("ERROR " + s)
     exit(1)

def tryUart(x):
     x=x.upper()
     if x == "UART":
          return "UART"
     else:
          return None

def tryRam(x):
     x=x.upper()
     if x == "[]" or x == "[MAR]":
          return "RAM[MAR]"
     else:
          return None

def tryZP(x):
     x = x.upper()
     p = re.compile("^\[(.+)]$")
     r = p.match(x)
     if r:
          return tryConst(r.groups(1)[0])
     else:
          return None

def tryConst(x):
     x=x.upper()
     ph = re.compile("^\$([A-F0-9][A-F0-9])$")
     r = ph.match(x)
     if r:
          return int(r.groups(1)[0],16)
     else:
          pd = re.compile("^\$([0-9]{1,3})D$")
          r = pd.match(x)
          if r:
               dc = r.groups(1)[0]
               d=int(dc)
               if d>255:
                    error("constant '{}' to big for 8 bits".format(dc))
               else:
                    return d
          else:
               pd = re.compile("^\$([01][01][01][01][01][01][01][01])B$")
               r = pd.match(x)
               if r:
                    dc = r.groups(1)[0]
                    d=int(dc,2)
                    if d>255:
                         error("constant '{}' to big for 8 bits".format(dc))
                    else:
                         return d

               return None


with open("code.as", "r") as fp:
     line = fp.readline()
     address = 0
     while line:
          print("-----")
          line = line.strip()
          print("{:4d}: {}".format(address, line))

          lines[address] = {address, line};

          if line.startswith(":"):
               labels[line] = address
          elif line.startswith("#"):
               pass
          elif line == "":
               pass
          else:
               [typ, rest] = re.split("\s+", line, 1)
               if typ.upper() == "LD":
                    [dest, src] = re.split("\s*=\s*", rest, 1)
                    print("split:dest " + dest)
                    print("split:src  " + src)

                    dev = tryDevice(dest)
                    if dev is not None:
                         print("got dest device {}".format(dev))
                    else:
                         zp = tryZP(dest)
                         if zp is not None:
                              print("got dest zp {}".format(zp))
                         else:
                              error("not recognised dest '{}'".format(dest))

                    zpAddr = tryZP(src)
                    if zpAddr is not None:
                         print("got zp d{}".format(zpAddr))
                    else:
                         konst = tryConst(src)
                         if konst is not None:
                              print("got const d{}".format(konst))
                         else:
                              ram = tryRam(src)
                              if ram is not None:
                                   print("got ram source {}".format(ram))
                              else:
                                   uart = tryUart(src)
                                   if uart is not None:
                                        print("got uart source {}".format(uart))
                                   else:
                                        reg = tryReg(src)
                                        if reg is not None:
                                             print("got reg source {}".format(reg))
                                        else:
                                             error("not recognised source '{}'".format(src))


               elif typ.upper() == "OP":
                    pass
               else:
                    print("BAD type '{}'".format(typ))
               address += 1
          
          
          line = fp.readline()

print("LINES")
print(lines)
print("LABELS")
print(labels)


