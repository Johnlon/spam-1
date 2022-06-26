import java.awt.*
import java.awt.BorderLayout.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import javax.swing.*
import javax.swing.JOptionPane.QUESTION_MESSAGE
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.plaf.metal.MetalScrollBarUI
import javax.swing.table.*

val RamSize = 65535

val ramModel = RamTableModel()
val program = mutableListOf<Instruction>()

var registerView: ScrollableJTable? = null
var currentInstView: JTable? = null
var progView: ScrollableJTable? = null
var controllerView: Component? = null

fun mkExecModel(): DefaultTableModel {
    val dtm = DefaultTableModel()
    dtm.addColumn("data")
    return dtm
}

val execModel = mkExecModel()

val regModel = RegModel(execModel)

fun addExec(exec: InstructionExec) {
    execModel.addRow(arrayOf(exec))
}

fun currentExec(): InstructionExec {
    return getExec(execModel.rowCount - 1)
}

fun getExec(row: Int): InstructionExec {
    try {
        if (execModel.rowCount > 0) {
            val r = execModel.getValueAt(row, 0);
            return r as InstructionExec
        }
        return InstructionExec(
            clk = 0,
            pc = 0,
            alu = 0,
            aval = 0,
            bval = 0,
            doExec = true,
            regIn = Registers(clk = 0, pc = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            flagsIn = emptyList(),
            flagsOut = emptyList(),
            effectiveOp = Op.A
        )
    } catch (ex: Exception) {
        throw ex
    }
}

interface ColWidth {
    fun getWidth(col: Int): Int
}

val debugger = AtomicReference<Debugger>()

fun main() {
//    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel")
//te    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")

    EventQueue.invokeAndWait(::createAndShowGUI)
}

private fun createAndShowGUI() {

    val mainframe = MainFrame("SPAM-1 Simulator")
    val mainPain = mainframe.contentPane

    val menuBar = JMenuBar()
    mainframe.jMenuBar = menuBar
    val menuFile = JMenu("File");
    val menuEdit = JMenu("Edit");
    menuBar.add(menuFile)
    menuBar.add(menuEdit)

    val menuLoad = JMenuItem("Load..")
    val menuSave = JMenuItem("Save..")
    menuFile.add(menuLoad)
    menuFile.add(menuSave)

    mainPain.add(createMemoryView(), WEST)

    registerView = createRegisterView()
    progView = createProgView()
    currentInstView = createCurrentInstView()
    controllerView = createControlView()

    mainPain.add(object : BPanel() { init {
        add(object : BPanel() { init {
            add(
                object : BPanel() { init {
                    add(registerView, EAST)
                }
                },
                NORTH
            )
            add(
                object : BPanel() { init {
                    add(currentInstView, NORTH)
                    add(progView, CENTER)
                }
                },
                CENTER
            )
            add(controllerView, SOUTH)
        }
        })
    }
    }, EAST)

    mainframe.config()

    val dbg = object : Debugger {
        override fun instructions(code: List<Instruction>) {
            program.addAll(code)
        }

        override fun onDebug(code: InstructionExec, commit: () -> Unit) {
            addExecState(code)
            progView?.repaint()

            commit.invoke()

            /*
            progModel.add(
                InstructionData(
                    clk = code.clk,
                    pc = code.pc,
                    doExec = code.doExec,
                    targ = "%-7s".format(code.instruction.t.name) +  " " + "%3d".format(code.alu),
                    left = "%-7s".format(code.instruction.a.name) +  " " + "%3d".format(code.aval),
                    right = "%-7s".format(code.instruction.b.name) +  " " + "%3d".format(code.bval),
                    op = code.instruction.aluOp.name,
                    setf = code.instruction.setFlags.name,
                    amode = code.instruction.amode.name,
                    cond = code.instruction.condition.name,
                    inv = code.instruction.conditionInvert.name,
                    flags = code.flagsIn.map { it.name }.joinToString(" ")
                )
            )
            */
        }

        override fun observeRam(ram: ObservableList<Int>) {
            ram.addObserver(object: Observer {
                override fun update(o: Observable?, arg: Any?) {
                    val idx = arg as Int
                    val ramVal = ram.get(idx)
                    val viewPos = RamSize - idx - 1
                    ramModel.setValueAt(ramVal, viewPos, 1) // col 1 is the ram value
                }
            } )
        }

    }
    mainframe.repaint()

    debugger.set(dbg)
}

val ByteStringsText = (0..255).map { "0x%02x %3d".format(it, it) }.toList().toTypedArray()

abstract class BPanel : JPanel() {
    init {
        layout = BorderLayout()
    }
}

class MainFrame(title: String) : JFrame() {

    init {
        createUI(title)
    }

    private fun createUI(title: String) {

        setTitle(title)

        setLocationRelativeTo(null)

        layout = BorderLayout()

    }

    fun config() {

        val w = components.sumOf { it.preferredSize.width }
        val h = components.maxOf { it.preferredSize.height }

        size = Dimension(w, h)
        positionCentrally(this)
        pack();
        isVisible = true
    }
}


fun createRegisterView(): ScrollableJTable {

    val table = JTable(regModel)
    execModel.addTableModelListener(table)

    val tab = ScrollableJTable(table)
    tab.border = BorderFactory.createLineBorder(Color.RED)

    regModel.cols.forEachIndexed { i, w ->
        tab.table.columnModel.getColumn(i).preferredWidth = regModel.getWidth(i)
    }
    val sz = 20 + tab.table.columnModel.columns.toList().sumOf { it.preferredWidth }
    tab.preferredSize = Dimension(sz, 300)

    return tab
}

fun createControlView(): Component {

    var pane = JPanel()
    val layout = GroupLayout(pane)
    pane.layout = layout

    layout.setAutoCreateGaps(true)
    layout.setAutoCreateContainerGaps(true)

    val asmLabel = JLabel("ASM")
    asmLabel.font = asmLabel.font.deriveFont(20.0f).deriveFont(Font.PLAIN)

    val asmText = JTextField("REGA = REGA A_MINUS_B_MINUS_C 23 _C_S !")
    asmText.font = Font(Font.MONOSPACED, Font.PLAIN, 20)
    asmText.preferredSize = Dimension(300, 20)
    asmText.isEditable = false

    val nextBtn = JButton("Next")
    val runBtn = JButton("Run")

    val labelStep = JLabel("Advance Clocks")
    val textStep = JTextField("10")
    textStep.maximumSize = Dimension(50, 30)

    val labelBrkClk = JLabel("Break Clk")
    val textBrkClk = JTextField("10")
    textBrkClk.maximumSize = Dimension(70, 30)

    val labelBrkPC = JLabel("Break PC")
    val textBrkPC = JTextField("10")
    textBrkPC.maximumSize = Dimension(70, 30)

    val resetRecentUpdates = JButton("Reset Recent")
    resetRecentUpdates.addActionListener { a -> ramModel.recentUpdates.clear() }

    layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addComponent(asmLabel).addComponent(asmText))
            .addGroup(
                layout.createSequentialGroup().addComponent(nextBtn).addComponent(runBtn).addGap(30)
                    .addComponent(labelStep).addComponent(textStep).addGap(30)
                    .addComponent(labelBrkClk).addComponent(textBrkClk).addGap(30)
                    .addComponent(labelBrkPC).addComponent(textBrkPC).addGap(30)
                    .addComponent(resetRecentUpdates).addGap(100)
            )
    )

    layout.setVerticalGroup(
        layout.createSequentialGroup()
            .addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(asmLabel).addComponent(asmText)
            )
            .addGroup(
                layout.createParallelGroup().addComponent(nextBtn).addComponent(runBtn)
                    .addComponent(labelStep).addComponent(textStep)
                    .addComponent(labelBrkClk).addComponent(textBrkClk)
                    .addComponent(labelBrkPC).addComponent(textBrkPC)
                    .addComponent(resetRecentUpdates)
            )
    )

    pane.preferredSize = Dimension(1, 100)


    return pane
}

val instNames = listOf(
    ColDef("PC", 70, "%d"),
    ColDef("Targ", 70, "%s", "t"),
    ColDef("Left", 70, "%s"),
    ColDef("Operation", 110, "%s", "aluop"),
    ColDef("Right", 70, "%s", "b"),
    ColDef("SetF", 40, "%s", "setflags"),
    ColDef("aMode", 50, "%s", "amode"),
    ColDef("Cond", 40, "%s", "condition"),
    ColDef("Inv", 30, "%s", "conditioninvert"),
    ColDef("Immed", 70, "<html>0x%02x<br/>%3d</html>")
)

fun createCurrentInstView(): JTable {

    val progModel = object : AbstractTableModel() {

        override fun getColumnCount(): Int {
            return instNames.size
        }

        override fun getRowCount(): Int {
            return 1
        }

        override fun getValueAt(row: Int, col: Int): Any {

            val currentExec = currentExec()
            val pc = currentExec.pc
            val i = program[pc]

            val cd = instNames[col]
            val nameLower = cd.name.lowercase()

            return when (nameLower) {
                "pc" -> {
                    pc.toString()
                }
                "targ" -> {
                    "<html>%s<br/>0x%02x</html>".format(i.t.name, currentExec.alu)
                }
                "left" -> {
                    "<html>%s<br/>0x%02x</html>".format(i.a.name, currentExec.aval)
                }
                "right" -> {
                    "<html>%s<br/>0x%02x</html>".format(i.b.name, currentExec.bval)
                }
                "operation" -> {
                    if (i.aluOp != currentExec.effectiveOp && row == 0) "<html>%s<br/>%s</html>".format(
                        i.aluOp.name,
                        currentExec.effectiveOp
                    )
                    else i.aluOp.name
                }
                "setf" -> {
                    i.setFlags.name
                }
                "amode" -> {
                    i.amode.name
                }
                "cond" -> {
                    i.condition.name
                }
                "inv" -> {
                    i.conditionInvert.name
                }
                "immed" -> {
                    "0x%02x".format(i.immed)
                }
                else -> {
                    "bad column " + nameLower
                }
            }
        }

        override fun getColumnName(column: Int): String {
            return instNames[column].name
        }
    }

    val table = JTable(progModel)
    table.font = Font(Font.MONOSPACED, Font.PLAIN, 10)
    execModel.addTableModelListener(table)

    instNames.forEachIndexed { i, w ->
        table.columnModel.getColumn(i).preferredWidth = w.width
    }
    table.columnModel.getColumn(instNames.size - 1).preferredWidth += 20 // stretch the last col where the scroll bar would have been
    val sz = table.columnModel.columns.toList().sumOf { it.preferredWidth }
    table.preferredSize = Dimension(sz, 35)

    table.setRowHeight(35);//Try set height to 15 (I've tried higher)

    table.background = Color(200, 255, 200)
    return table
}

fun createProgView(): ScrollableJTable {

    val progModel = object : AbstractTableModel() {

        override fun getColumnCount(): Int {
            return instNames.size
        }

        override fun getRowCount(): Int {
            return program.size
        }

        override fun getValueAt(row: Int, col: Int): Any {
            val pos = program.size - row - 1
            val i = program[pos]

            val cd = instNames[col]
            val nameLower = cd.name.lowercase()

            return when (nameLower) {
                "pc" -> {
                    row.toString()
                }
                "targ" -> {
                    i.t.name
                }
                "left" -> {
                    i.a.name
                }
                "right" -> {
                    i.b.name
                }
                "operation" -> {
                    i.aluOp.name
                }
                "setf" -> {
                    i.setFlags.name
                }
                "amode" -> {
                    i.amode.name
                }
                "cond" -> {
                    i.condition.name
                }
                "inv" -> {
                    i.conditionInvert.name
                }
                "immed" -> {
                    "0x%02x".format(i.immed)
                }
                else -> {
                    "bad column " + nameLower
                }
            }
        }

        override fun getColumnName(column: Int): String {
            return instNames[column].name
        }
    }

    val table = JTable(progModel)
    val tab = ScrollableJTable(table)

    instNames.forEachIndexed { i, w ->
        table.columnModel.getColumn(i).preferredWidth = w.width + 20
    }
    table.columnModel.getColumn(instNames.size - 1).preferredWidth += 20 // stretch the last col where the scroll bar would have been
    val sz = table.columnModel.columns.toList().sumOf { it.preferredWidth }
    tab.preferredSize = Dimension(sz, 250)

    return tab
}

fun createMemoryView(): ScrollableJTable {

    val table = object : JTable() {
        override fun prepareRenderer(
            renderer: TableCellRenderer?,
            row: Int,
            column: Int
        ): Component? {

            val addr = RamSize - row

            val c = super.prepareRenderer(
                renderer,
                row, column
            )

            val shouldTint = getColumnName(column) == "Value"

            if (shouldTint && (model as RamTableModel).recentUpdates.contains(addr)) {
                c.background = Color.RED
            } else {
                c.background = Color.WHITE
            }
            return c
        }

    }

    table.model = ramModel

    val tab = ScrollableJTable(table, RamScrollBar(ramModel))

    tab.table.columnModel.getColumn(0).preferredWidth = 90
    tab.table.columnModel.getColumn(1).preferredWidth = 70
    tab.table.columnModel.getColumn(2).preferredWidth = 70
    tab.table.columnModel.getColumn(3).preferredWidth = 100

    tab.table.columnModel.getColumn(1).cellEditor = DialogByteEditor("Value")

    val sz = tab.table.columnModel.columns.toList().sumOf { it.preferredWidth }
    tab.preferredSize = Dimension(sz, 700)
    return tab
}


private fun positionCentrally(frame: MainFrame) {
// Determine the new location of the window
    val dim: Dimension = Toolkit.getDefaultToolkit().getScreenSize()

// Determine the new location of the window
    val w: Int = frame.getSize().width
    val h: Int = frame.getSize().height
    val x = (dim.width - w) / 2
    val y = (dim.height - h) / 2

    frame.setLocation(x, y);

}

class DialogByteEditor(val title: String) : AbstractCellEditor(), TableCellEditor, ActionListener {
    var newInput: Int = 0
    var oldValue: Int = 0
    var button: JButton


    init {
        button = JButton()
        button.background = Color.WHITE
        button.actionCommand = EDIT
        button.addActionListener(this)
        button.isBorderPainted = false
    }

    override fun actionPerformed(e: ActionEvent) {
        if (EDIT == e.actionCommand) {

            var selected = JOptionPane.showInputDialog(
                null,
                "Edit",
                title,
                QUESTION_MESSAGE,
                null,
                (0..255).toList().toTypedArray(),
                oldValue
            ) as Int?

            if (selected == -1) {
                selected = oldValue
            } else if (selected == null) {
                selected = oldValue
            }

            newInput = selected
            if (newInput != oldValue) fireEditingStopped()
            else fireEditingCanceled()
        }
    }

    override fun getCellEditorValue(): Any {
        return newInput!!
    }

    override fun getTableCellEditorComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        row: Int,
        column: Int
    ): Component {
        newInput = ByteStringsText.indexOf(value)
        oldValue = ByteStringsText.indexOf(value)
        return button
    }

    companion object {
        const val EDIT = "edit"
    }
}

class ObservableList<T>(val wrapped: MutableList<T>) : MutableList<T> by wrapped, Observable() {
    override fun add(element: T): Boolean {
        if (wrapped.add(element)) {
            setChanged()
            notifyObservers(wrapped.size-1)
            return true
        }
        return false
    }

    override fun set(index: Int, element: T): T {
        var ret: T = wrapped.set(index, element)
        setChanged()
        notifyObservers(index)
        return ret
    }
}

data class RamData(
    val addr: Int = 0,
    var value: Int = 0,
    var prev: Int = 0,
    var clk: Int = 0
)

data class InstructionExec(
    val clk: Int,
    val pc: Int,
    val alu: Int,
    val aval: Int,
    val bval: Int,
    val doExec: Boolean,
    val regIn: Registers,
    val flagsIn: List<Cond>,
    val flagsOut: List<Cond>,
    val effectiveOp: Op
)


/*
data class InstructionData(
    val clk: Int,
    val pc: Int,
    val doExec: Boolean,
    val targ: String,
    val left: String,
    val op: String,
    val right: String,
    val setf: String,
    val amode: String,
    val cond: String,
    val inv: String,
    val flags: String
)
 */

data class Registers(
    val clk: Int,
    val pc: Int,
    val pchitmp: Int,
    val pchi: Int,
    val pclo: Int,
    val marhi: Int,
    val marlo: Int,
    val rega: Int,
    val regb: Int,
    val regc: Int,
    val regd: Int,
    val portSel: Int,
    val timer1: Int,
    val halt: Int,
    val alu: Int
)

data class ColDef(val name: String, val width: Int, val format: String, val field: String = name)

/*
class InstructionTableModel : AbstractTableModel() {

    override fun getColumnCount(): Int {
        return instNames.size
    }

    override fun getRowCount(): Int {
        return program.size
    }

    override fun getValueAt(row: Int, col: Int): Any {
        val pos = program.size - row - 1

        val cd = instNames[col]
        if (cd.name.lowercase() == "pc") {
            return row
        }

        val i = program[pos]
        val po = Instruction::class.members.filter { it.name.lowercase() == cd.field.lowercase() }
        if (po.isEmpty()) {
            System.err.println("field " + cd.name + " not found")
            System.exit(1)
            error("field " + cd.name + " not found")
        }
        val v = po.first().call(i)

        if (cd.name.lowercase() == "cond") {
            if (v == Cond.A) {
                return ""
            }
        }
        if (cd.name.lowercase() == "inv") {
            if (v == CInv.Std) {
                return ""
            }
        }
        return cd.format.format(v, v)
    }

    override fun getColumnName(column: Int): String {
        return instNames[column].name
    }
}
 */

fun fmt2(i: Int) = "0x%02x %3d".format(i, i)

class RegModel(val execMode: TableModel) : AbstractTableModel(), ColWidth, TableModelListener {

    init {
        execModel.addTableModelListener(this)
    }

    val cols = listOf(
        ColDef("clk", 90, "%d"),
        ColDef("pc", 70, "%d"),
        ColDef("pchitmp", 70, "%d"),
        ColDef("mar", 90, "0x%04x %5d"),
        ColDef("rega", 60, "0x%02x %3d"),
        ColDef("regb", 60, "0x%02x %3d"),
        ColDef("regc", 60, "0x%02x %3d"),
        ColDef("regd", 60, "0x%02x %3d"),
        ColDef("halt", 60, "0x%02x %3d"),
        ColDef("portsel", 60, "0x%02x %3d"),
        ColDef("halt", 60, "0x%02x %3d"),
        ColDef("alu", 60, "0x%02x %3d"),
    )

    override fun getColumnCount() = cols.size

    override fun getColumnName(column: Int) = cols[column].name

    override fun getRowCount() = execModel.rowCount

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val colName = getColumnName(columnIndex).lowercase()
        val pos = execModel.rowCount - rowIndex - 1
        val data = getExec(pos)

        // Registers

        return when (colName) {
            "clk" -> {
                data.clk
            }
            "pc" -> {
                data.pc
                val mar = (data.regIn.pchi * 256) + data.regIn.pclo
                "0x%04x %5d".format(mar, mar)
            }
            "pchitmp" -> {
                fmt2(data.regIn.pchitmp)
            }
            "mar" -> {
                val mar = (data.regIn.marhi * 256) + data.regIn.marlo
                "0x%04x %5d".format(mar, mar)
            }
            "rega" -> {
                fmt2(data.regIn.rega)
            }
            "regb" -> {
                fmt2(data.regIn.regb)
            }
            "regc" -> {
                fmt2(data.regIn.regc)
            }
            "regd" -> {
                fmt2(data.regIn.regd)
            }
            "portSel" -> {
                fmt2(data.regIn.portSel)
            }
            "timer1" -> {
                fmt2(data.regIn.timer1)
            }
            "halt" -> {
                fmt2(data.regIn.halt)
            }
            "alu" -> {
                fmt2(data.regIn.alu)
            }
            else -> {
                "bad column " + colName
            }
        }
    }

    override fun getWidth(col: Int): Int = cols[col].width

    override fun tableChanged(e: TableModelEvent?) {
        fireTableDataChanged()
    }
}

class RamTableModel : AbstractTableModel() {
    val recentUpdates = ObservableList(CopyOnWriteArrayList<Int>())
    val data = (0 .. RamSize).map { RamData(addr = it, clk = it) }.reversed().toMutableList()

    val names = listOf(
        ColDef("Addr", 90, "0x%04d %5d"),
        ColDef("Value", 70, "0x%02x %3d"),
        ColDef("Prev", 90, "0x%02x %3d"),
        ColDef("Clk", 90, "%d")
    )

    override fun getColumnCount(): Int {
        return names.size
    }

    override fun getRowCount(): Int {
        return data.size
    }

    override fun getValueAt(row: Int, col: Int): Any {
        val pos = row // read with reverse order to write

        val cd = names[col]
        val i = data[pos]
        val po = RamData::class.members.filter { it.name.lowercase() == cd.field.lowercase() }
        if (po.isEmpty()) {
            error("field " + cd.name + " not found")
        }
        val v = po.first().call(i)

        return cd.format.format(v, v)
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val pos = data.size - rowIndex - 1 // write with reverse order to read

        if (columnIndex == 1) {
            data[pos].prev = data[pos].value

            val update = aValue?.toString()?.toInt() ?: data[pos].value
            data[pos].value = update

            recentUpdates.add(pos)
            this.fireTableCellUpdated(rowIndex, columnIndex)
        }
    }


    override fun getColumnName(column: Int): String {
        return names[column].name
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return columnIndex == 1
    }
}


class RamScrollBar(val data: RamTableModel) : MetalScrollBarUI() {
    val self = this

    val obs = object : Observer {
        override fun update(o: Observable?, arg: Any?) {
            self.scrollbar.repaint()
        }
    }

    init {
        data.recentUpdates.addObserver(obs)
    }

    override fun paintTrack(g: Graphics, c: JComponent, trackBounds: Rectangle) {

        super.paintTrack(g, c, trackBounds)
        g.color = Color.RED

        for (i in data.recentUpdates.wrapped) {
            val pos = 1 - ((i * 1.0) / data.data.size)
            g.fillRect(0, trackBounds.y + (pos * trackBounds.height).toInt() - (5 / 2), trackBounds.width, 5)
        }
    }
}

class ScrollableJTable(val table: JTable, val scrollBarUI: RamScrollBar? = null) : JPanel() {

    init {
        initializeUI()
    }

    private fun initializeUI() {
        layout = BorderLayout()
        preferredSize = Dimension(400, 500)

        table.font = Font(Font.MONOSPACED, Font.PLAIN, 10)

        // Turn off JTable's auto resize so that JScrollPane will show a horizontal scroll bar.
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        val pane = JScrollPane(table)
        add(pane, CENTER)

        if (scrollBarUI != null) pane.verticalScrollBar.setUI(scrollBarUI)

        pane.horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER
    }
}


fun addExecState(state: InstructionExec) {
    addExec(state)
    currentInstView?.repaint()
    registerView?.repaint()

    progView?.table?.getSelectionModel()?.setSelectionInterval(state.pc, state.pc)
    progView?.table?.scrollRectToVisible(Rectangle(progView?.table?.getCellRect(state.pc, 0, true)));
}