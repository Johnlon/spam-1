import java.io.File
import java.util.regex.Pattern
import kotlin.concurrent.thread

interface Debugger {
    fun instructions(code: List<Instruction>): Unit
    fun onDebug(code: InstructionExec, commit: () -> Unit): Unit
    fun observeRam(ram: ObservableList<Int>)
}

object NoOpDebugger : Debugger {
    override fun instructions(code: List<Instruction>) {
    }

    override fun onDebug(code: InstructionExec, commit: () -> Unit): Unit {
        commit.invoke()
    }

    override fun observeRam(ram: ObservableList<Int>) {
    }
}

fun loadAlu(aluRom: MutableList<Int>) {
    val romFile = File("../../../simplecpu/verilog/alu/roms/alu-hex.rom")
    println("ALU rom: " + romFile.absolutePath)
    romFile.forEachLine { line ->
        val words = line.trim().split(" ")
        words.forEach { word ->
            aluRom.add(word.toInt(16))
        }
    }
}

class CPU(
    val debugger: Debugger = NoOpDebugger,
    val rom: List<Long>,
    val instructions: List<Instruction>,
) {

    @Volatile
    var timer1: Int = 0
    val countFreq = 50
    val countIntervalMs = ((1.0 / countFreq) * 1000).toLong()

    private val aluRom = mutableListOf<Int>()


    lateinit var lastOp: Op

    val ram = ObservableList((0..65535).map { x -> 0 }.toMutableList())
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

    var haltVal: Int = 0
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
        if (1 == 1) {
            val code = instructions[pc()]
            println("CYCLE: ${cycles} PC=${pc()} : ${code} ")
        }
    }

    /* take pc as an arg as PC will already have been updated to the next instruction by the time we get called*/
    fun dumpState(pc: Int) {
        val code = instructions[pc]
        //val ramData = if (code.amode == Reg) ram[mar()] else ram[code.address]

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

    // re instruction rate: https://github.com/ajor/chip8
    val freq = 1000 * 1000
    val intervalNs: Long = (1000000000 * (1.0 / freq)).toLong()
//    var intervalNs : Long = 1 // approx same as 600 instructions per second

    init {
        loadAlu(aluRom)
        debugger.observeRam(ram)

        thread(isDaemon = true, start = true) {
            while (true) {
                Thread.sleep(countIntervalMs)
                if (timer1 > 0) {
                    timer1--
                }
            }
        }
    }

    fun cycle(terminalHandler: (String) -> Unit) {

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
            BDev.ram ->
                if (i.amode == AMode.Dir)
                    ram[i.address]
                else
                    ram[mar()]
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
                    ReadPort.Timer1.id ->
                        timer1
                    ReadPort.Timer2.id ->
                        TODO()
                    else -> TODO()
                }
            }
        }

        var jumped = false
        val shouldInvert = (i.conditionInvert == CInv.Inv)
        val condMatch = flags.contains(i.condition)
        val doExec = if (shouldInvert) !condMatch else condMatch

        val curPc = pc()
        val (aluVal, newFlags, effectiveOp) = alu(aluRom, i.aluOp, flags.contains(Cond.C), aval, bval)

        var code = InstructionExec(
            clk = cycles, pc = curPc, doExec = doExec,
            aval = aval, bval = bval, alu = aluVal,
            regIn = Registers(
                clk = cycles,
                pc = pc(),
                pchitmp = pcHitmp,
                portSel = portsel,
                timer1 = timer1,
                marhi = marhi,
                marlo = marlo,
                rega = rega,
                regb = regb,
                regc = regc,
                regd = regd,
                pchi = pcHi,
                pclo = pcLo,
                halt = haltVal,
                alu = aluVal
            ),
            flagsIn = flags,
            flagsOut = newFlags,
            effectiveOp = effectiveOp
        )

        debugger.onDebug(code) {

            if (doExec) {
//                val (aluVal, newFlags, effectiveOp) = alu(aluRom, i.aluOp, flags.contains(Cond.C), aval, bval)

                lastOp = effectiveOp

                if (i.setFlags == Flag.Set) {
                    flags = newFlags
                }

                // TODO - for now always set data out enabled
                if (!flags.contains(Cond.DO)) flags.add(Cond.DO)

                // TODO - for now randomly set date in available
                if (!flags.contains(Cond.DI) and (rnd == 1)) flags.add(Cond.DI)

                when (i.t) {
                    TDev.rega -> rega = aluVal
                    TDev.regb -> regb = aluVal
                    TDev.regc -> regc = aluVal
                    TDev.regd -> regd = aluVal
                    TDev.marlo -> marlo = aluVal
                    TDev.marhi -> marhi = aluVal
                    TDev.uart -> uartOut(aluVal, terminalHandler)
                    TDev.ram -> {
                        if (i.amode == AMode.Dir)
                            ram[i.address] = aluVal
                        else
                            ram[mar()] = aluVal
                    }
                    TDev.halt -> halt(aluVal)
                    TDev.vram -> TODO()
                    TDev.port -> {
                        when (portsel) {
                            WritePort.Timer1.id ->
                                timer1 = aluVal
                            WritePort.Timer2.id ->
                                TODO()
                            WritePort.Parallel.id ->
                                TODO()
                            else -> TODO()
                        }
                    }
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
        }
        dumpState(curPc)

        if (!jumped) {
            incPc()
        }

        // slow down
        delay()
    }


    var lastTime = System.nanoTime()
    fun delay() {
        val now = System.nanoTime()
        val remaining = intervalNs - (now - lastTime)
        nanosleep(remaining)
        lastTime = now
    }


    private fun uartOut(aluVal: Int, terminalHandler: (String) -> Unit) {

        val terminalOut: String = aluVal.toString(16).padStart(2, '0')
        terminalHandler(terminalOut)
    }

    val gamepadData = mutableListOf<String>()

    fun gamepadHandler(line: String) {
        synchronized(gamepadData) {
            gamepadData.add(line)
        }
    }


    fun nanosleep(i: Long) {
        val start = System.nanoTime()
        var end: Long
        do {
            end = System.nanoTime()
        } while (start + i >= end)
    }

    /* field 0 is 0 if nothing read , otherwise it's the controller id*/
    var lastPad1 = 0
    var lastPad2 = 0
    private fun padin(pad: Int): Int {
        val k = synchronized(gamepadData) {
            if (gamepadData.size > 0) gamepadData.removeFirst() else null
        }

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
    fun run(terminalHandler: (String) -> Unit) {
        pcHi = 0
        pcLo = 0

        halted = false
        val start = System.currentTimeMillis()
        while (!halted) {
            cycle(terminalHandler)
            if (cycles % 1000000 == 0) printRate(start)
            Thread.sleep(1)
        }
        printRate(start)
    }

    private fun printRate(start: Long) {
        val took = System.currentTimeMillis() - start
        println("RUNTIME ${took} ms")
        println("RATE ${(1000L * cycles) / (took + 1)} inst/s")
    }

    private fun halt(aluValue: Int) {
        haltVal = aluValue
        println("==========================================")
        println("HALTING....")
        println("HALTCODE MAR ${marhi.toString(16)}:${marlo.toString(16)}  ALUCODE ${aluValue.toString(16)}")
        println("==========================================")
        halted = true
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

fun disasm(i: Instruction): String {

    val btxt = when (i.b) {
        BDev.immed -> "0x%02x(%d)".format(i.immed, i.immed)
        BDev.ram -> if (i.amode == AMode.Dir)
            "[0x%04x(%d)]".format(i.address, i.address)
        else
            "[MAR]"
        else -> i.b.name
    }

    val ttxt = when (i.t) {
        TDev.ram -> if (i.amode == AMode.Dir)
            "[0x%04x(%d)]".format(i.address, i.address)
        else
            "[MAR]"
        else -> i.t.name
    }

    val op = when (i.aluOp) {
        Op.B -> ttxt + " = " + btxt
        Op.A -> ttxt + " = " + i.a.name
        else -> ttxt + " = " + i.a.name + " " + i.aluOp.name + " " + btxt
    }

    val inv = if (i.conditionInvert == CInv.Inv) "!" else ""
    val set = if (i.setFlags == Flag.Set) "_S" else ""

    return op + " " + inv + " " + i.condition + " " + set
}
