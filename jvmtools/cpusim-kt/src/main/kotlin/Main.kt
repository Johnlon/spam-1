import AMode.Dir
import CInv.Inv
import CInv.Std
import Flag.Keep
import Flag.Set
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


fun loadProgram(programRomFile: String): Pair<List<Long>, List<Instruction>> {
    var loc = 0

    val rom = mutableListOf<Long>()
    val instructions = mutableListOf<Instruction>()
    File(programRomFile).forEachLine { line ->
        rom.add(line.toLong(2))
        val inst = decode(line)
        // DISASM println("${loc} = ${inst}")
        instructions.add(inst)
        loc++
    }
    println("LOADED " + loc + " INSTRUCTIONS")
    return Pair(rom, instructions)
}


fun main(_args: Array<String>) {
    println("START")

    Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread?, e: Throwable?) {
            e?.printStackTrace()
            println(e)
            System.exit(1)
        }

    })

    //val prog = File("../../jvmtools/compiler/programs/Chip8Emulator.scc.asm.rom")
    //val prog = File("c:/Users/johnl/work/simplecpu/jvmtools/compiler/programs/Chip8Emulator.scc.asm.rom")
    //val prog = File("c:/Users/johnl/work/simplecpu/jvmtools/compiler/programs/Mandelbrot.scc.asm.rom")
    var aluRomFile = File("c:/Users/johnl/work/simplecpu/verilog/alu/roms/alu-hex.rom")
    var progRomFile = File("c:/Users/johnl/work/simplecpu/jvmtools/compiler/programs/Mandelbrot.asm.rom")
    if (_args.size > 1) {
        aluRomFile = File(_args.get(0))
    }
    if (_args.size > 1) {
        progRomFile = File(_args.get(1))
    }


    println("Program : " + progRomFile.absolutePath)
    println("ALU     : " + aluRomFile.absolutePath)
    val (programRom, inst) = loadProgram(progRomFile.path)
    program.addAll(inst)

    val slow = true

    val cpu = if (slow) CPU(debugger = simUI(), aluRomFile = aluRomFile, instructions = inst)
    else CPU(aluRomFile = aluRomFile, instructions = inst)

    val showGraphics = false
    if (showGraphics) {
        val t = InProcessTerminal(cpu::gamepadHandler)
        t.main(_args)
        cpu.run(t::handleLine)
    } else {
        var lastS = ""
        var lastC = ' '
        val rendering: String = System.getenv().getOrDefault("rendering", Rendering.Encoded.name);

        val r = Rendering.valueOf(rendering)

        cpu.run { s: String ->

            val ci = Integer.parseInt(s, 16)
            val c = ci.toChar()

            when (r) {
                Rendering.Encoded -> {
                    if (lastS.isEmpty()) {
                        lastS = s
                        lastC = c
                    } else {
                        if (lastC == 'h') { // hex
                            print(s)
                        } else if (lastC == 'c') {
                            print(c)
                        } else if (lastC == 'd') {
                            print(ci) // decimal
                        } else if (lastC == 'a') { // ansi
                            val colour = mapColour(ci)
                            print(String.format("\u001b[48;05;%dm%02x\u001b[0m", colour, ci))
                        } else if (lastC == 'A') { // not ansi cos it's mapped
                            val colour = mapColour(ci)
                            print(String.format("\u001b[48;05;%dm  \u001b[0m", colour)) // ensu nonum
                        } else {
                            println("\nprotocol - unexpected ctrl code '0x$lastS' ($lastC) prior to '0x$s' ($c)\n")
                        }
                        lastS = ""
                    }
                }
                Rendering.Numeric ->
                    if (s == "ff") {
                        print("\n")
                    } else {
                        print(" " + s)
                    }
            }
            System.out.flush()
        }
    }

}

fun mapColour(ci: Int): Int {

    val colours = listOf(
    0, 15, 88,   80,
    127, 28, 19,   226,
    208, 94, 167, 237,
    8, 39, 0,  0
    )

    return colours.get(ci)
}

enum class Rendering {
    Numeric,
    Encoded
}
