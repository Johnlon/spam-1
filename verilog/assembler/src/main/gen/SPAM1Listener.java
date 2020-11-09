// Generated from C:/Users/johnl/OneDrive/simplecpu/verilog/assembler/src/main/antlr\SPAM1.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SPAM1Parser}.
 */
public interface SPAM1Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(SPAM1Parser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(SPAM1Parser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(SPAM1Parser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(SPAM1Parser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#labeledCommand}.
	 * @param ctx the parse tree
	 */
	void enterLabeledCommand(SPAM1Parser.LabeledCommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#labeledCommand}.
	 * @param ctx the parse tree
	 */
	void exitLabeledCommand(SPAM1Parser.LabeledCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EquInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterEquInstruction(SPAM1Parser.EquInstructionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EquInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitEquInstruction(SPAM1Parser.EquInstructionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignABInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterAssignABInstruction(SPAM1Parser.AssignABInstructionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignABInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitAssignABInstruction(SPAM1Parser.AssignABInstructionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignAInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterAssignAInstruction(SPAM1Parser.AssignAInstructionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignAInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitAssignAInstruction(SPAM1Parser.AssignAInstructionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignBInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterAssignBInstruction(SPAM1Parser.AssignBInstructionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignBInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitAssignBInstruction(SPAM1Parser.AssignBInstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#adev}.
	 * @param ctx the parse tree
	 */
	void enterAdev(SPAM1Parser.AdevContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#adev}.
	 * @param ctx the parse tree
	 */
	void exitAdev(SPAM1Parser.AdevContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BDevDevice}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void enterBDevDevice(SPAM1Parser.BDevDeviceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BDevDevice}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void exitBDevDevice(SPAM1Parser.BDevDeviceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BDevRAMDirect}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void enterBDevRAMDirect(SPAM1Parser.BDevRAMDirectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BDevRAMDirect}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void exitBDevRAMDirect(SPAM1Parser.BDevRAMDirectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BDevExpr}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void enterBDevExpr(SPAM1Parser.BDevExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BDevExpr}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void exitBDevExpr(SPAM1Parser.BDevExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BDevOnlyRAMRegister}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 */
	void enterBDevOnlyRAMRegister(SPAM1Parser.BDevOnlyRAMRegisterContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BDevOnlyRAMRegister}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 */
	void exitBDevOnlyRAMRegister(SPAM1Parser.BDevOnlyRAMRegisterContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BDevOnlyRAMDirect}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 */
	void enterBDevOnlyRAMDirect(SPAM1Parser.BDevOnlyRAMDirectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BDevOnlyRAMDirect}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 */
	void exitBDevOnlyRAMDirect(SPAM1Parser.BDevOnlyRAMDirectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BDevOnlyExpr}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 */
	void enterBDevOnlyExpr(SPAM1Parser.BDevOnlyExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BDevOnlyExpr}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 */
	void exitBDevOnlyExpr(SPAM1Parser.BDevOnlyExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#bdevDevices}.
	 * @param ctx the parse tree
	 */
	void enterBdevDevices(SPAM1Parser.BdevDevicesContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#bdevDevices}.
	 * @param ctx the parse tree
	 */
	void exitBdevDevices(SPAM1Parser.BdevDevicesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TargetDevice}
	 * labeled alternative in {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 */
	void enterTargetDevice(SPAM1Parser.TargetDeviceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TargetDevice}
	 * labeled alternative in {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 */
	void exitTargetDevice(SPAM1Parser.TargetDeviceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TargetRamDirect}
	 * labeled alternative in {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 */
	void enterTargetRamDirect(SPAM1Parser.TargetRamDirectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TargetRamDirect}
	 * labeled alternative in {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 */
	void exitTargetRamDirect(SPAM1Parser.TargetRamDirectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PC}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPC(SPAM1Parser.PCContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PC}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPC(SPAM1Parser.PCContext ctx);
	/**
	 * Enter a parse tree produced by the {@code HiHyte}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterHiHyte(SPAM1Parser.HiHyteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HiHyte}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitHiHyte(SPAM1Parser.HiHyteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Var}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVar(SPAM1Parser.VarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVar(SPAM1Parser.VarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Num}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNum(SPAM1Parser.NumContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Num}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNum(SPAM1Parser.NumContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parents}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParents(SPAM1Parser.ParentsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parents}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParents(SPAM1Parser.ParentsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LoByte}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLoByte(SPAM1Parser.LoByteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LoByte}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLoByte(SPAM1Parser.LoByteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Times}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTimes(SPAM1Parser.TimesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Times}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTimes(SPAM1Parser.TimesContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(SPAM1Parser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(SPAM1Parser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#ramDirect}.
	 * @param ctx the parse tree
	 */
	void enterRamDirect(SPAM1Parser.RamDirectContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#ramDirect}.
	 * @param ctx the parse tree
	 */
	void exitRamDirect(SPAM1Parser.RamDirectContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(SPAM1Parser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(SPAM1Parser.NumberContext ctx);
}