import AMode.Dir
import CInv.Inv
import CInv.Std
import Flag.Keep
import Flag.Set
import java.io.File


fun prog(cpu: CPU) {
    assemble(
        cpu.rom,
        cpu.instructions,
        Instruction(
            Op.B, TDev.marlo, ADev.marhi, BDev.immed, Cond.A, Keep, Std, Dir, 0xffff, 0x0
        ),
        Instruction(
            Op.B, TDev.marhi, ADev.marhi, BDev.immed, Cond.A, Keep, Std, Dir, 0xffff, 0x0
        ),
        Instruction(
            Op.A_PLUS_B, TDev.marlo, ADev.marlo, BDev.immed, Cond.A, Set, Std, Dir, 0xffff, 0x1
        ),
        Instruction(
            Op.B, TDev.pchitmp, ADev.marhi, BDev.immed, Cond.A, Keep, Std, Dir, 0xffff, 0x0
        ),
        Instruction(
            Op.B, TDev.pc, ADev.marhi, BDev.immed, Cond.C, Keep, Inv, Dir, 0xffff, 0x2
        ),
        Instruction(
            Op.A_PLUS_B, TDev.marhi, ADev.marhi, BDev.immed, Cond.A, Set, Std, Dir, 0xffff, 0x1
        ),
        Instruction(
            Op.B, TDev.pchitmp, ADev.marhi, BDev.immed, Cond.A, Keep, Std, Dir, 0xffff, 0x0
        ),
        Instruction(
            Op.B, TDev.pc, ADev.marhi, BDev.immed, Cond.C, Keep, Inv, Dir, 0xffff, 0x2
        ),
        Instruction(
            Op.B, TDev.halt, ADev.nu, BDev.not_used, Cond.A, Keep, Std, Dir, 0xffff, 0x0
        )
    )
}


fun loadProgram(cpu: CPU, romFile: String) {
    var loc = 0
    File(romFile).forEachLine { line ->
        cpu.rom.add(line.toLong(2))
        val inst = decode(line)
        println("${loc} = ${inst}")
        cpu.instructions.add(inst)
        loc++
    }
    println("LOADED " + loc + " INSTRUCTIONS")
}


fun main(_args: Array<String>) {
    println("START")

    val cpu = CPU()
    //prog(cpu)
    loadProgram(cpu, "c:/Users/johnl/OneDrive/simplecpu/jvmtools/compiler/programs/Chip8Emulator.scc.asm.rom")

    val t = InProcessTerminal(cpu::gamepadHandler)
    t.main(_args)
    cpu.run(t::handleLine)
}

