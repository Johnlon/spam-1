import AMode.Dir
import AMode.Reg
import CInv.Inv
import CInv.Std
import Flag.Keep
import Flag.Set
import java.io.File

fun alu(rom: MutableList<Int>?, aluOp: Op, carryIn: Boolean, a: Int, b: Int): Tuple5<Int, MutableList<Cond>, Op, Int, Int> {
    var effectiveOp = aluOp

    if (aluOp == Op.A_PLUS_B_PLUS_C && !carryIn)
        effectiveOp = Op.A_PLUS_B
    if (aluOp == Op.A_MINUS_B_MINUS_C && !carryIn)
        effectiveOp = Op.A_MINUS_B
    if (aluOp == Op.B_MINUS_A_MINUS_C && !carryIn)
        effectiveOp = Op.B_MINUS_A

    val ret = if (rom!=null) {
        val address = b + (a shl 8) + (effectiveOp.id shl 16)

        val ret = rom[address]
        val f = (ret ushr 8) and 0xff
        val flags = mutableListOf(Cond.A)
        // active low
        if ((f and 0x01) == 0) flags.add(Cond.LT)
        if ((f and 0x02) == 0) flags.add(Cond.GT)
        if ((f and 0x04) == 0) flags.add(Cond.NE)
        if ((f and 0x08) == 0) flags.add(Cond.EQ)
        if ((f and 0x10) == 0) flags.add(Cond.O)
        if ((f and 0x20) == 0) flags.add(Cond.N)
        if ((f and 0x40) == 0) flags.add(Cond.Z)
        if ((f and 0x80) == 0) flags.add(Cond.C)
        Pair(ret and 0xff, flags)
    } else {

        /*
        http://class.ece.iastate.edu/arun/Cpre381/lectures/arithmetic.pdf
        pass sign bits in for addition overflow
        - No overflow when adding a +ve and a -ve number
        - No overflow when signs are the same for subtraction (because -- means a +)
        - overflow when adding two +ves yields a -ve
        - or, adding two -ves gives a +ve
        - or, subtract a -ve from a +ve and get a -ve
        - or, subtract a +ve from a -ve and get a +ve
        */
        fun addOv(left: Int, right: Int, result: Int): Boolean {
            val negLeft = (left and 0x80) != 0
            val negRight = (right and 0x80) != 0
            val negResult = (result and 0x80) != 0

            if (negLeft != negRight) return false // negs diff
            if (negLeft && negRight && !negResult) return true // adding two -ve but got +ve
            if (!negLeft && !negRight && negResult) return true // adding two +ve but got -vee
            return false
        }

        fun subOv(left: Int, right: Int, result: Int): Boolean {
            val negLeft = (left and 0x80) != 0
            val negRight = (right and 0x80) != 0
            val negResult = (result and 0x80) != 0

            if (negLeft == negRight) return false // plus - plus doesn't overflow
            if (!negLeft && negRight && negResult) return true // plus - negative is overflow if result is neg
            if (negLeft && !negRight && !negResult) return true // neg - plus is overflow if result is plus
            return false
        }

        var overflow = false
        var carry = false

        val ret: Int = when (effectiveOp) {
            Op.ZERO -> 0
            Op.A -> a
            Op.B -> b
            Op.NEGATE_A -> {
                overflow = (a == 128) // 128 is same as unsigned 10000000
                -a
            }
            Op.NEGATE_B -> {
                overflow = (b == 128) // 128 is same as unsigned 10000000
                -b
            }
            Op.BA_DIV_10 -> TODO()
            Op.BA_MOD_10 -> TODO()
            Op.B_PLUS_1 -> TODO()
            Op.B_MINUS_1 -> TODO()
            Op.A_PLUS_B -> {
                overflow = addOv(a, b, a + b)
                carry = (a + b) > 255
                a + b
            }
            Op.A_MINUS_B -> {
                overflow = subOv(a, b, a - b)
                carry = (a - b) < 0
                a - b
            }
            Op.B_MINUS_A -> {
                overflow = subOv(b, a, b - a)
                carry = (b - a) < 0
                b - a
            }
            Op.A_MINUS_B_SIGNEDMAG -> {
                overflow = subOv(a, b, a - b)
                carry = (a - b) < 0
                a - b
            }
            Op.A_PLUS_B_PLUS_C -> {
                overflow = addOv(a, b, a + b + 1)
                carry = (a + b + 1) > 255
                a + b + 1
            }
            Op.A_MINUS_B_MINUS_C -> {
                overflow = subOv(a, b, a - b - 1)
                carry = (a - b - 1) < 0
                (a - b) - 1
            }
            Op.B_MINUS_A_MINUS_C -> {
                overflow = subOv(b, a, (b - a) - 1)
                carry = ((b - a) - 1) < 0
                (b - a) - 1
            }
            Op.A_TIMES_B_LO -> TODO()
            Op.A_TIMES_B_HI -> TODO()
            Op.A_DIV_B -> TODO()
            Op.A_MOD_B -> TODO()
            Op.A_LSL_B -> {
                val v = a shl (Math.min(b, 16)) // kotlin sees to have a limit on shift of 5 bits
                carry = (v and 0x100) != 0
                overflow = (a and 0x80) != (v and 0x80)
                v
            }
            Op.A_LSR_B -> {
                val v = a ushr (Math.min(b, 16))
                carry = (a ushr (Math.min(b - 1, 16)) and 1) != 0
                overflow = (a and 0x80) != (v and 0x80)
                v
            }
            Op.A_ASR_B -> TODO()
            Op.A_RLC_B -> TODO()
            Op.A_RRC_B -> TODO()
            Op.A_AND_B -> {
                a and b
            }
            Op.A_OR_B -> {
                a or b
            }
            Op.A_XOR_B -> TODO()
            Op.A_NAND_B -> TODO()
            Op.NOT_B -> TODO()
            Op.A_PLUS_B_BCD -> TODO()
            Op.A_MINUS_B_BCD -> TODO()
        }

        val ret8: Int = (ret and 0xff)

        val flags = mutableListOf(Cond.A)
        if (aluOp == Op.A_MINUS_B_SIGNEDMAG) {
            if (a.toByte() > b.toByte()) flags.add(Cond.GT)
            if (a.toByte() < b.toByte()) flags.add(Cond.LT)
            if (a.toByte() == b.toByte()) flags.add(Cond.EQ)
            if (a.toByte() != b.toByte()) flags.add(Cond.NE)
        } else {
            // FIXME - SHOULD BE USING UNSIGNED MAG CHECK - NOT TESTED !!!
            if (a.toUByte() > b.toUByte()) flags.add(Cond.GT)
            if (a.toUByte() < b.toUByte()) flags.add(Cond.LT)
            if (a.toUByte() == b.toUByte()) flags.add(Cond.EQ)
            if (a.toUByte() != b.toUByte()) flags.add(Cond.NE)
        }

        if (ret8 == 0) flags.add(Cond.Z)
        if ((ret8 and 0x80) != 0) flags.add(Cond.N)
        if (carry) flags.add(Cond.C)
        if (overflow) flags.add(Cond.O)

        Pair(ret8, flags)
    }

    //println("ALU : $a ($effectiveOp) $b => ${ret.first}")

    ret.second.sortBy { x -> x.id }
    return Tuple5(ret.first, ret.second, effectiveOp, a, b)
}
