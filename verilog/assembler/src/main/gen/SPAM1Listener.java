// Generated from C:/Users/johnl/OneDrive/simplecpu/verilog/assembler/src/main/antlr\SPAM1.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SPAM1Parser}.
 */
public interface SPAM1Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#compile}.
	 * @param ctx the parse tree
	 */
	void enterCompile(SPAM1Parser.CompileContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#compile}.
	 * @param ctx the parse tree
	 */
	void exitCompile(SPAM1Parser.CompileContext ctx);
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
	 * Enter a parse tree produced by {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(SPAM1Parser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(SPAM1Parser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#instruction_passleft}.
	 * @param ctx the parse tree
	 */
	void enterInstruction_passleft(SPAM1Parser.Instruction_passleftContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#instruction_passleft}.
	 * @param ctx the parse tree
	 */
	void exitInstruction_passleft(SPAM1Parser.Instruction_passleftContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#instruction_passright}.
	 * @param ctx the parse tree
	 */
	void enterInstruction_passright(SPAM1Parser.Instruction_passrightContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#instruction_passright}.
	 * @param ctx the parse tree
	 */
	void exitInstruction_passright(SPAM1Parser.Instruction_passrightContext ctx);
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
	 * Enter a parse tree produced by {@link SPAM1Parser#comment}.
	 * @param ctx the parse tree
	 */
	void enterComment(SPAM1Parser.CommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#comment}.
	 * @param ctx the parse tree
	 */
	void exitComment(SPAM1Parser.CommentContext ctx);
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
	 * Enter a parse tree produced by {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void enterBdev(SPAM1Parser.BdevContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 */
	void exitBdev(SPAM1Parser.BdevContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 */
	void enterTarget(SPAM1Parser.TargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 */
	void exitTarget(SPAM1Parser.TargetContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#immed}.
	 * @param ctx the parse tree
	 */
	void enterImmed(SPAM1Parser.ImmedContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#immed}.
	 * @param ctx the parse tree
	 */
	void exitImmed(SPAM1Parser.ImmedContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPAM1Parser#ram_direct}.
	 * @param ctx the parse tree
	 */
	void enterRam_direct(SPAM1Parser.Ram_directContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPAM1Parser#ram_direct}.
	 * @param ctx the parse tree
	 */
	void exitRam_direct(SPAM1Parser.Ram_directContext ctx);
}