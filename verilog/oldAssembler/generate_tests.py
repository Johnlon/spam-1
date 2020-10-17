#!/usr/bin/python

# start = left most, end is right most
def subbits(x, start, end):
    return (x >> end) & (pow(2,1+start-end)-1)

def unrot(bits):
    # top most op bit is in pos[0]
    bottombit = bits % 2
    return (bits >> 1) + (bottombit*16)

def devName(dev):
    return dev.replace("_reg", "").replace("_in","").replace("_","")

def tc(op, bits_4_0, b_acc, d_in, zp, alu_passx, x_eq_0):
    opidx=ops.index(op)

    code = opidx*32 + bits_4_0

    # top most op bit is in pos[0]
    #bit = 1 if (d_in>16) else 0;
    #d_rot = ((d_in << 1) & 0x1e) + bit
    dev=devices[d_in]
    err=""
    if (dev == "_ram_in" and b_acc == "_ram_out"):
        i="MAR" if zp  else "ZP"
        err="// => RAM["+i+"]=0 - _WE wins & bus is Z pulled down to 0"  

    if (dev == "_uart_in" and b_acc == "_uart_out"):
        err="// => UART= UNKNOWN "

    bus_outs={"_ram_out", "_rom_out", "_alu_out", "_uart_out"}

    verilogtest=1
    assemblertest=2
    listing=3

    out=assemblertest
    if out == verilogtest:
        
        print("// {0}".format(err))
        print("// {0} {1}".format(op, dev))
        print("hi_rom=8'b{0:08b};".format(code))
        print("#101") 
        for o in bus_outs:
            if o == b_acc:
                print("`Equals({0}, F); //  expected".format(o))
            else:
                print("`Equals({0}, T);".format(o))
    elif out == assemblertest:
        
        #devicename=dev.replace("_in","").replace("_","").replace("_reg", "")
        devicename=dev.replace("_reg", "").replace("_in","").replace("_","")
        if opidx==0:
            print("ld {}=${}d".format(devicename, d_in))
        elif opidx==1:
            print("ld {}=[]".format(devicename))
        elif opidx==2:
            print("ld {}=[${}d]".format(devicename, d_in))
        elif opidx==3:
            print("ld {}=uart".format(devicename))
        elif opidx==4:
            nonreg=devName(devices[d_in%16])
            reg=devName(devices[16+(d_in%16)])
            print("ld {}=0(+){}".format(nonreg,reg))
        elif opidx==5:
            regx=devName(devices[16+(d_in%16)])
            regy=devName(devices[16+((15-d_in)%16)])
            print("ld {}={}(+){}".format(regx,regx,regy))
        elif opidx==6:
            regx=devName(devices[16+(d_in%16)])
            print("ld [${}d]={}".format(d_in,regx))
        elif opidx==7:
            regx=devName(devices[16+(d_in%16)])
            print("ld [${}d]=uart".format(d_in))

        print("#checkop {}".format(opidx))

    elif out == listing:
        print(
            "{0:08b}: ".format(code) +
            " op={0:23s}".format(op) +
            " _DEV_in={0:13s}".format(devices[d_in]) + 
            " _BUS_out={0:9s}".format(b_acc) +
            " _zp={0}".format(zp) + 
            " _alu_passx={0}".format(alu_passx) + 
            " _x_eq_0={0}".format(x_eq_0) +
            "   " + err
        )


ops=[
"op_DEV_eq_ROM_sel",
"op_DEV_eq_RAM_sel",
"op_DEV_eq_RAMZP_sel",
"op_DEV_eq_UART_sel",
"op_NONREG_eq_OPREGY_sel",
"op_REGX_eq_ALU_sel",
"op_RAMZP_eq_REG_sel",
"op_RAMZP_eq_UART_sel"
]

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
    tc(op="op_DEV_eq_ROM_sel", bits_4_0=i, b_acc="_rom_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op="op_DEV_eq_RAM_sel", bits_4_0=i, b_acc="_ram_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op="op_DEV_eq_RAMZP_sel", bits_4_0=i, b_acc="_ram_out", d_in=i, zp=0, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op="op_DEV_eq_UART_sel", bits_4_0=i, b_acc="_uart_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    # never a reg in
    tc(op="op_NONREG_eq_OPREGY_sel", bits_4_0=i, b_acc="_alu_out", d_in=i, zp=1, alu_passx=1, x_eq_0=0)

for i in range(32):
    # always a reg in
    tc(op="op_REGX_eq_ALU_sel", bits_4_0=i, b_acc="_alu_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)


for i in range(32):
    # always ram in
    tc(op="op_RAMZP_eq_REG_sel", bits_4_0=i, b_acc="_alu_out", d_in=i, zp=0, alu_passx=0, x_eq_0=1)

for i in range(32):
    # always ram in
    tc(op="op_RAMZP_eq_UART_sel", bits_4_0=i, b_acc="_uart_out", d_in=i, zp=0, alu_passx=1, x_eq_0=1)

