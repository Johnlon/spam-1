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
	 * Visit a parse tree produced by {@link SPAM1Parser#compile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompile(SPAM1Parser.CompileContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(SPAM1Parser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(SPAM1Parser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#instruction_passleft}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction_passleft(SPAM1Parser.Instruction_passleftContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#instruction_passright}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction_passright(SPAM1Parser.Instruction_passrightContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(SPAM1Parser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#comment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComment(SPAM1Parser.CommentContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#adev}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdev(SPAM1Parser.AdevContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#bdev}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBdev(SPAM1Parser.BdevContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#target}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget(SPAM1Parser.TargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#immed}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImmed(SPAM1Parser.ImmedContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPAM1Parser#ram_direct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRam_direct(SPAM1Parser.Ram_directContext ctx);
}