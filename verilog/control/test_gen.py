
def tc(op, b_acc, d_in, zp, alu_passx, x_eq_0):
    code = op*32 +  d_in

    # top most op bit is in pos[0]
    bit = 1 if (d_in>16) else 0;
    d_rot = ((d_in << 1) & 0x1e) + bit

    print(
        "{0:03d}: ".format(code) +
        " op={0:03b}".format(op) + 
        " d_in={0:05b}".format(d_rot) + 
        " bus={0:9s}".format(b_acc) +
        " _zp={0}".format(zp) + 
        " _alu_passx={0}".format(alu_passx) + 
        " _x_eq_0={0}".format(x_eq_0) + 
        " // {0}".format(idx[d_in])
    )



op_DEV_eq_ROM_sel = 0
op_DEV_eq_RAM_sel = 1
op_DEV_eq_RAMZP_sel = 2
op_DEV_eq_UART_sel = 3
op_NONREG_eq_OPREGY_sel = 5
op_REGX_eq_ALU_sel = 5
op_RAMZP_eq_REG_sel = 6
op_RAMZP_eq_UART_sel = 7

idx={}
idx[0] = "idx_RAM_sel"
idx[1] = "idx_MARLO_sel"
idx[2] = "idx_MARHI_sel"
idx[3] = "idx_UART_sel"
idx[4] = "idx_PCHITMP_sel"
idx[5] = "idx_PCLO_sel"
idx[6] = "idx_PC_sel"
idx[7] = "idx_JMPO_sel"
idx[8] = "idx_JMPZ_sel"
idx[9] = "idx_JMPC_sel"
idx[10] = "idx_JMPDI_sel"
idx[11] = "idx_JMPDO_sel"
idx[12] = "idx_JMPEQ_sel"
idx[13] = "idx_JMPNE_sel"
idx[14] = "idx_JMPGT_sel"
idx[15] = "idx_JMPLT_sel"
for r in range(16):
    idx[16+r] = "idx_REG" + chr(ord('A') + r) + "_sel"



for i in range(32):
    tc(op=op_DEV_eq_ROM_sel, b_acc="_rom_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op=op_DEV_eq_RAM_sel, b_acc="_ram_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op=op_DEV_eq_RAMZP_sel, b_acc="_ram_out", d_in=i, zp=0, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op=op_DEV_eq_UART_sel, b_acc="_uart_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op=op_NONREG_eq_OPREGY_sel, b_acc="_alu_out", d_in=i, zp=1, alu_passx=1, x_eq_0=0)

for i in range(32):
    tc(op=op_REGX_eq_ALU_sel, b_acc="_alu_out", d_in=i, zp=1, alu_passx=1, x_eq_0=1)

for i in range(32):
    tc(op=op_RAMZP_eq_REG_sel, b_acc="_alu_out", d_in=i, zp=0, alu_passx=0, x_eq_0=1)

for i in range(32):
    tc(op=op_RAMZP_eq_UART_sel, b_acc="_uart_out", d_in=i, zp=0, alu_passx=1, x_eq_0=1)

