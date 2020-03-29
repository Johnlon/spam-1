# start = left most, end is right most
def subbits(x, start, end):
    return (x >> end) & (pow(2,1+start-end)-1)

def unrot(bits):
    # top most op bit is in pos[0]
    bottombit = bits % 2
    return (bits >> 1) + (bottombit*16)


def tc(bits_7_5, bits_4_0, b_acc, d_in, zp, alu_passx, x_eq_0):
    #op=subbits(opidx, 7, 5)
    opidx=bits_7_5

    code = bits_7_5*32 + bits_4_0

    # top most op bit is in pos[0]
    #bit = 1 if (d_in>16) else 0;
    #d_rot = ((d_in << 1) & 0x1e) + bit
    dev=devices[d_in]
    err=""
    if (dev == "_RAM_in" and b_acc == "_ram_out"):
        i="MAR" if zp  else "ZP"
        err="// => RAM["+i+"]=0 - _WE wins & bus is Z pulled down to 0"  

    bus_outs={"_ram_out", "_rom_out", "_alu_out", "_uart_out"}
    devs={"_ram_out", "_rom_out", "_alu_out", "_uart_out"}

    verilog=1
    if verilog:
        print("hi_rom=8'b{0:08b};".format(code))
        print("#101") 
        for o in bus_outs:
            if o == b_acc:
                print("`Equals({0}, F);".format(o))
            else:
                print("`Equals({0}, T);".format(o))
        for o in devices:
            dname = devices[o]
            if dname == dev:
                print("`Equals({0}, F);".format(dname))
            else:
                print("`Equals({0}, T);".format(dname))
    else:
        print(
            "{0:08b}: ".format(code) +
            " op={0:23s}".format(ops[opidx]) +
            " _DEV_in={0:13s}".format(devices[d_in]) + 
            " _BUS_out={0:9s}".format(b_acc) +
            " _zp={0}".format(zp) + 
            " _alu_passx={0}".format(alu_passx) + 
            " _x_eq_0={0}".format(x_eq_0) +
            "   " + err
        )


ops={}
ops[0] = "op_DEV_eq_ROM_sel"
ops[1] = "op_DEV_eq_RAM_sel"
ops[2] = "op_DEV_eq_RAMZP_sel"
ops[3] = "op_DEV_eq_UART_sel"
ops[4] = "op_NONREG_eq_OPREGY_sel"
ops[5] = "op_REGX_eq_ALU_sel"
ops[6] = "op_RAMZP_eq_REG_sel"
ops[7] = "op_RAMZP_eq_UART_sel"

devices={}
devices[0] = "_ram_in"
devices[1] = "_marlo_in"
devices[2] = "_marhi_in"
devices[3] = "_uart_in"
devices[4] = "_pchitmp_in"
devices[5] = "_pclo_in"
devices[6] = "_pc_in"
devices[7] = "_jmpo_in"
devices[8] = "_jmpz_in"
devices[9] = "_jmpc_in"
devices[10] = "_jmpdi_in"
devices[11] = "_jmpdo_in"
devices[12] = "_jmpeq_in"
devices[13] = "_jmpne_in"
devices[14] = "_jmpgt_in"
devices[15] = "_jmplt_in"

for r in range(16):
    devices[16+r] = "_reg" + chr(ord('a') + r) + "_in"

# tests

for i in range(32):
    tc(bits_7_5=0, bits_4_0=i, b_acc="_rom_out", d_in=unrot(i), zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(bits_7_5=1, bits_4_0=i, b_acc="_ram_out", d_in=unrot(i), zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(bits_7_5=2, bits_4_0=i, b_acc="_ram_out", d_in=unrot(i), zp=0, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(bits_7_5=3, bits_4_0=i, b_acc="_uart_out", d_in=unrot(i), zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    # never a reg in
    tc(bits_7_5=4, bits_4_0=i, b_acc="_alu_out", d_in=subbits(i, 4, 1), zp=1, alu_passx=1, x_eq_0=0)

for i in range(32):
    # always a reg in
    tc(bits_7_5=5, bits_4_0=i, b_acc="_alu_out", d_in=16+subbits(i, 4,1), zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    # always ram in
    tc(bits_7_5=6, bits_4_0=i, b_acc="_alu_out", d_in=0, zp=0, alu_passx=0, x_eq_0=1)

for i in range(32):
    # always ram in
    tc(bits_7_5=7, bits_4_0=i, b_acc="_uart_out", d_in=0, zp=0, alu_passx=1, x_eq_0=1)

