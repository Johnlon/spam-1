import java.awt.*
import java.awt.BorderLayout.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.swing.*
import javax.swing.JOptionPane.QUESTION_MESSAGE
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.plaf.metal.MetalScrollBarUI
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


val RamSize = 65535
val StateSize = 100

val progModel = InstructionTableModel()
val regModel = RegisterTableModel()
val ramModel = RamTableModel()

var registerView: ScrollableJTable? = null
var progView: ScrollableJTable? = null
var controllerView: Component? = null

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
    progView = createInstructionView()
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
            add(progView, CENTER)
            add(controllerView, SOUTH)
        }
        })
    }
    }, EAST)

    mainframe.config()

    val dbg = object : Debugger {
        override fun onDebug(code: InstructionExec, commit: () -> Unit) {
            commit.invoke()
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
            progView?.repaint()
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

    val table = JTable()

    table.model = regModel

    val tab = ScrollableJTable(table)
    tab.border = BorderFactory.createLineBorder(Color.RED)

    regModel.names.forEachIndexed { i, w ->
        tab.table.columnModel.getColumn(i).preferredWidth = regModel.names[i].width
    }
    val sz = tab.table.columnModel.columns.toList().sumOf { it.preferredWidth } + 20
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

    layout.setHorizontalGroup(
        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addComponent(asmLabel).addComponent(asmText))
            .addGroup(
                layout.createSequentialGroup().addComponent(nextBtn).addComponent(runBtn).addGap(30)
                    .addComponent(labelStep).addComponent(textStep).addGap(30)
                    .addComponent(labelBrkClk).addComponent(textBrkClk).addGap(30)
                    .addComponent(labelBrkPC).addComponent(textBrkPC).addGap(200)
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
            )
    )

    pane.preferredSize = Dimension(1, 100)


    return pane
}

fun createInstructionView(): ScrollableJTable {

    val table = object : JTable() {
        override fun prepareRenderer(
            renderer: TableCellRenderer?,
            row: Int,
            column: Int
        ): Component? {

            val c = super.prepareRenderer(
                renderer,
                row, column
            )

            setValueAt(123, 1, 2)
            return c
        }
    }


    table.model = progModel

    val tab = ScrollableJTable(table)

    progModel.names.forEachIndexed { i, w ->
        tab.table.columnModel.getColumn(i).preferredWidth = w.width
    }
    val sz = tab.table.columnModel.columns.toList().sumOf { it.preferredWidth } + 20
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
            notifyObservers()
            return true
        }
        return false
    }

}

data class RamData(
    val addr: Int = 0,
    var value: Int = 0,
    var prev: Int = 0,
    var clk: Int = 0
)

data class InstructionDecode(
    val pc: Int,
    val targ: String = "RAM",
    val left: String = "REGA",
    val op: String = "A_MINUS_B_MINUS_C",
    val right: String = "IMMED",
    val setf: String = "",
    val amode: String = "DIR",
    val cond: String = "A",
    val inv: String = "",
)

data class Flags(
    val carry: Boolean,
    val zero: Boolean,
    val overflow: Boolean,
    val negative: Boolean,
    val gt: Boolean,
    val lt: Boolean,
    val eq: Boolean,
    val ne: Boolean,
    val datain: Boolean,
    val dataout: Boolean
)

data class InstructionExec(
    val clk: Int,
    val pc: Int,
    val instruction: Instruction,
    val alu: Int,
    val aval: Int,
    val bval: Int,
    val doExec: Boolean = true,
    val regIn: Registers,
    val flagsIn: List<Cond>
)

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

data class RegisterData(
    val clk: Int,
    val pc: Int,
    val mar: Int = 65535,
    val rega: Int = 255,
    val regb: Int = 0,
    val regc: Int = 0,
    val regd: Int = 0,
    val halt: Int = 0,
    val alu: Int = 0
)

data class Registers(
    val marhi: Int = 0,
    val marlo: Int = 0,
    val rega: Int = 255,
    val regb: Int = 0,
    val regc: Int = 0,
    val regd: Int = 0,
    val portSel: Int = 0,
    val timer1: Int = 0,
    val uartOut: Int = 0,
    val pchitmp: Int = 0,
    val pchi: Int = 0,
    val pclo: Int = 0,
    val halt: Int = 0,
    val alu: Int = 0
)

data class ColDef(val name: String, val width: Int, val format: String, val field: String = name)

class InstructionTableModel : AbstractTableModel() {

    //var data = (0..StateSize).map { InstructionData(clk = it, pc = it) }.toMutableList()
    var data = mutableListOf<InstructionData>()

    fun add(inst: InstructionData) {
        data.add(inst)
        fireTableDataChanged()
    }

    val names = listOf(
        ColDef("Clk", 90, "%d"),
        ColDef("PC", 70, "%d"),
        ColDef("Targ", 70, "%s"),
        ColDef("Left", 70, "%s"),
        ColDef("Operation", 130, "%s", "op"),
        ColDef("Right", 70, "%s"),
        ColDef("SetF", 40, "%s"),
        ColDef("aMode", 40, "%s", "amode"),
        ColDef("Cond",  40, "%s"),
        ColDef("Inv", 30, "%s"),
        ColDef("Flags", 100, "%s")
    )

    override fun getColumnCount(): Int {
        return names.size
    }

    override fun getRowCount(): Int {
        return data.size
    }

    override fun getValueAt(row: Int, col: Int): Any {
        val pos = data.size - row - 1

        val cd = names[col]
        val i = data[pos]
        val po = InstructionData::class.members.filter { it.name.lowercase() == cd.field.lowercase() }
        if (po.isEmpty()) {
            error("field " + cd.name + " not found")
        }
        val v = po.first().call(i)

        return cd.format.format(v, v)
    }

    override fun getColumnName(column: Int): String {
        return names[column].name
    }
}

class RegisterTableModel : AbstractTableModel() {
    var data = (0..StateSize).map { RegisterData(clk = it, pc = it) }.toMutableList()

    val names = listOf(
        ColDef("Clk", 90, "%d"),
        ColDef("PC", 70, "%d"),
        ColDef("MAR", 90, "0x%04x %5d"),
        ColDef("REGA", 60, "0x%02x %3d"),
        ColDef("REGB", 60, "0x%02x %3d"),
        ColDef("REGC", 60, "0x%02x %3d"),
        ColDef("REGD", 60, "0x%02x %3d"),
        ColDef("HALT", 60, "0x%02x %3d"),
        ColDef("ALU", 60, "0x%02x %3d"),
    )

    override fun getColumnCount(): Int {
        return names.size
    }

    override fun getRowCount(): Int {
        return data.size
    }

    override fun getValueAt(row: Int, col: Int): Any {
        val pos = data.size - row - 1

        val cd = names[col]
        val i = data[pos]
        val po = RegisterData::class.members.filter { it.name.lowercase() == cd.field.lowercase() }
        if (po.isEmpty()) {
            error("field " + cd.name + " not found")
        }
        val v = po.first().call(i)

        return cd.format.format(v, v)
    }

    override fun getColumnName(column: Int): String {
        return names[column].name
    }
}

interface TableRendering {
    fun render(col: Int, row: Int): String
}

class RamTableModel : AbstractTableModel() {
    val recentUpdates = ObservableList(mutableListOf<Int>())
    var data = (0..RamSize).map { RamData(addr = it, clk = it) }.toMutableList()

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
        val pos = data.size - row - 1

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
        val addr = RamSize - rowIndex

        if (columnIndex == 1) {
            data[addr].prev = data[addr].value

            val update = aValue?.toString()?.toInt() ?: data[addr].value
            data[addr].value = update

            recentUpdates.add(addr)
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

