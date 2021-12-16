import AMode.Dir
import AMode.Reg
import CInv.Inv
import CInv.Std
import Flag.Keep
import Flag.Set
import java.io.*
import java.util.*
import java.util.regex.Pattern


class CPU {
    val gamepageReader = GamepadFileReader()

    val t = InProcessTerminal()

    companion object {
        fun loadAlu(aluRom: MutableList<Int>) {
            File("c:/Users/johnl/OneDrive/simplecpu/verilog/alu/roms/alu-hex.rom").forEachLine { line ->
                val words = line.trim().split(" ")
                words.forEach { word ->
                    aluRom.add(word.toInt(16))
                }
            }
        }
    }


    constructor() {
        loadAlu(aluRom)
        gamepageReader.startTailer(false, this::gamepadHandler)
    }

    lateinit var lastOp: Op

    val aluRom = mutableListOf<Int>()

    val rom = mutableListOf<Long>(65536)
    val instructions = mutableListOf<Instruction>()

    val ram = IntArray(65536) { 0 }
    var pcLo: Int = 0
    var pcHi: Int = 0
    var pcHitmp: Int = 0

    fun pc(): Int = ((pcHi shl 8) + pcLo)
    fun incPc() {
        pcLo++
        if (pcLo > 255) {
            pcLo = 0
            pcHi++
        }
    }

    var marlo: Int = 0
    var marhi: Int = 0
    fun mar() = (marhi shl 8) + marlo

    var rega: Int = 0
    var regb: Int = 0
    var regc: Int = 0
    var regd: Int = 0

    var portsel = 0

    var flags = mutableListOf(Cond.A)

    fun hex(i: Int) = i.toString(16).padStart(2, '0')

    fun dump() {
        val code = instructions[pc()]
        //println("")
        //println("CYCLE: ${cycles} PC=${pc()} : ${code} ")
    }

    /* take pc as an arg as PC will already have been updated to the next instruction by the time we get called*/
    fun dumpState(pc: Int) {
        val code = instructions[pc]
        val ramData = if (code.amode == Reg) ram[mar()] else ram[code.address]

        /*
        print("CYCLE: ${cycles} PC=${pc.toString().padStart(5,' ')}(${pc.toString(16).padStart(2,'0')}) : ")
        val amodeChar = code.amode.toString().get(0)
        print(
            "STATE:  PC ${hex(pcHi)}:${hex(pcLo)}=${pc().toString().padEnd(5)} TMP ${hex(pcHitmp)}" +
                    "  MAR ${hex(marhi)}:${hex(marlo)} RAM [$amodeChar ${hex(ramData)}]" +
                    "  REG ${hex(rega)} ${hex(regb)} ${hex(regc)} ${hex(regd)}" +
                    "  PORT ${hex(port)} SEL ${hex(portsel)}" +
                    "  FLAGS ${flags.toString().padEnd(30)}"
        )
        println("   : ${code} ")
        */
    }

    var cycles = 0
    fun cycle() {

        cycles++
        val i: Instruction = instructions[pc()] ?: throw Error("NO INSTRUCTION AT " + pc())
        dump()

        val rnd = (Math.random() * 255).toInt()

        /*
        The 4 key is left rotate, 5 - left move, 6 - right move, 1 - drop,
        ENTER - restart, DROP - end.  After every 5 lines,
        the speed increases slightly and peaks at 45 lines.
         */
        val aval: Int = when (i.a) {
            ADev.rega -> rega
            ADev.regb -> regb
            ADev.regc -> regc
            ADev.regd -> regd
            ADev.marlo -> marlo
            ADev.marhi -> marhi
            ADev.uart -> rnd % 16 // Chip 8 keypad is 0-15 TODO - make the Chip8 use the game pad !!!!
            ADev.nu -> 0
        }
        val bval: Int = when (i.b) {
            BDev.rega -> rega
            BDev.regb -> regb
            BDev.regc -> regc
            BDev.regd -> regd
            BDev.marlo -> marlo
            BDev.marhi -> marhi
            BDev.immed -> i.immed
            BDev.ram -> if (i.amode == Dir) ram[i.address] else ram[mar()]
            BDev.not_used -> 0
            BDev.vram -> 0
            BDev.port -> {
                when (portsel) {
                    ReadPort.Random.id ->
                        rnd
                    ReadPort.Gamepad1.id ->
                        padin(1)
                    ReadPort.Gamepad2.id ->
                        TODO()
                    else -> TODO()
                }
            }
        }

        // enable UART out always - not impl yet

        var jumped = false
        val shouldInvert = (i.conditionInvert == Inv)
        val condMatch = flags.contains(i.condition)
        val doExec = if (shouldInvert) !condMatch else condMatch
        val curPc = pc()

        if (doExec) {
            val (aluVal, newFlags, effectiveOp) = alu(aluRom, i.aluOp, flags.contains(Cond.C), aval, bval)
            lastOp = effectiveOp

            if (i.setFlags == Set) {
                flags = newFlags
            }
            if (!flags.contains(Cond.DO)) flags.add(Cond.DO)
            if (!flags.contains(Cond.DI) and (rnd == 1)) flags.add(Cond.DI)

            when (i.t) {
                TDev.rega -> rega = aluVal
                TDev.regb -> regb = aluVal
                TDev.regc -> regc = aluVal
                TDev.regd -> regd = aluVal
                TDev.marlo -> marlo = aluVal
                TDev.marhi -> marhi = aluVal
                TDev.uart -> uartOut(aluVal)
                TDev.ram -> if (i.amode == Dir) ram[i.address] = aluVal else ram[mar()] = aluVal
                TDev.halt -> halt(aluVal)
                TDev.vram -> TODO()
                TDev.port -> TODO()
                TDev.portsel -> {
                    portsel = aluVal
                }
                TDev.not_used12 -> {} // no op
                TDev.pchitmp -> pcHitmp = aluVal
                TDev.pclo -> {
                    pcLo = aluVal
                    jumped = true
                }
                TDev.pc -> {
                    pcLo = aluVal
                    pcHi = pcHitmp
                    jumped = true
                }
            }

        }
        dumpState(curPc)

        if (!jumped) {
            incPc()
        }
    }

    var transmitted = 0

    val uartOutName = "C:\\Users\\johnl\\OneDrive\\simplecpu\\verilog\\cpu\\uart.out"

    val uartOutFile = run {
        File(uartOutName).delete()
        BufferedWriter(FileWriter(uartOutName))
    }

    private fun uartOut(aluVal: Int) {
        // just to slow the cpu down

        fun map(i: Int): String {

            return if (i.toChar().isISOControl())
                when (i) {
                    0 -> "nul"
                    '\t'.code -> "\\t"
                    '\n'.code -> "\\n"
                    '\r'.code -> "\\r"
                    else -> "<ctrl>"
                }
            else
                i.toChar().toString()
        }

//        println(
//            "TRANSMITTING ${
//                (transmitted++).toString().padStart(4, ' ')
//            } :  D:${aluVal}  H:${aluVal.toString(16)}   C:'${map(aluVal)}'"
//        )

        uartOutFile.write(aluVal.toString(16).padStart(2, '0') + "\n")
    }

    val gamepadData = LinkedList<String>()

    fun gamepadHandler(line: String) {
        gamepadData.add(line)
    }

    /* field 0 is 0 if nothing read , otherwise it's the controller id*/
    var lastPad1 = 0
    var lastPad2 = 0
    private fun padin(pad: Int): Int {
        val k = if (gamepadData.size>0) gamepadData.removeFirst() else null

        if (k != null) {
            val p = Pattern.compile("c([12])=(\\d+)")
            val m = p.matcher(k)
            if (m.matches()) {
                val c = m.group(1).toInt()
                val v = m.group(2).toInt(16)
                if (c == 1) lastPad1 = v
                if (c == 2) lastPad2 = v
            }
        }

        return if (pad == 1) lastPad1
        else if (pad == 2) lastPad2
        else 0
    }

    var halted = false

    /* note: runs at up to 30MHz when dump IO is disabled and jvm warmed up, approx 2MHz for short progs*/
    fun run() {
        pcHi = 0
        pcLo = 0

        halted = false
        val start = System.currentTimeMillis()
        while (!halted) {
            cycle()
        }
        val took = System.currentTimeMillis() - start
        println("RUNTIME ${took} ms")
        println("RATE ${(1000 * cycles) / (took + 1)} inst/s")
    }

    private fun halt(aluValue: Int) {
        println("==========================================")
        println("HALTING....")
        println("HALTCODE MAR ${marhi.toString(16)}:${marlo.toString(16)}  ALUCODE ${aluValue.toString(16)}")
        println("==========================================")
        halted = true
    }
}

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
}

fun decode(l: String): Instruction {

    return Instruction(
        Op.values().first { it.id == slice(l, 47, 43) },
        TDev.values().first { it.id == (slice(l, 25, 25) shl 4) + slice(l, 42, 39) },
        ADev.values().first { it.id == slice(l, 38, 36) },
        BDev.values().first { it.id == (slice(l, 26, 26) shl 3) + slice(l, 35, 33) },
        Cond.values().first { it.id == slice(l, 32, 29) },
        Flag.values().first { it.id == slice(l, 28, 28) },
        CInv.values().first { it.id == slice(l, 27, 27) },
        AMode.values().first { it.id == slice(l, 24, 24) },
        slice(l, 23, 8),
        slice(l, 7, 0),
    )
}


fun main(_args: Array<String>) {
    println("START")

    val cpu = CPU()
    loadProgram(cpu, "c:/Users/johnl/OneDrive/simplecpu/jvmtools/compiler/programs/Chip8Emulator.scc.asm.rom")
    cpu.run()
}

