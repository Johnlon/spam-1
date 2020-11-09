// Generated from C:/Users/johnl/OneDrive/simplecpu/verilog/assembler/src/main/antlr\SPAM1.g4 by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SPAM1Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, ALUOP=11, FLAG=12, PCHITMP=13, PCLO=14, PC=15, RAM=16, REGA=17, 
		REGB=18, REGC=19, REGD=20, MARLO=21, MARHI=22, UART=23, NU=24, LABEL=25, 
		NAME=26, STRING=27, INT=28, HEX=29, OCT=30, BIN=31, CHAR=32, WS=33, EOL=34, 
		COMMENT=35;
	public static final int
		RULE_prog = 0, RULE_line = 1, RULE_labeledCommand = 2, RULE_instruction = 3, 
		RULE_adev = 4, RULE_bdev = 5, RULE_bdevOnly = 6, RULE_bdevDevices = 7, 
		RULE_target = 8, RULE_expr = 9, RULE_label = 10, RULE_ramDirect = 11, 
		RULE_number = 12;
	private static String[] makeRuleNames() {
		return new String[] {
			"prog", "line", "labeledCommand", "instruction", "adev", "bdev", "bdevOnly", 
			"bdevDevices", "target", "expr", "label", "ramDirect", "number"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'EQU'", "'='", "'**'", "'+'", "'('", "')'", "'<'", "'>'", "'['", 
			"']'", null, "''S'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, "ALUOP", 
			"FLAG", "PCHITMP", "PCLO", "PC", "RAM", "REGA", "REGB", "REGC", "REGD", 
			"MARLO", "MARHI", "UART", "NU", "LABEL", "NAME", "STRING", "INT", "HEX", 
			"OCT", "BIN", "CHAR", "WS", "EOL", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SPAM1.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SPAM1Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgContext extends ParserRuleContext {
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__8) | (1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << RAM) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU) | (1L << LABEL) | (1L << EOL))) != 0)) {
				{
				{
				setState(26);
				line();
				}
				}
				setState(31);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TerminalNode EOL() { return getToken(SPAM1Parser.EOL, 0); }
		public LabeledCommandContext labeledCommand() {
			return getRuleContext(LabeledCommandContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		int _la;
		try {
			setState(37);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__8) | (1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << RAM) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU) | (1L << LABEL))) != 0)) {
					{
					setState(32);
					labeledCommand();
					}
				}

				setState(35);
				match(EOL);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(36);
				match(EOL);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabeledCommandContext extends ParserRuleContext {
		public InstructionContext instruction() {
			return getRuleContext(InstructionContext.class,0);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public LabeledCommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledCommand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterLabeledCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitLabeledCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitLabeledCommand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabeledCommandContext labeledCommand() throws RecognitionException {
		LabeledCommandContext _localctx = new LabeledCommandContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_labeledCommand);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL) {
				{
				setState(39);
				label();
				}
			}

			setState(42);
			instruction();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InstructionContext extends ParserRuleContext {
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
	 
		public InstructionContext() { }
		public void copyFrom(InstructionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EquInstructionContext extends InstructionContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public EquInstructionContext(InstructionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterEquInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitEquInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitEquInstruction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignABInstructionContext extends InstructionContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public AdevContext adev() {
			return getRuleContext(AdevContext.class,0);
		}
		public TerminalNode ALUOP() { return getToken(SPAM1Parser.ALUOP, 0); }
		public BdevContext bdev() {
			return getRuleContext(BdevContext.class,0);
		}
		public TerminalNode FLAG() { return getToken(SPAM1Parser.FLAG, 0); }
		public AssignABInstructionContext(InstructionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterAssignABInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitAssignABInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitAssignABInstruction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignAInstructionContext extends InstructionContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public AdevContext adev() {
			return getRuleContext(AdevContext.class,0);
		}
		public TerminalNode FLAG() { return getToken(SPAM1Parser.FLAG, 0); }
		public AssignAInstructionContext(InstructionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterAssignAInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitAssignAInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitAssignAInstruction(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignBInstructionContext extends InstructionContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public BdevOnlyContext bdevOnly() {
			return getRuleContext(BdevOnlyContext.class,0);
		}
		public TerminalNode FLAG() { return getToken(SPAM1Parser.FLAG, 0); }
		public AssignBInstructionContext(InstructionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterAssignBInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitAssignBInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitAssignBInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_instruction);
		int _la;
		try {
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new EquInstructionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				match(T__0);
				setState(45);
				expr(0);
				}
				break;
			case 2:
				_localctx = new AssignABInstructionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(46);
				target();
				setState(47);
				match(T__1);
				setState(48);
				adev();
				setState(49);
				match(ALUOP);
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(50);
					match(FLAG);
					}
				}

				setState(53);
				bdev();
				}
				break;
			case 3:
				_localctx = new AssignAInstructionContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(55);
				target();
				setState(56);
				match(T__1);
				setState(57);
				adev();
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(58);
					match(FLAG);
					}
				}

				}
				break;
			case 4:
				_localctx = new AssignBInstructionContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(61);
				target();
				setState(62);
				match(T__1);
				setState(63);
				bdevOnly();
				setState(65);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(64);
					match(FLAG);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AdevContext extends ParserRuleContext {
		public TerminalNode REGA() { return getToken(SPAM1Parser.REGA, 0); }
		public TerminalNode REGB() { return getToken(SPAM1Parser.REGB, 0); }
		public TerminalNode REGC() { return getToken(SPAM1Parser.REGC, 0); }
		public TerminalNode REGD() { return getToken(SPAM1Parser.REGD, 0); }
		public TerminalNode MARLO() { return getToken(SPAM1Parser.MARLO, 0); }
		public TerminalNode MARHI() { return getToken(SPAM1Parser.MARHI, 0); }
		public TerminalNode UART() { return getToken(SPAM1Parser.UART, 0); }
		public TerminalNode NU() { return getToken(SPAM1Parser.NU, 0); }
		public AdevContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_adev; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterAdev(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitAdev(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitAdev(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdevContext adev() throws RecognitionException {
		AdevContext _localctx = new AdevContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_adev);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BdevContext extends ParserRuleContext {
		public BdevContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bdev; }
	 
		public BdevContext() { }
		public void copyFrom(BdevContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BDevExprContext extends BdevContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public BDevExprContext(BdevContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBDevExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBDevExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBDevExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BDevDeviceContext extends BdevContext {
		public BdevDevicesContext bdevDevices() {
			return getRuleContext(BdevDevicesContext.class,0);
		}
		public BDevDeviceContext(BdevContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBDevDevice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBDevDevice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBDevDevice(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BDevRAMDirectContext extends BdevContext {
		public RamDirectContext ramDirect() {
			return getRuleContext(RamDirectContext.class,0);
		}
		public BDevRAMDirectContext(BdevContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBDevRAMDirect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBDevRAMDirect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBDevRAMDirect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BdevContext bdev() throws RecognitionException {
		BdevContext _localctx = new BdevContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_bdev);
		try {
			setState(74);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RAM:
			case REGA:
			case REGB:
			case REGC:
			case REGD:
			case MARLO:
			case MARHI:
			case NU:
				_localctx = new BDevDeviceContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(71);
				bdevDevices();
				}
				break;
			case T__8:
				_localctx = new BDevRAMDirectContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(72);
				ramDirect();
				}
				break;
			case T__2:
			case T__4:
			case T__6:
			case T__7:
			case NAME:
			case INT:
			case HEX:
			case OCT:
			case BIN:
				_localctx = new BDevExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(73);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BdevOnlyContext extends ParserRuleContext {
		public BdevOnlyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bdevOnly; }
	 
		public BdevOnlyContext() { }
		public void copyFrom(BdevOnlyContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BDevOnlyRAMDirectContext extends BdevOnlyContext {
		public RamDirectContext ramDirect() {
			return getRuleContext(RamDirectContext.class,0);
		}
		public BDevOnlyRAMDirectContext(BdevOnlyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBDevOnlyRAMDirect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBDevOnlyRAMDirect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBDevOnlyRAMDirect(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BDevOnlyRAMRegisterContext extends BdevOnlyContext {
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public BDevOnlyRAMRegisterContext(BdevOnlyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBDevOnlyRAMRegister(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBDevOnlyRAMRegister(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBDevOnlyRAMRegister(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BDevOnlyExprContext extends BdevOnlyContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public BDevOnlyExprContext(BdevOnlyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBDevOnlyExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBDevOnlyExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBDevOnlyExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BdevOnlyContext bdevOnly() throws RecognitionException {
		BdevOnlyContext _localctx = new BdevOnlyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_bdevOnly);
		try {
			setState(79);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RAM:
				_localctx = new BDevOnlyRAMRegisterContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(76);
				match(RAM);
				}
				break;
			case T__8:
				_localctx = new BDevOnlyRAMDirectContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(77);
				ramDirect();
				}
				break;
			case T__2:
			case T__4:
			case T__6:
			case T__7:
			case NAME:
			case INT:
			case HEX:
			case OCT:
			case BIN:
				_localctx = new BDevOnlyExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(78);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BdevDevicesContext extends ParserRuleContext {
		public TerminalNode REGA() { return getToken(SPAM1Parser.REGA, 0); }
		public TerminalNode REGB() { return getToken(SPAM1Parser.REGB, 0); }
		public TerminalNode REGC() { return getToken(SPAM1Parser.REGC, 0); }
		public TerminalNode REGD() { return getToken(SPAM1Parser.REGD, 0); }
		public TerminalNode MARLO() { return getToken(SPAM1Parser.MARLO, 0); }
		public TerminalNode MARHI() { return getToken(SPAM1Parser.MARHI, 0); }
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public TerminalNode NU() { return getToken(SPAM1Parser.NU, 0); }
		public BdevDevicesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bdevDevices; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBdevDevices(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBdevDevices(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBdevDevices(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BdevDevicesContext bdevDevices() throws RecognitionException {
		BdevDevicesContext _localctx = new BdevDevicesContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_bdevDevices);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RAM) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << NU))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TargetContext extends ParserRuleContext {
		public TargetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_target; }
	 
		public TargetContext() { }
		public void copyFrom(TargetContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TargetRamDirectContext extends TargetContext {
		public RamDirectContext ramDirect() {
			return getRuleContext(RamDirectContext.class,0);
		}
		public TargetRamDirectContext(TargetContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterTargetRamDirect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitTargetRamDirect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitTargetRamDirect(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TargetDeviceContext extends TargetContext {
		public TerminalNode REGA() { return getToken(SPAM1Parser.REGA, 0); }
		public TerminalNode REGB() { return getToken(SPAM1Parser.REGB, 0); }
		public TerminalNode REGC() { return getToken(SPAM1Parser.REGC, 0); }
		public TerminalNode REGD() { return getToken(SPAM1Parser.REGD, 0); }
		public TerminalNode MARLO() { return getToken(SPAM1Parser.MARLO, 0); }
		public TerminalNode MARHI() { return getToken(SPAM1Parser.MARHI, 0); }
		public TerminalNode UART() { return getToken(SPAM1Parser.UART, 0); }
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public TerminalNode PC() { return getToken(SPAM1Parser.PC, 0); }
		public TerminalNode PCLO() { return getToken(SPAM1Parser.PCLO, 0); }
		public TerminalNode PCHITMP() { return getToken(SPAM1Parser.PCHITMP, 0); }
		public TerminalNode NU() { return getToken(SPAM1Parser.NU, 0); }
		public TargetDeviceContext(TargetContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterTargetDevice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitTargetDevice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitTargetDevice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TargetContext target() throws RecognitionException {
		TargetContext _localctx = new TargetContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_target);
		int _la;
		try {
			setState(85);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PCHITMP:
			case PCLO:
			case PC:
			case RAM:
			case REGA:
			case REGB:
			case REGC:
			case REGD:
			case MARLO:
			case MARHI:
			case UART:
			case NU:
				_localctx = new TargetDeviceContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(83);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << RAM) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case T__8:
				_localctx = new TargetRamDirectContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(84);
				ramDirect();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PCContext extends ExprContext {
		public PCContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterPC(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitPC(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitPC(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class HiHyteContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public HiHyteContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterHiHyte(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitHiHyte(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitHiHyte(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarContext extends ExprContext {
		public TerminalNode NAME() { return getToken(SPAM1Parser.NAME, 0); }
		public VarContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumContext extends ExprContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public NumContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterNum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitNum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitNum(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParentsContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParentsContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterParents(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitParents(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitParents(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LoByteContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public LoByteContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterLoByte(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitLoByte(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitLoByte(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimesContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TimesContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterTimes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitTimes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitTimes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case HEX:
			case OCT:
			case BIN:
				{
				_localctx = new NumContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(88);
				number();
				}
				break;
			case T__2:
				{
				_localctx = new PCContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(89);
				match(T__2);
				}
				break;
			case NAME:
				{
				_localctx = new VarContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(90);
				match(NAME);
				}
				break;
			case T__4:
				{
				_localctx = new ParentsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(91);
				match(T__4);
				setState(92);
				expr(0);
				setState(93);
				match(T__5);
				}
				break;
			case T__6:
				{
				_localctx = new LoByteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(95);
				match(T__6);
				setState(96);
				expr(2);
				}
				break;
			case T__7:
				{
				_localctx = new HiHyteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(97);
				match(T__7);
				setState(98);
				expr(1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TimesContext(new ExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(101);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(102);
					match(T__3);
					setState(103);
					expr(5);
					}
					} 
				}
				setState(108);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode LABEL() { return getToken(SPAM1Parser.LABEL, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(LABEL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RamDirectContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public RamDirectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ramDirect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterRamDirect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitRamDirect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitRamDirect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RamDirectContext ramDirect() throws RecognitionException {
		RamDirectContext _localctx = new RamDirectContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ramDirect);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(T__8);
			setState(112);
			number();
			setState(113);
			match(T__9);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode HEX() { return getToken(SPAM1Parser.HEX, 0); }
		public TerminalNode OCT() { return getToken(SPAM1Parser.OCT, 0); }
		public TerminalNode BIN() { return getToken(SPAM1Parser.BIN, 0); }
		public TerminalNode INT() { return getToken(SPAM1Parser.INT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << HEX) | (1L << OCT) | (1L << BIN))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 9:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%x\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f"+
		"\t\f\4\r\t\r\4\16\t\16\3\2\7\2\36\n\2\f\2\16\2!\13\2\3\3\5\3$\n\3\3\3"+
		"\3\3\5\3(\n\3\3\4\5\4+\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\66"+
		"\n\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5>\n\5\3\5\3\5\3\5\3\5\5\5D\n\5\5\5F\n"+
		"\5\3\6\3\6\3\7\3\7\3\7\5\7M\n\7\3\b\3\b\3\b\5\bR\n\b\3\t\3\t\3\n\3\n\5"+
		"\nX\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5"+
		"\13f\n\13\3\13\3\13\3\13\7\13k\n\13\f\13\16\13n\13\13\3\f\3\f\3\r\3\r"+
		"\3\r\3\r\3\16\3\16\3\16\2\3\24\17\2\4\6\b\n\f\16\20\22\24\26\30\32\2\6"+
		"\3\2\23\32\4\2\22\30\32\32\3\2\17\32\3\2\36!\2\177\2\37\3\2\2\2\4\'\3"+
		"\2\2\2\6*\3\2\2\2\bE\3\2\2\2\nG\3\2\2\2\fL\3\2\2\2\16Q\3\2\2\2\20S\3\2"+
		"\2\2\22W\3\2\2\2\24e\3\2\2\2\26o\3\2\2\2\30q\3\2\2\2\32u\3\2\2\2\34\36"+
		"\5\4\3\2\35\34\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2\37 \3\2\2\2 \3\3\2\2"+
		"\2!\37\3\2\2\2\"$\5\6\4\2#\"\3\2\2\2#$\3\2\2\2$%\3\2\2\2%(\7$\2\2&(\7"+
		"$\2\2\'#\3\2\2\2\'&\3\2\2\2(\5\3\2\2\2)+\5\26\f\2*)\3\2\2\2*+\3\2\2\2"+
		"+,\3\2\2\2,-\5\b\5\2-\7\3\2\2\2./\7\3\2\2/F\5\24\13\2\60\61\5\22\n\2\61"+
		"\62\7\4\2\2\62\63\5\n\6\2\63\65\7\r\2\2\64\66\7\16\2\2\65\64\3\2\2\2\65"+
		"\66\3\2\2\2\66\67\3\2\2\2\678\5\f\7\28F\3\2\2\29:\5\22\n\2:;\7\4\2\2;"+
		"=\5\n\6\2<>\7\16\2\2=<\3\2\2\2=>\3\2\2\2>F\3\2\2\2?@\5\22\n\2@A\7\4\2"+
		"\2AC\5\16\b\2BD\7\16\2\2CB\3\2\2\2CD\3\2\2\2DF\3\2\2\2E.\3\2\2\2E\60\3"+
		"\2\2\2E9\3\2\2\2E?\3\2\2\2F\t\3\2\2\2GH\t\2\2\2H\13\3\2\2\2IM\5\20\t\2"+
		"JM\5\30\r\2KM\5\24\13\2LI\3\2\2\2LJ\3\2\2\2LK\3\2\2\2M\r\3\2\2\2NR\7\22"+
		"\2\2OR\5\30\r\2PR\5\24\13\2QN\3\2\2\2QO\3\2\2\2QP\3\2\2\2R\17\3\2\2\2"+
		"ST\t\3\2\2T\21\3\2\2\2UX\t\4\2\2VX\5\30\r\2WU\3\2\2\2WV\3\2\2\2X\23\3"+
		"\2\2\2YZ\b\13\1\2Zf\5\32\16\2[f\7\5\2\2\\f\7\34\2\2]^\7\7\2\2^_\5\24\13"+
		"\2_`\7\b\2\2`f\3\2\2\2ab\7\t\2\2bf\5\24\13\4cd\7\n\2\2df\5\24\13\3eY\3"+
		"\2\2\2e[\3\2\2\2e\\\3\2\2\2e]\3\2\2\2ea\3\2\2\2ec\3\2\2\2fl\3\2\2\2gh"+
		"\f\6\2\2hi\7\6\2\2ik\5\24\13\7jg\3\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2"+
		"m\25\3\2\2\2nl\3\2\2\2op\7\33\2\2p\27\3\2\2\2qr\7\13\2\2rs\5\32\16\2s"+
		"t\7\f\2\2t\31\3\2\2\2uv\t\5\2\2v\33\3\2\2\2\17\37#\'*\65=CELQWel";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}