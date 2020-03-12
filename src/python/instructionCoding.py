#!/bin/python3

flagsROMout = (0, 0)
flagsRAMout = (0, 1)
flagsALUout = (1, 0)
flagsUARTout = (1, 1)


def decodeDevice(e, d, c, b, a):
    devices = {
        (0, 0, 0, 0, 0): "RAMin",
        (0, 0, 0, 0, 1): "UNUSED",
        (0, 0, 0, 1, 0): "MARLOin",
        (0, 0, 0, 1, 1): "MARHIin",
        (0, 0, 1, 0, 0): "UARTin",
        (0, 0, 1, 0, 1): "JMPPAGEin",
        (0, 0, 1, 1, 0): "PCLOin",
        (0, 0, 1, 1, 1): "UNUSED",
        (0, 1, 0, 0, 0): "JMP",
        (0, 1, 0, 0, 1): "JMPOF",
        (0, 1, 0, 1, 0): "JMPZS",
        (0, 1, 0, 1, 1): "JMPCS",
        (0, 1, 1, 0, 0): "JMPDI",
        (0, 1, 1, 0, 1): "JMPDO",
        (0, 1, 1, 1, 0): "UNUSED",
        (0, 1, 1, 1, 1): "NOOP",
        (1, 0, 0, 0, 0): "A",
        (1, 0, 0, 0, 1): "B",
        (1, 0, 0, 1, 0): "C",
        (1, 0, 0, 1, 1): "D",
        (1, 0, 1, 0, 0): "E",
        (1, 0, 1, 0, 1): "F",
        (1, 0, 1, 1, 0): "G",
        (1, 0, 1, 1, 1): "H",
        (1, 1, 0, 0, 0): "I",
        (1, 1, 0, 0, 1): "J",
        (1, 1, 0, 1, 0): "K",
        (1, 1, 0, 1, 1): "L",
        (1, 1, 1, 0, 0): "M",
        (1, 1, 1, 0, 1): "N",
        (1, 1, 1, 1, 0): "O",
        (1, 1, 1, 1, 1): "P"
    }
    return devices[(e, d, c, b, a)]


class Bool(int):
    def __init__(self, b):
        if type(b) == int:
            if b == 0:
                b = False
            elif b == 1:
                b = True
            else:
                raise Exception("illegal argument {0} must be 0 or 1".format(b))

        if type(b) != bool:
            raise Exception("illegal argument {0} type {1} unsupportee".format(b, type(b)))

        self.b = b

    def __str__(self):
        return self.__class__.__name__ + ":" + str(self.b)

    def __repr__(self):
        return self.__str__()

    def __unicode__(self):
        return self.__str__()


class MARLOin(Bool):
    pass


class ROMout(Bool):
    pass


class RAMout(Bool):
    pass


class ALUout(Bool):
    pass


class UARTout(Bool):
    pass


class RAMin(Bool):
    pass


class MARLOin(Bool):
    pass


class MARHIin(Bool):
    pass


class UARTin(Bool):
    pass


class PCHiTmpin(Bool):
    pass


class PCLOin(Bool):
    pass


class PCHIin(Bool):
    pass


class RAMZP(Bool):
    pass


class REGin(Bool):
    pass


class Reg():
    def __init__(self, d: bool, c: bool, b: bool, a: bool):
        self.d = Bool(d)
        self.c = Bool(c)
        self.b = Bool(b)
        self.a = Bool(a)

        self.offset = a + b * 2 + c * 4 + d * 8
        self.reg = chr(ord('A') + self.offset)

    def __str__(self):
        return self.__class__.__name__ + ":" + self.reg

    def __repr__(self):
        return self.__str__()

    def __unicode__(self):
        return self.__str__()


class ALUOP():
    def __init__(self, e: bool, d: bool, c: bool, b: bool, a: bool):
        self.e = Bool(e)
        self.d = Bool(d)
        self.c = Bool(c)
        self.b = Bool(b)
        self.a = Bool(a)

        self.ops = [
            "0",
            "A",
            "B",
            "-A",
            "-B",
            "A+1",
            "B+1",
            "A-1",
            "B-1",
            "__A+B+Cin (0)",
            "__A-B-Cin (0)",
            "__B-A-Cin (0)",
            "A-B (special)",
            "__A+B+Cin (1)",
            "__A-B-Cin (1)",
            "__B-A-Cin (1)",
            "A*B (high bits)",
            "A*B (low bits)",
            "A/B",
            "A%B",
            "A << B",
            "A >> B arithmetic",
            "A >> B logical",
            "A ROL B",
            "A ROR B",
            "A AND B",
            "A OR B",
            "A XOR B",
            "NOT A",
            "NOT B",
            "A+B (BCD)",
            "A-B (BCD)",
        ]
        self.offset = a + b * 2 + c * 4 + d * 8 + e*16
        self.op = self.ops[self.offset]

    def __str__(self):
        return self.__class__.__name__ + ":" + self.op

    def __repr__(self):
        return self.__str__()

    def __unicode__(self):
        return self.__str__()



class Flags():
    def __init__(self,
                 rom_out: ROMout,
                 ram_out: RAMout,
                 alu_out: ALUout,
                 uart_out: UARTout,
                 ram_in: RAMin,
                 marlo_in: MARLOin,
                 marhi_in: MARHIin,
                 uart_in: UARTin,
                 pchitmp_in: PCHiTmpin,
                 pslo_in: PCLOin,
                 pchi_in: PCHIin,
                 reg_in: REGin,
                 reg_x: Reg,  # also = write addr
                 reg_y: Reg,
                 alu_op: ALUOP,
                 ram_zp: RAMZP
                 ):
        self.rom_out = rom_out
        self.ram_out = ram_out
        self.alu_out = alu_out
        self.uart_out = uart_out

        self.ram_in = ram_in
        self.marlo_in = marlo_in
        self.marhi_in = marhi_in
        self.uart_in = uart_in
        self.pchitmp_in = pchitmp_in
        self.pslo_in = pslo_in
        self.pchi_in = pchi_in
        self.reg_in = reg_in

        self.reg_x = reg_x
        self.reg_y = reg_y
        self.alu_op = alu_op

        self.ram_zp = ram_zp

        print(self.__dict__.values())

T = True
F = False
X = '#'

class FlagSet(dict):
    def __init__(self):
        self[ROMout.__name__] = X
        self[RAMout.__name__] = X
        self[ALUout.__name__] = X
        self[UARTout.__name__] = X
        self[RAMin.__name__] = X
        self[MARLOin.__name__] = X
        self[MARHIin.__name__] = X
        self[UARTin.__name__] = X
        self[PCHiTmpin.__name__] = X
        self[PCLOin.__name__] = X
        self[PCHIin.__name__] = X
        self[REGin.__name__] = X
        self[Reg.__name__] = X
        self[Reg.__name__] = X
        self[ALUOP.__name__] = X
        self[RAMZP.__name__] = X

#         ,
    # ram_out: RAMout,
    # alu_out: ALUout,
    # uart_out: UARTout,
    # ram_in: RAMin,
    # marlo_in: MARLOin,
    # marhi_in: MARHIin,
    # uart_in: UARTin,
    # pchitmp_in: PCHiTmpin,
    # pslo_in: PCLOin,
    # pchi_in: PCHIin,
    # reg_in: REGin,
    # reg_x: Reg,  # also = write addr
    # reg_y: Reg,
    # alu_op: ALUOP,
    # ram_zp: RAMZP


print(FlagSet())

def decode(code):
    bitStr = "{0:08b}".format(code)
    (BUSACC1, BUSACC0, ZPMODE, DEST4, DEST3, DEST2, DEST1, DEST0) = [int(x) for x in bitStr]
    dev=decodeDevice(DEST4, DEST3, DEST2, DEST1, DEST0)
    status = ""
    print("{0:03d} {1} : ".format(code, bitStr), end="")

    if (BUSACC1, BUSACC0) == flagsROMout:
        if ZPMODE:
            status = " # Not that useful as ZP address bits are occupied by the rom immediate"

        print("BUS_ACC={0:10s} WRITE_EN={1:10s} {2}".format("ROM", dev, status))

    if (BUSACC1, BUSACC0) == flagsRAMout:
        if dev == "RAMin":
            status = " # ILLEGAL can't read and write RAM"

        src = "RAM[MAR]"
        if ZPMODE:
            src = "RAM[ZP]"

        print("BUS_ACC={0:10s} WRITE_EN={1:10s} {2}".format(src, dev, status))

    if (BUSACC1, BUSACC0) == flagsALUout:
        regx=dev
        if not DEST4:
            status = " # regx " + regx + " switched to CONST0"
            regx="CONST0"

        if ZPMODE:
            status = status + " # dev " + dev + " switched to RAM[ZP]"
            dev="RAM[ZP]"

        print("BUS_ACC={0:10s} WRITE_EN={1:10s} ALUX={2:10s} ALUY=WORD2 ALUOP=WORD2 {3}".format("ALU", dev, regx, status))

    if (BUSACC1, BUSACC0) == flagsUARTout:

        if ZPMODE:
            status = status + " # dev " + dev + " switched to RAM[ZP]"
            dev="RAM[ZP]"

        print("BUS_ACC={0:10s} WRITE_EN={1:10s} ALUX={2:10s} ALUY=WORD2 ALUOP=WORD2 {3}".format("ALU", dev, regx, status))




def test():
    regx = Reg(F, F, F, F)
    regy = Reg(T, T, T, T)
    aluop = ALUOP(T, F, T, F, T)

    marloIn = MARLOin(T)

    Flags(T, T, T, T, T, marloIn, T, T, T, T, T, T, T, regx, regy, aluop)


test()
