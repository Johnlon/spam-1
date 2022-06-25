import java.awt.*
import java.awt.BorderLayout.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import javax.swing.JOptionPane.QUESTION_MESSAGE
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
import javax.swing.plaf.metal.MetalScrollBarUI
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


val RamSize = 65535
val StateSize = 100


fun main() {
//    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel")
//te    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")

    EventQueue.invokeLater(::createAndShowGUI)
}

private fun createAndShowGUI() {

    val mainframe = MainFrame("SPAM-1 Simulator")
    val mainPain = mainframe.contentPane

    mainPain.add(createMemoryView(), WEST)

    mainPain.add(object : BPanel() { init {
        add(object : BPanel() { init {
            add(
                object : BPanel() { init {
                    add(createRegisterView(), EAST)
                }
                },
                NORTH
            )
            add(createInstructionView(), CENTER)
            add(createControlView(), SOUTH)
        }
        })
    }
    }, EAST)

    mainframe.config()
}

val ByteStringsText = (0..255).map { "0x%02x (%3d)".format(it, it) }.toList().toTypedArray()

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

    val dataModel = RegisterTableModel()
    table.model = dataModel

    val tab = ScrollableJTable(table)
    tab.border = BorderFactory.createLineBorder(Color.RED)

    dataModel.names.forEachIndexed { i, w ->
        tab.table.columnModel.getColumn(i).preferredWidth = dataModel.names[i].width
    }
    val sz = tab.table.columnModel.columns.toList().sumOf { it.preferredWidth } + 20
    tab.preferredSize = Dimension(sz, 300)

    return tab
}

fun createControlView(): Component {

    var pane = JPanel()
    pane.preferredSize = Dimension(1, 100)

    pane.add(JLabel("foo"))

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
            return c
        }

    }


    val dataModel = InstructionTableModel()
    table.model = dataModel

    val tab = ScrollableJTable(table)

    dataModel.names.forEachIndexed { i, w ->
        tab.table.columnModel.getColumn(i).preferredWidth = w.width
    }
    val sz = tab.table.columnModel.columns.toList().sumOf { it.preferredWidth } + 20
    tab.preferredSize = Dimension(sz, 300)

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
            if ((model as RamTableModel).recentUpdates.contains(addr)) {
                c.background = Color.RED
            } else {
                c.background = Color.WHITE
            }
            return c
        }

    }


    val dataModel = RamTableModel()
    table.model = dataModel

    val tab = ScrollableJTable(table, MyScrollBarUI(dataModel))

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

            if (selected == null) {
                selected = oldValue
            }

            newInput = selected

            fireEditingStopped()
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
    val addr: Int=0,
    var value: Int=0,
    var prev: Int=0,
    var clk: Int=0
)

data class InstructionData(
    val clk: Int,
    val pc: Int,
    val targ: String = "RAM",
    val left: String = "REGA",
    val op: String = "A_MINUS_B_MINUS_C",
    val right: String = "IMMED",
    val setf: String = "",
    val amode: String = "DIR",
    val cond: String = "A",
    val inv: String = "",
    val carry: Int = 0,
    val zero: Int = 0,
    val overflow: Int = 0,
    val negative: Int = 0,
    val gt: Int = 0,
    val lt: Int = 0,
    val eq: Int = 0,
    val ne: Int = 0,
    val datain: Int = 0,
    val dataout: Int = 0
)
data class RegisterData(
    val clk: Int,
    val pc: Int,
    val mar: Int=65535,
    val rega: Int=255,
    val regb: Int=0,
    val regc: Int=0,
    val regd: Int=0,
    val halt: Int=0,
    val alu: Int=0
)

data class ColDef(val name: String, val width: Int, val format: String, val field: String = name)

class InstructionTableModel : AbstractTableModel() {

    var data = (0..StateSize).map { InstructionData(clk = it, pc = it) }.toMutableList()

    val names = listOf(
        ColDef("Clk", 90, "%d"),
        ColDef("PC", 70, "%d"),
        ColDef("TARG", 70, "%s"),
        ColDef("LEFT", 70, "%s"),
        ColDef("OP", 130, "%s"),
        ColDef("RIGHT", 70, "%s"),
        ColDef("SET", 30, "%s", "setf"),
        ColDef("AM", 30, "%s", "amode"),
        ColDef("?", 25, "%s", "cond"),
        ColDef("INV", 30, "%s"),
        ColDef("c", 25, "%d", "carry"),
        ColDef("z", 25, "%d", "zero"),
        ColDef("o", 25, "%d", "overflow"),
        ColDef("n", 25, "%d", "negative"),
        ColDef("G", 25, "%d", "gt"),
        ColDef("L", 25, "%d", "lt"),
        ColDef("E", 25, "%d", "eq"),
        ColDef("N", 25, "%d", "ne"),
        ColDef("DI", 25, "%d", "datain"),
        ColDef("DO", 25, "%d", "dataout")
    )

    override fun getColumnCount(): Int {
        return names.size
    }

    override fun getRowCount(): Int {
        return StateSize
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

class RamTableModel : AbstractTableModel() {
    val recentUpdates = ObservableList(mutableListOf<Int>())
    var data = (0..RamSize).map { RamData(addr = it, clk = it) }.toMutableList()

    val names = listOf(
        ColDef("Addr", 90, "0x%04d %5d"),
        ColDef("Value", 70, "0x%02d %3d"),
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


class MyScrollBarUI(val data: RamTableModel) : MetalScrollBarUI() {
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
            val pos = 1 - ((i * 1.0) / RamSize)
            g.fillRect(0, (pos * trackBounds.height).toInt(), trackBounds.width, 2)
        }
    }
}

class ScrollableJTable(val table: JTable, val scrollBarUI: MyScrollBarUI? = null) : JPanel() {

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
        add(pane, BorderLayout.CENTER)

        if (scrollBarUI != null) pane.verticalScrollBar.setUI(scrollBarUI)

        pane.horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER
    }
}

