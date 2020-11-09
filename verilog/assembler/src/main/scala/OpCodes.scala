
//object AddrMode extends Enumeration {
//  type AddrMode = Value
//  val Absolute,
//  AbsoluteIndexedIndirect,
//  AbsoluteIndexedX,
//  AbsoluteIndexedY,
//  AbsoluteIndirect,
//  Immediate,
//  Implied,
//  Relative,
//  ZeroPage,
//  ZeroPagePreIndexedIndirect,
//  ZeroPageIndexedX,
//  ZeroPageIndexedY,
//  ZeroPageIndirect,
//  ZeroPagePostIndexedIndirect = Value
//}

//object Operation extends Enumeration {
//  type Operation = Value
//  val ADC  , AND  , ASL  , BBR0 , BBR1 , BBR2 , BBR3 , BBR4 , BBR5 , BBR6,
//  BBR7 , BBS0 , BBS1 , BBS2 , BBS3 , BBS4 , BBS5 , BBS6 , BBS7 , BCC,
//  BCS  , BEQ  , BIT  , BMI  , BNE  , BPL  , BRA  , BRK  , BVC  , BVS,
//  CLC  , CLD  , CLI  , CLV  , CMP  , CPX  , CPY  , DEC  , DEX  , DEY,
//  EOR  , INC  , INX  , INY  , JMP  , JSR  , LDA  , LDX  , LDY  , LSR,
//  NOP  , ORA  , PHA  , PHP  , PHX  , PHY  , PLA  , PLP  , PLX  , PLY,
//  RMB0 , RMB1 , RMB2 , RMB3 , RMB4 , RMB5 , RMB6 , RMB7 , ROL  , ROR,
//  RTI  , RTS  , SBC  , SEC  , SED  , SEI  , SMB0 , SMB1 , SMB2 , SMB3,
//  SMB4 , SMB5 , SMB6 , SMB7 , STA  , STP  , STX  , STY  , STZ  , TAX,
//  TAY  , TRB  , TSB  , TSX  , TXA  , TXS  , TYA  , WAI = Value
//  val relativeAddrOps = List(
//    BBR0 , BBR1 , BBR2 , BBR3 , BBR4 , BBR5 , BBR6 , BBR7 ,
//    BBS0 , BBS1 , BBS2 , BBS3 , BBS4 , BBS5 , BBS6 , BBS7 ,
//    BCC  , BCS  , BEQ  , BMI  , BNE  , BPL  , BRA  , BVC  ,
//    BVS
//  )
//}

//case class Op(opCode:Int, bytes:Int)
//
//object OpCodes {
//  import AddrMode._
//  import Operation._
//  private val operations = Map(
//    ADC -> Map(Immediate -> Op(0x69, 1), ZeroPage -> Op(0x65, 1), Absolute -> Op(0x6d, 2), AbsoluteIndexedX -> Op(0x7d, 2), AbsoluteIndexedY -> Op(0x79, 2), ZeroPageIndexedX -> Op(0x75, 1), ZeroPagePreIndexedIndirect -> Op(0x61, 1), ZeroPagePostIndexedIndirect -> Op(0x71, 1)),
//    AND -> Map(Immediate -> Op(0x29, 1), ZeroPage -> Op(0x25, 1), Absolute -> Op(0x2d, 2), AbsoluteIndexedX -> Op(0x3d, 2), AbsoluteIndexedY -> Op(0x39, 2), ZeroPageIndexedX -> Op(0x35, 1), ZeroPagePreIndexedIndirect -> Op(0x21, 1), ZeroPagePostIndexedIndirect -> Op(0x31, 1)),
//    ASL -> Map(Implied -> Op(0x0a, 0), ZeroPage -> Op(0x06, 1), Absolute -> Op(0x0e, 2), AbsoluteIndexedX -> Op(0x1e, 2), ZeroPageIndexedX -> Op(0x16, 1)),
//    BCC -> Map(Relative -> Op(0x90, 1)),
//    BCS -> Map(Relative -> Op(0xb0, 1)),
//    BEQ -> Map(Relative -> Op(0xf0, 1)),
//    BIT -> Map(ZeroPage -> Op(0x24, 1), Absolute -> Op(0x2c, 2)),
//    BMI -> Map(Relative -> Op(0x30, 1)),
//    BNE -> Map(Relative -> Op(0xd0, 1)),
//    BPL -> Map(Relative -> Op(0x10, 1)),
//    BRK -> Map(Implied -> Op(0x00, 0)),
//    BVC -> Map(Relative -> Op(0x50, 1)),
//    BVS -> Map(Relative -> Op(0x70, 1)),
//    CLC -> Map(Implied -> Op(0x18, 0)),
//    CLD -> Map(Implied -> Op(0xd8, 0)),
//    CLI -> Map(Implied -> Op(0x58, 0)),
//    CLV -> Map(Implied -> Op(0xb8, 0)),
//    CMP -> Map(Immediate -> Op(0xc9, 1), ZeroPage -> Op(0xc5, 1), Absolute -> Op(0xcd, 2), AbsoluteIndexedX -> Op(0xdd, 2), AbsoluteIndexedY -> Op(0xd9, 2), ZeroPageIndexedX -> Op(0xd5, 1), ZeroPagePreIndexedIndirect -> Op(0xc1, 1), ZeroPagePostIndexedIndirect -> Op(0xd1, 1)),
//    CPX -> Map(Immediate -> Op(0xe0, 1), ZeroPage -> Op(0xe4, 1), Absolute -> Op(0xec, 2)),
//    CPY -> Map(Immediate -> Op(0xc0, 1), ZeroPage -> Op(0xc4, 1), Absolute -> Op(0xcc, 2)),
//    DEC -> Map(ZeroPage -> Op(0xc6, 1), Absolute -> Op(0xce, 2), AbsoluteIndexedX -> Op(0xde, 2), ZeroPageIndexedX -> Op(0xd6, 1)),
//    DEX -> Map(Implied -> Op(0xca, 0)),
//    DEY -> Map(Implied -> Op(0x88, 0)),
//    EOR -> Map(Immediate -> Op(0x49, 1), ZeroPage -> Op(0x45, 1), Absolute -> Op(0x4d, 2), AbsoluteIndexedX -> Op(0x5d, 2), AbsoluteIndexedY -> Op(0x59, 2), ZeroPageIndexedX -> Op(0x55, 1), ZeroPagePreIndexedIndirect -> Op(0x41, 1), ZeroPagePostIndexedIndirect -> Op(0x51, 1)),
//    INC -> Map(ZeroPage -> Op(0xe6, 1), Absolute -> Op(0xee, 2), AbsoluteIndexedX -> Op(0xfe, 2), ZeroPageIndexedX -> Op(0xf6, 1)),
//    INX -> Map(Implied -> Op(0xe8, 0)),
//    INY -> Map(Implied -> Op(0xc8, 0)),
//    JMP -> Map(Absolute -> Op(0x4c, 2), AbsoluteIndirect -> Op(0x6c, 2)),
//    JSR -> Map(Absolute -> Op(0x20, 2)),
//    LDA -> Map(Immediate -> Op(0xa9, 1), ZeroPage -> Op(0xa5, 1), Absolute -> Op(0xad, 2), AbsoluteIndexedX -> Op(0xbd, 2), AbsoluteIndexedY -> Op(0xb9, 2), ZeroPageIndexedX -> Op(0xb5, 1), ZeroPagePreIndexedIndirect -> Op(0xa1, 1), ZeroPagePostIndexedIndirect -> Op(0xb1, 1)),
//    LDX -> Map(Immediate -> Op(0xa2, 1), ZeroPage -> Op(0xa6, 1), Absolute -> Op(0xae, 2), AbsoluteIndexedY -> Op(0xbe, 2), ZeroPageIndexedY -> Op(0xb6, 1)),
//    LDY -> Map(Immediate -> Op(0xa0, 1), ZeroPage -> Op(0xa4, 1), Absolute -> Op(0xac, 2), AbsoluteIndexedX -> Op(0xbc, 2), ZeroPageIndexedX -> Op(0xb4, 1)),
//    LSR -> Map(Implied -> Op(0x4a, 0), ZeroPage -> Op(0x46, 1), Absolute -> Op(0x4e, 2), AbsoluteIndexedX -> Op(0x5e, 2), ZeroPageIndexedX -> Op(0x56, 1)),
//    NOP -> Map(Implied -> Op(0xea, 0)),
//    ORA -> Map(Immediate -> Op(0x09, 1), ZeroPage -> Op(0x05, 1), Absolute -> Op(0x0d, 2), AbsoluteIndexedX -> Op(0x1d, 2), AbsoluteIndexedY -> Op(0x19, 2), ZeroPageIndexedX -> Op(0x15, 1), ZeroPagePreIndexedIndirect -> Op(0x01, 1), ZeroPagePostIndexedIndirect -> Op(0x11, 1)),
//    ROL -> Map(Implied -> Op(0x2a, 0), ZeroPage -> Op(0x26, 1), Absolute -> Op(0x2e, 2), AbsoluteIndexedX -> Op(0x3e, 2), ZeroPageIndexedX -> Op(0x36, 1)),
//    ROR -> Map(Implied -> Op(0x6a, 0), ZeroPage -> Op(0x66, 1), Absolute -> Op(0x6e, 2), AbsoluteIndexedX -> Op(0x7e, 2), ZeroPageIndexedX -> Op(0x76, 1)),
//    PHA -> Map(Implied -> Op(0x48, 0)),
//    PHP -> Map(Implied -> Op(0x08, 0)),
//    PLA -> Map(Implied -> Op(0x68, 0)),
//    PLP -> Map(Implied -> Op(0x28, 0)),
//    RTI -> Map(Implied -> Op(0x40, 0)),
//    RTS -> Map(Implied -> Op(0x60, 0)),
//    SBC -> Map(Immediate -> Op(0xe9, 1), ZeroPage -> Op(0xe5, 1), Absolute -> Op(0xed, 2), AbsoluteIndexedX -> Op(0xfd, 2), AbsoluteIndexedY -> Op(0xf9, 2), ZeroPageIndexedX -> Op(0xf5, 1), ZeroPagePreIndexedIndirect -> Op(0xe1, 1), ZeroPagePostIndexedIndirect -> Op(0xf1, 1)),
//    SEC -> Map(Implied -> Op(0x38, 0)),
//    SED -> Map(Implied -> Op(0xf8, 0)),
//    SEI -> Map(Implied -> Op(0x78, 0)),
//    STA -> Map(ZeroPage -> Op(0x85, 1), Absolute -> Op(0x8d, 2), AbsoluteIndexedX -> Op(0x9d, 2), AbsoluteIndexedY -> Op(0x99, 2), ZeroPageIndexedX -> Op(0x95, 1), ZeroPagePreIndexedIndirect -> Op(0x81, 1), ZeroPagePostIndexedIndirect -> Op(0x91, 1)),
//    STX -> Map(ZeroPage -> Op(0x86, 1), Absolute -> Op(0x8e, 2), ZeroPageIndexedY -> Op(0x96, 1)),
//    STY -> Map(ZeroPage -> Op(0x84, 1), Absolute -> Op(0x8c, 2), ZeroPageIndexedX -> Op(0x94, 1)),
//    TAX -> Map(Implied -> Op(0xaa, 0)),
//    TAY -> Map(Implied -> Op(0xa8, 0)),
//    TSX -> Map(Implied -> Op(0xba, 0)),
//    TXA -> Map(Implied -> Op(0x8a, 0)),
//    TXS -> Map(Implied -> Op(0x9a, 0)),
//    TYA -> Map(Implied -> Op(0x98, 0))
//  )
//
//  def apply(op:Operation.Value, addrMode:AddrMode.Value) = operations(op)(addrMode)
//  def has(op:Operation.Value, addrMode:AddrMode.Value) = operations(op).contains(addrMode)
//}