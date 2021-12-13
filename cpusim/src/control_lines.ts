
ADEV_rega : 0,
ADEV_regb : 1,
ADEV_regc : 2,
ADEV_regd : 3,
ADEV_marlo : 4,
ADEV_marhi : 5,
ADEV_uart : 6,
ADEV_not_used : 7,

adev_devices : {
  ADEV_rega: this.ADEV_rega,
  ADEV_regb: this.ADEV_regb,
  ADEV_regc: this.ADEV_regc,
  ADEV_regd: this.ADEV_regd,
  ADEV_marlo: this.ADEV_marlo,
  ADEV_marhi: this.ADEV_marhi,
  ADEV_uart: this.ADEV_uart,
  ADEV_not_used: this.ADEV_not_used,
}

// B BUS
//BDEV_rega : 0
//BDEV_regb : 1
//BDEV_regc : 2
//BDEV_regd : 3
//BDEV_marlo : 4
//BDEV_marhi : 5
//BDEV_immed : 6 // IMMED READ FROM THE INSTRUCTION
//BDEV_ram : 7
//BDEV_not_used : 8 // DOES IT MAKE SENSE TO HAVE A LITERAL NOT USED DEVICE OR SELECT RAND OR CLOCK INSTEAD FOR INTANCE WHEN WE DONT CARE.
//BDEV_vram : 9
//BDEV_port : 10
//
//bdev_devices : {
//BDEV_rega :BDEV_rega ,
//BDEV_regb :BDEV_regb ,
//BDEV_regc :BDEV_regc ,
//BDEV_regd :BDEV_regd ,
//BDEV_marlo :BDEV_marlo ,
//BDEV_marhi :BDEV_marhi ,
//BDEV_immed :BDEV_immed ,
//BDEV_ram :BDEV_ram ,
//BDEV_not_used :BDEV_not_used ,
//BDEV_vram :BDEV_vram ,
//BDEV_port :BDEV_port ,
//}
//
//// DEST
//TDEV_rega : 0
//TDEV_regb : 1
//TDEV_regc : 2
//TDEV_regd : 3
//TDEV_marlo : 4
//TDEV_marhi : 5
//TDEV_uart : 6
//TDEV_ram : 7
//TDEV_halt : 8
//TDEV_vram : 9
//TDEV_port : 10
//TDEV_portsel : 11
//TDEV_not_used12 : 12
//TDEV_pchitmp : 13 // only load pchitmp
//TDEV_pclo : 14     // only load pclo
//TDEV_pc : 15       // load pclo from instruction and load pchi from pchitmp
//
//targ_devices : {
//  TDEV_rega: TDEV_rega,
//  TDEV_regb: TDEV_regb,
//  TDEV_regc: TDEV_regc,
//  TDEV_regd: TDEV_regd,
//  TDEV_marlo: TDEV_marlo,
//  TDEV_marhi: TDEV_marhi,
//  TDEV_uart: TDEV_uart,
//  TDEV_ram: TDEV_ram,
//  TDEV_halt: TDEV_halt,
//  TDEV_vram: TDEV_vram,
//  TDEV_port: TDEV_port,
//  TDEV_portsel: TDEV_portsel,
//  TDEV_not_used12: TDEV_not_used12,
//  TDEV_pchitmp: TDEV_pchitmp,
//  TDEV_pclo: TDEV_pclo,
//  TDEV_pc: TDEV_pc,
//}
//
}
