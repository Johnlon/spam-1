import AMode.Dir
import CInv.Inv
import Flag.Set

data class Instruction(
    val aluOp: Op,
    val t: TDev,
    val a: ADev,
    val b: BDev,
    val condition: Cond,
    val setFlags: Flag, // set=true
    val conditionInvert: CInv, // invert=true
    val amode: AMode,  // direct=true, register=false
    val address: Int,
    val immed: Int
) {
    override fun toString(): String {
        return "Instruction(" +
                "op:$aluOp, " +
                "t:$t, " +
                "a:$a, " +
                "b:$b, " +
                "cond:$condition, " +
                "setf:$setFlags, " +
                "cinv:$conditionInvert, " +
                "amode:$amode, " +
                "address:$address(${address.toString(16).padStart(2, '0')}) " +
                "immed:$immed(${immed.toString(16).padStart(2, '0')}) " +
                ")";
    }
}

fun assemble(
    prog: Collection<Instruction>
) : Pair<List<Long>, List<Instruction>> {
    return assemble(*prog.toTypedArray())
}

fun assemble(
    vararg prog: Instruction
) : Pair<List<Long>, List<Instruction>>
{
    val rom=  mutableListOf<Long>()
    val instructions=  mutableListOf<Instruction>()
    var l = 0

    prog.forEach { inst ->
        val strF = "" +
                " O:" + tobin(inst.aluOp.id, 5) +
                " T:" + tobin(inst.t.id, 5, 3, 0) +
                " A:" + tobin(inst.a.id, 3) +
                " B:" + tobin(inst.b.id, 4, 2, 0) +
                " C:" + tobin(inst.condition.id, 4) +
                " F:" + tobin(inst.setFlags == Set) +
                " I:" + tobin(inst.conditionInvert == Inv) +
                " b:" + tobin(inst.b.id, 4, 3, 3) +
                " t:" + tobin(inst.t.id, 5, 4, 4) +
                " a:" + tobin(inst.amode == Dir) +
                " @:" + tobin(inst.address, 16) +
                " #:" + tobin(inst.immed, 8)

        System.out.println("ASM : " + strF)

        val str = strF.replace("\\s.:".toRegex(), "")
        rom.add(str.toLong(2))
        instructions.add(inst)
        l++

    }

    return Pair(rom, instructions)
}