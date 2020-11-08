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
		T__0=1, OP=2, SETFLAGS=3, ALUOP=4, ALUOPS=5, PCHITMP=6, PCLO=7, PC=8, 
		RAM=9, REGA=10, REGB=11, REGC=12, REGD=13, MARLO=14, MARHI=15, UART=16, 
		NU=17, LABEL=18, NAME=19, IMMED_DEC=20, IMMED_HEX=21, IMMED_KONST=22, 
		IMMED_LABEL=23, RAM_ADDR_DEC=24, RAM_ADDR_HEX=25, RAM_ADDR_LABEL=26, RAM_ADDR_KONST=27, 
		WS=28, EOL=29, COMMENT=30, STRING=31;
	public static final int
		RULE_compile = 0, RULE_line = 1, RULE_instruction = 2, RULE_instruction_passleft = 3, 
		RULE_instruction_passright = 4, RULE_label = 5, RULE_comment = 6, RULE_adev = 7, 
		RULE_bdev = 8, RULE_target = 9, RULE_immed = 10, RULE_ram_direct = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"compile", "line", "instruction", "instruction_passleft", "instruction_passright", 
			"label", "comment", "adev", "bdev", "target", "immed", "ram_direct"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "OP", "SETFLAGS", "ALUOP", "ALUOPS", "PCHITMP", "PCLO", "PC", 
			"RAM", "REGA", "REGB", "REGC", "REGD", "MARLO", "MARHI", "UART", "NU", 
			"LABEL", "NAME", "IMMED_DEC", "IMMED_HEX", "IMMED_KONST", "IMMED_LABEL", 
			"RAM_ADDR_DEC", "RAM_ADDR_HEX", "RAM_ADDR_LABEL", "RAM_ADDR_KONST", "WS", 
			"EOL", "COMMENT", "STRING"
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

	public static class CompileContext extends ParserRuleContext {
		public List<TerminalNode> EOL() { return getTokens(SPAM1Parser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(SPAM1Parser.EOL, i);
		}
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public CompileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterCompile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitCompile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitCompile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompileContext compile() throws RecognitionException {
		CompileContext _localctx = new CompileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(25);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU) | (1L << COMMENT))) != 0)) {
					{
					setState(24);
					line();
					}
				}

				setState(27);
				match(EOL);
				}
				}
				setState(30); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU) | (1L << EOL) | (1L << COMMENT))) != 0) );
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
		public InstructionContext instruction() {
			return getRuleContext(InstructionContext.class,0);
		}
		public Instruction_passleftContext instruction_passleft() {
			return getRuleContext(Instruction_passleftContext.class,0);
		}
		public Instruction_passrightContext instruction_passright() {
			return getRuleContext(Instruction_passrightContext.class,0);
		}
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
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
		try {
			setState(36);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(32);
				instruction();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(33);
				instruction_passleft();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(34);
				instruction_passright();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(35);
				comment();
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

	public static class InstructionContext extends ParserRuleContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public AdevContext adev() {
			return getRuleContext(AdevContext.class,0);
		}
		public TerminalNode OP() { return getToken(SPAM1Parser.OP, 0); }
		public BdevContext bdev() {
			return getRuleContext(BdevContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
		}
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_instruction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			target();
			setState(42);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(39);
				match(WS);
				}
				}
				setState(44);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(45);
			match(T__0);
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(46);
				match(WS);
				}
				}
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(52);
			adev();
			setState(54); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(53);
				match(WS);
				}
				}
				setState(56); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(58);
			match(OP);
			setState(60); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(59);
				match(WS);
				}
				}
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(64);
			bdev();
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(65);
				match(WS);
				}
				}
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(71);
				comment();
				}
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

	public static class Instruction_passleftContext extends ParserRuleContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public AdevContext adev() {
			return getRuleContext(AdevContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
		}
		public TerminalNode SETFLAGS() { return getToken(SPAM1Parser.SETFLAGS, 0); }
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public Instruction_passleftContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction_passleft; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterInstruction_passleft(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitInstruction_passleft(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitInstruction_passleft(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Instruction_passleftContext instruction_passleft() throws RecognitionException {
		Instruction_passleftContext _localctx = new Instruction_passleftContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_instruction_passleft);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			target();
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(75);
				match(WS);
				}
				}
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			match(T__0);
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(82);
				match(WS);
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
			adev();
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SETFLAGS) {
				{
				setState(89);
				match(SETFLAGS);
				}
			}

			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(92);
				match(WS);
				}
				}
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(98);
				comment();
				}
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

	public static class Instruction_passrightContext extends ParserRuleContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public ImmedContext immed() {
			return getRuleContext(ImmedContext.class,0);
		}
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public Ram_directContext ram_direct() {
			return getRuleContext(Ram_directContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
		}
		public TerminalNode SETFLAGS() { return getToken(SPAM1Parser.SETFLAGS, 0); }
		public CommentContext comment() {
			return getRuleContext(CommentContext.class,0);
		}
		public Instruction_passrightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction_passright; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterInstruction_passright(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitInstruction_passright(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitInstruction_passright(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Instruction_passrightContext instruction_passright() throws RecognitionException {
		Instruction_passrightContext _localctx = new Instruction_passrightContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_instruction_passright);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			target();
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(102);
				match(WS);
				}
				}
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(108);
			match(T__0);
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(109);
				match(WS);
				}
				}
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(118);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IMMED_DEC:
			case IMMED_HEX:
			case IMMED_KONST:
			case IMMED_LABEL:
				{
				setState(115);
				immed();
				}
				break;
			case RAM:
				{
				setState(116);
				match(RAM);
				}
				break;
			case RAM_ADDR_DEC:
			case RAM_ADDR_HEX:
			case RAM_ADDR_LABEL:
			case RAM_ADDR_KONST:
				{
				setState(117);
				ram_direct();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SETFLAGS) {
				{
				setState(120);
				match(SETFLAGS);
				}
			}

			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(123);
				match(WS);
				}
				}
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(129);
				comment();
				}
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
		enterRule(_localctx, 10, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
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

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(SPAM1Parser.COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitComment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitComment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(COMMENT);
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
		enterRule(_localctx, 14, RULE_adev);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
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
		public TerminalNode REGA() { return getToken(SPAM1Parser.REGA, 0); }
		public TerminalNode REGB() { return getToken(SPAM1Parser.REGB, 0); }
		public TerminalNode REGC() { return getToken(SPAM1Parser.REGC, 0); }
		public TerminalNode REGD() { return getToken(SPAM1Parser.REGD, 0); }
		public TerminalNode MARLO() { return getToken(SPAM1Parser.MARLO, 0); }
		public TerminalNode MARHI() { return getToken(SPAM1Parser.MARHI, 0); }
		public ImmedContext immed() {
			return getRuleContext(ImmedContext.class,0);
		}
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public Ram_directContext ram_direct() {
			return getRuleContext(Ram_directContext.class,0);
		}
		public TerminalNode NU() { return getToken(SPAM1Parser.NU, 0); }
		public BdevContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bdev; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterBdev(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitBdev(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitBdev(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BdevContext bdev() throws RecognitionException {
		BdevContext _localctx = new BdevContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_bdev);
		try {
			setState(148);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REGA:
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				match(REGA);
				}
				break;
			case REGB:
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				match(REGB);
				}
				break;
			case REGC:
				enterOuterAlt(_localctx, 3);
				{
				setState(140);
				match(REGC);
				}
				break;
			case REGD:
				enterOuterAlt(_localctx, 4);
				{
				setState(141);
				match(REGD);
				}
				break;
			case MARLO:
				enterOuterAlt(_localctx, 5);
				{
				setState(142);
				match(MARLO);
				}
				break;
			case MARHI:
				enterOuterAlt(_localctx, 6);
				{
				setState(143);
				match(MARHI);
				}
				break;
			case IMMED_DEC:
			case IMMED_HEX:
			case IMMED_KONST:
			case IMMED_LABEL:
				enterOuterAlt(_localctx, 7);
				{
				setState(144);
				immed();
				}
				break;
			case RAM:
				enterOuterAlt(_localctx, 8);
				{
				setState(145);
				match(RAM);
				}
				break;
			case RAM_ADDR_DEC:
			case RAM_ADDR_HEX:
			case RAM_ADDR_LABEL:
			case RAM_ADDR_KONST:
				enterOuterAlt(_localctx, 9);
				{
				setState(146);
				ram_direct();
				}
				break;
			case NU:
				enterOuterAlt(_localctx, 10);
				{
				setState(147);
				match(NU);
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

	public static class TargetContext extends ParserRuleContext {
		public TerminalNode REGA() { return getToken(SPAM1Parser.REGA, 0); }
		public TerminalNode REGB() { return getToken(SPAM1Parser.REGB, 0); }
		public TerminalNode REGC() { return getToken(SPAM1Parser.REGC, 0); }
		public TerminalNode REGD() { return getToken(SPAM1Parser.REGD, 0); }
		public TerminalNode MARLO() { return getToken(SPAM1Parser.MARLO, 0); }
		public TerminalNode MARHI() { return getToken(SPAM1Parser.MARHI, 0); }
		public TerminalNode UART() { return getToken(SPAM1Parser.UART, 0); }
		public TerminalNode PC() { return getToken(SPAM1Parser.PC, 0); }
		public TerminalNode PCLO() { return getToken(SPAM1Parser.PCLO, 0); }
		public TerminalNode PCHITMP() { return getToken(SPAM1Parser.PCHITMP, 0); }
		public TerminalNode NU() { return getToken(SPAM1Parser.NU, 0); }
		public TargetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_target; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterTarget(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitTarget(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitTarget(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TargetContext target() throws RecognitionException {
		TargetContext _localctx = new TargetContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_target);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU))) != 0)) ) {
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

	public static class ImmedContext extends ParserRuleContext {
		public TerminalNode IMMED_DEC() { return getToken(SPAM1Parser.IMMED_DEC, 0); }
		public TerminalNode IMMED_HEX() { return getToken(SPAM1Parser.IMMED_HEX, 0); }
		public TerminalNode IMMED_LABEL() { return getToken(SPAM1Parser.IMMED_LABEL, 0); }
		public TerminalNode IMMED_KONST() { return getToken(SPAM1Parser.IMMED_KONST, 0); }
		public ImmedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_immed; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterImmed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitImmed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitImmed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImmedContext immed() throws RecognitionException {
		ImmedContext _localctx = new ImmedContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_immed);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IMMED_DEC) | (1L << IMMED_HEX) | (1L << IMMED_KONST) | (1L << IMMED_LABEL))) != 0)) ) {
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

	public static class Ram_directContext extends ParserRuleContext {
		public TerminalNode RAM_ADDR_DEC() { return getToken(SPAM1Parser.RAM_ADDR_DEC, 0); }
		public TerminalNode RAM_ADDR_HEX() { return getToken(SPAM1Parser.RAM_ADDR_HEX, 0); }
		public TerminalNode RAM_ADDR_LABEL() { return getToken(SPAM1Parser.RAM_ADDR_LABEL, 0); }
		public TerminalNode RAM_ADDR_KONST() { return getToken(SPAM1Parser.RAM_ADDR_KONST, 0); }
		public Ram_directContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ram_direct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterRam_direct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitRam_direct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SPAM1Visitor ) return ((SPAM1Visitor<? extends T>)visitor).visitRam_direct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ram_directContext ram_direct() throws RecognitionException {
		Ram_directContext _localctx = new Ram_directContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ram_direct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RAM_ADDR_DEC) | (1L << RAM_ADDR_HEX) | (1L << RAM_ADDR_LABEL) | (1L << RAM_ADDR_KONST))) != 0)) ) {
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!\u009f\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\5\2\34\n\2\3\2\6\2\37\n\2\r\2\16\2 \3\3\3\3\3"+
		"\3\3\3\5\3\'\n\3\3\4\3\4\7\4+\n\4\f\4\16\4.\13\4\3\4\3\4\7\4\62\n\4\f"+
		"\4\16\4\65\13\4\3\4\3\4\6\49\n\4\r\4\16\4:\3\4\3\4\6\4?\n\4\r\4\16\4@"+
		"\3\4\3\4\7\4E\n\4\f\4\16\4H\13\4\3\4\5\4K\n\4\3\5\3\5\7\5O\n\5\f\5\16"+
		"\5R\13\5\3\5\3\5\7\5V\n\5\f\5\16\5Y\13\5\3\5\3\5\5\5]\n\5\3\5\7\5`\n\5"+
		"\f\5\16\5c\13\5\3\5\5\5f\n\5\3\6\3\6\7\6j\n\6\f\6\16\6m\13\6\3\6\3\6\7"+
		"\6q\n\6\f\6\16\6t\13\6\3\6\3\6\3\6\5\6y\n\6\3\6\5\6|\n\6\3\6\7\6\177\n"+
		"\6\f\6\16\6\u0082\13\6\3\6\5\6\u0085\n\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0097\n\n\3\13\3\13\3\f\3\f\3\r"+
		"\3\r\3\r\2\2\16\2\4\6\b\n\f\16\20\22\24\26\30\2\6\3\2\f\23\4\2\b\n\f\23"+
		"\3\2\26\31\3\2\32\35\2\u00b2\2\36\3\2\2\2\4&\3\2\2\2\6(\3\2\2\2\bL\3\2"+
		"\2\2\ng\3\2\2\2\f\u0086\3\2\2\2\16\u0088\3\2\2\2\20\u008a\3\2\2\2\22\u0096"+
		"\3\2\2\2\24\u0098\3\2\2\2\26\u009a\3\2\2\2\30\u009c\3\2\2\2\32\34\5\4"+
		"\3\2\33\32\3\2\2\2\33\34\3\2\2\2\34\35\3\2\2\2\35\37\7\37\2\2\36\33\3"+
		"\2\2\2\37 \3\2\2\2 \36\3\2\2\2 !\3\2\2\2!\3\3\2\2\2\"\'\5\6\4\2#\'\5\b"+
		"\5\2$\'\5\n\6\2%\'\5\16\b\2&\"\3\2\2\2&#\3\2\2\2&$\3\2\2\2&%\3\2\2\2\'"+
		"\5\3\2\2\2(,\5\24\13\2)+\7\36\2\2*)\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2"+
		"\2\2-/\3\2\2\2.,\3\2\2\2/\63\7\3\2\2\60\62\7\36\2\2\61\60\3\2\2\2\62\65"+
		"\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\66\3\2\2\2\65\63\3\2\2\2\668\5"+
		"\20\t\2\679\7\36\2\28\67\3\2\2\29:\3\2\2\2:8\3\2\2\2:;\3\2\2\2;<\3\2\2"+
		"\2<>\7\4\2\2=?\7\36\2\2>=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2AB\3\2"+
		"\2\2BF\5\22\n\2CE\7\36\2\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2GJ\3"+
		"\2\2\2HF\3\2\2\2IK\5\16\b\2JI\3\2\2\2JK\3\2\2\2K\7\3\2\2\2LP\5\24\13\2"+
		"MO\7\36\2\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QS\3\2\2\2RP\3\2\2"+
		"\2SW\7\3\2\2TV\7\36\2\2UT\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2XZ\3\2"+
		"\2\2YW\3\2\2\2Z\\\5\20\t\2[]\7\5\2\2\\[\3\2\2\2\\]\3\2\2\2]a\3\2\2\2^"+
		"`\7\36\2\2_^\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2be\3\2\2\2ca\3\2\2\2"+
		"df\5\16\b\2ed\3\2\2\2ef\3\2\2\2f\t\3\2\2\2gk\5\24\13\2hj\7\36\2\2ih\3"+
		"\2\2\2jm\3\2\2\2ki\3\2\2\2kl\3\2\2\2ln\3\2\2\2mk\3\2\2\2nr\7\3\2\2oq\7"+
		"\36\2\2po\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2sx\3\2\2\2tr\3\2\2\2uy"+
		"\5\26\f\2vy\7\13\2\2wy\5\30\r\2xu\3\2\2\2xv\3\2\2\2xw\3\2\2\2y{\3\2\2"+
		"\2z|\7\5\2\2{z\3\2\2\2{|\3\2\2\2|\u0080\3\2\2\2}\177\7\36\2\2~}\3\2\2"+
		"\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0084\3"+
		"\2\2\2\u0082\u0080\3\2\2\2\u0083\u0085\5\16\b\2\u0084\u0083\3\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0085\13\3\2\2\2\u0086\u0087\7\24\2\2\u0087\r\3\2\2\2\u0088"+
		"\u0089\7 \2\2\u0089\17\3\2\2\2\u008a\u008b\t\2\2\2\u008b\21\3\2\2\2\u008c"+
		"\u0097\7\f\2\2\u008d\u0097\7\r\2\2\u008e\u0097\7\16\2\2\u008f\u0097\7"+
		"\17\2\2\u0090\u0097\7\20\2\2\u0091\u0097\7\21\2\2\u0092\u0097\5\26\f\2"+
		"\u0093\u0097\7\13\2\2\u0094\u0097\5\30\r\2\u0095\u0097\7\23\2\2\u0096"+
		"\u008c\3\2\2\2\u0096\u008d\3\2\2\2\u0096\u008e\3\2\2\2\u0096\u008f\3\2"+
		"\2\2\u0096\u0090\3\2\2\2\u0096\u0091\3\2\2\2\u0096\u0092\3\2\2\2\u0096"+
		"\u0093\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0095\3\2\2\2\u0097\23\3\2\2"+
		"\2\u0098\u0099\t\3\2\2\u0099\25\3\2\2\2\u009a\u009b\t\4\2\2\u009b\27\3"+
		"\2\2\2\u009c\u009d\t\5\2\2\u009d\31\3\2\2\2\27\33 &,\63:@FJPW\\aekrx{"+
		"\u0080\u0084\u0096";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}