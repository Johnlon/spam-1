// Generated from C:/Users/johnl/OneDrive/simplecpu/verilog/assembler/src/main/antlr\SPAM1.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SPAM1Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SPAM1Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(SPAM1Parser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(SPAM1Parser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#labeledCommand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabeledCommand(SPAM1Parser.LabeledCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EquInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquInstruction(SPAM1Parser.EquInstructionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignABInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignABInstruction(SPAM1Parser.AssignABInstructionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignAInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignAInstruction(SPAM1Parser.AssignAInstructionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignBInstruction}
	 * labeled alternative in {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignBInstruction(SPAM1Parser.AssignBInstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#adev}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdev(SPAM1Parser.AdevContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BDevDevice}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBDevDevice(SPAM1Parser.BDevDeviceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BDevRAMDirect}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBDevRAMDirect(SPAM1Parser.BDevRAMDirectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BDevExpr}
	 * labeled alternative in {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBDevExpr(SPAM1Parser.BDevExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BDevOnlyRAMRegister}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBDevOnlyRAMRegister(SPAM1Parser.BDevOnlyRAMRegisterContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BDevOnlyRAMDirect}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBDevOnlyRAMDirect(SPAM1Parser.BDevOnlyRAMDirectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BDevOnlyExpr}
	 * labeled alternative in {@link SPAM1Parser#bdevOnly}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBDevOnlyExpr(SPAM1Parser.BDevOnlyExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#bdevDevices}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBdevDevices(SPAM1Parser.BdevDevicesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TargetDevice}
	 * labeled alternative in {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTargetDevice(SPAM1Parser.TargetDeviceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TargetRamDirect}
	 * labeled alternative in {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTargetRamDirect(SPAM1Parser.TargetRamDirectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PC}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPC(SPAM1Parser.PCContext ctx);
	/**
	 * Visit a parse tree produced by the {@code HiHyte}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHiHyte(SPAM1Parser.HiHyteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(SPAM1Parser.VarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Num}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNum(SPAM1Parser.NumContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parents}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParents(SPAM1Parser.ParentsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LoByte}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoByte(SPAM1Parser.LoByteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Times}
	 * labeled alternative in {@link SPAM1Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimes(SPAM1Parser.TimesContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(SPAM1Parser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#ramDirect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRamDirect(SPAM1Parser.RamDirectContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SPAM1Parser.NumberContext ctx);
}