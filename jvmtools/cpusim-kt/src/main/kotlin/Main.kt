import AMode.Dir
import CInv.Inv
import CInv.Std
import Flag.Keep
import Flag.Set
import java.awt.EventQueue
import java.io.File


fun prog(cpu: CPU) {
    assemble(
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


fun loadProgram(romFile: String) : Pair<List<Long>, List<Instruction>> {
    var loc = 0

    val rom = mutableListOf<Long>()
    val instructions = mutableListOf<Instruction>()
    File(romFile).forEachLine { line ->
        rom.add(line.toLong(2))
        val inst = decode(line)
        println("${loc} = ${inst}")
        instructions.add(inst)
        loc++
    }
    println("LOADED " + loc + " INSTRUCTIONS")
    return Pair(rom, instructions)
}


fun main(_args: Array<String>) {
    println("START")

    Thread.setDefaultUncaughtExceptionHandler(object: Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread?, e: Throwable?) {
            e?.printStackTrace()
            println(e)
            System.exit(1)
        }

    })

    //prog(cpu)
//    val (rom, inst) =loadProgram("c:/Users/johnl/OneDrive/simplecpu/jvmtools/compiler/programs/Chip8Emulator.scc.asm.rom")
//    val prog = File("../../jvmtools/compiler/programs/Chip8Emulator.scc.asm.rom")
    val prog = File("C:\\Users\\johnl\\work\\simplecpu\\jvmtools\\compiler\\build\\spammcc-test.rom")
    println("Program : " + prog.absolutePath)
    val (rom, inst) =loadProgram(prog.path)
    program.addAll(inst)

    main()

    while (debugger.get() == null) {
        Thread.sleep(100)
    }

    val cpu = CPU(debugger = debugger.get(), rom = rom, instructions = inst)

    val t = InProcessTerminal(cpu::gamepadHandler)
    t.main(_args)

    cpu.run(t::handleLine)
}

