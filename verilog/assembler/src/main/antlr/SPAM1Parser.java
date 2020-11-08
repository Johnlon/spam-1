// Generated from src\main\antlr\SPAM1.g4 by ANTLR 4.7.2
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
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, ALUOP=2, ALUOPS=3, FLAG=4, PCHITMP=5, PCLO=6, PC=7, RAM=8, REGA=9, 
		REGB=10, REGC=11, REGD=12, MARLO=13, MARHI=14, UART=15, NU=16, LABEL=17, 
		NAME=18, DIGIT=19, HEX=20, IMMED_DEC=21, IMMED_HEX=22, IMMED_KONST=23, 
		IMMED_LABEL=24, DIGIT5=25, RAM_ADDR_DEC=26, RAM_ADDR_HEX=27, RAM_ADDR_LABEL=28, 
		RAM_ADDR_KONST=29, WS=30, EOL=31, COMMENT=32, STRING=33;
	public static final int
		RULE_compile = 0, RULE_line = 1, RULE_instructions = 2, RULE_instruction = 3, 
		RULE_instruction_passleft = 4, RULE_instruction_passright = 5, RULE_comment = 6, 
		RULE_adev = 7, RULE_bdev = 8, RULE_target = 9, RULE_immed = 10, RULE_ram_direct = 11, 
		RULE_op = 12, RULE_passa = 13, RULE_passb = 14, RULE_label = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"compile", "line", "instructions", "instruction", "instruction_passleft", 
			"instruction_passright", "comment", "adev", "bdev", "target", "immed", 
			"ram_direct", "op", "passa", "passb", "label"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", null, null, "''S'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "ALUOP", "ALUOPS", "FLAG", "PCHITMP", "PCLO", "PC", "RAM", 
			"REGA", "REGB", "REGC", "REGD", "MARLO", "MARHI", "UART", "NU", "LABEL", 
			"NAME", "DIGIT", "HEX", "IMMED_DEC", "IMMED_HEX", "IMMED_KONST", "IMMED_LABEL", 
			"DIGIT5", "RAM_ADDR_DEC", "RAM_ADDR_HEX", "RAM_ADDR_LABEL", "RAM_ADDR_KONST", 
			"WS", "EOL", "COMMENT", "STRING"
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
	}

	public final CompileContext compile() throws RecognitionException {
		CompileContext _localctx = new CompileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(33);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(32);
					line();
					}
					break;
				}
				setState(35);
				match(EOL);
				}
				}
				setState(38); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << RAM) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU) | (1L << LABEL) | (1L << RAM_ADDR_DEC) | (1L << RAM_ADDR_HEX) | (1L << RAM_ADDR_LABEL) | (1L << RAM_ADDR_KONST) | (1L << WS) | (1L << EOL) | (1L << COMMENT))) != 0) );
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
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
		}
		public InstructionsContext instructions() {
			return getRuleContext(InstructionsContext.class,0);
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
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABEL) {
				{
				setState(40);
				label();
				}
			}

			setState(46);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(43);
					match(WS);
					}
					} 
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PCHITMP) | (1L << PCLO) | (1L << PC) | (1L << RAM) | (1L << REGA) | (1L << REGB) | (1L << REGC) | (1L << REGD) | (1L << MARLO) | (1L << MARHI) | (1L << UART) | (1L << NU) | (1L << RAM_ADDR_DEC) | (1L << RAM_ADDR_HEX) | (1L << RAM_ADDR_LABEL) | (1L << RAM_ADDR_KONST))) != 0)) {
				{
				setState(49);
				instructions();
				}
			}

			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(52);
				match(WS);
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(58);
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

	public static class InstructionsContext extends ParserRuleContext {
		public InstructionContext instruction() {
			return getRuleContext(InstructionContext.class,0);
		}
		public Instruction_passrightContext instruction_passright() {
			return getRuleContext(Instruction_passrightContext.class,0);
		}
		public Instruction_passleftContext instruction_passleft() {
			return getRuleContext(Instruction_passleftContext.class,0);
		}
		public InstructionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instructions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterInstructions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitInstructions(this);
		}
	}

	public final InstructionsContext instructions() throws RecognitionException {
		InstructionsContext _localctx = new InstructionsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_instructions);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(61);
				instruction();
				}
				break;
			case 2:
				{
				setState(62);
				instruction_passright();
				}
				break;
			case 3:
				{
				setState(63);
				instruction_passleft();
				}
				break;
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

	public static class InstructionContext extends ParserRuleContext {
		public TargetContext target() {
			return getRuleContext(TargetContext.class,0);
		}
		public AdevContext adev() {
			return getRuleContext(AdevContext.class,0);
		}
		public OpContext op() {
			return getRuleContext(OpContext.class,0);
		}
		public BdevContext bdev() {
			return getRuleContext(BdevContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
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
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_instruction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			target();
			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(67);
				match(WS);
				}
				}
				setState(72);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(73);
			match(T__0);
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(74);
				match(WS);
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(80);
			adev();
			setState(82); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(81);
				match(WS);
				}
				}
				setState(84); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(86);
			op();
			setState(88); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(87);
				match(WS);
				}
				}
				setState(90); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(92);
			bdev();
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
		public PassaContext passa() {
			return getRuleContext(PassaContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
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
	}

	public final Instruction_passleftContext instruction_passleft() throws RecognitionException {
		Instruction_passleftContext _localctx = new Instruction_passleftContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_instruction_passleft);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			target();
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(95);
				match(WS);
				}
				}
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(101);
			match(T__0);
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
			passa();
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
		public PassbContext passb() {
			return getRuleContext(PassbContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(SPAM1Parser.WS); }
		public TerminalNode WS(int i) {
			return getToken(SPAM1Parser.WS, i);
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
	}

	public final Instruction_passrightContext instruction_passright() throws RecognitionException {
		Instruction_passrightContext _localctx = new Instruction_passrightContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_instruction_passright);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			target();
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(111);
				match(WS);
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(117);
			match(T__0);
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(118);
				match(WS);
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(124);
			passb();
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
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_comment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
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
	}

	public final AdevContext adev() throws RecognitionException {
		AdevContext _localctx = new AdevContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_adev);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
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
	}

	public final BdevContext bdev() throws RecognitionException {
		BdevContext _localctx = new BdevContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_bdev);
		try {
			setState(140);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REGA:
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				match(REGA);
				}
				break;
			case REGB:
				enterOuterAlt(_localctx, 2);
				{
				setState(131);
				match(REGB);
				}
				break;
			case REGC:
				enterOuterAlt(_localctx, 3);
				{
				setState(132);
				match(REGC);
				}
				break;
			case REGD:
				enterOuterAlt(_localctx, 4);
				{
				setState(133);
				match(REGD);
				}
				break;
			case MARLO:
				enterOuterAlt(_localctx, 5);
				{
				setState(134);
				match(MARLO);
				}
				break;
			case MARHI:
				enterOuterAlt(_localctx, 6);
				{
				setState(135);
				match(MARHI);
				}
				break;
			case IMMED_DEC:
			case IMMED_HEX:
			case IMMED_KONST:
			case IMMED_LABEL:
				enterOuterAlt(_localctx, 7);
				{
				setState(136);
				immed();
				}
				break;
			case RAM:
				enterOuterAlt(_localctx, 8);
				{
				setState(137);
				match(RAM);
				}
				break;
			case RAM_ADDR_DEC:
			case RAM_ADDR_HEX:
			case RAM_ADDR_LABEL:
			case RAM_ADDR_KONST:
				enterOuterAlt(_localctx, 9);
				{
				setState(138);
				ram_direct();
				}
				break;
			case NU:
				enterOuterAlt(_localctx, 10);
				{
				setState(139);
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
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public Ram_directContext ram_direct() {
			return getRuleContext(Ram_directContext.class,0);
		}
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
	}

	public final TargetContext target() throws RecognitionException {
		TargetContext _localctx = new TargetContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_target);
		try {
			setState(155);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REGA:
				enterOuterAlt(_localctx, 1);
				{
				setState(142);
				match(REGA);
				}
				break;
			case REGB:
				enterOuterAlt(_localctx, 2);
				{
				setState(143);
				match(REGB);
				}
				break;
			case REGC:
				enterOuterAlt(_localctx, 3);
				{
				setState(144);
				match(REGC);
				}
				break;
			case REGD:
				enterOuterAlt(_localctx, 4);
				{
				setState(145);
				match(REGD);
				}
				break;
			case MARLO:
				enterOuterAlt(_localctx, 5);
				{
				setState(146);
				match(MARLO);
				}
				break;
			case MARHI:
				enterOuterAlt(_localctx, 6);
				{
				setState(147);
				match(MARHI);
				}
				break;
			case UART:
				enterOuterAlt(_localctx, 7);
				{
				setState(148);
				match(UART);
				}
				break;
			case RAM:
				enterOuterAlt(_localctx, 8);
				{
				setState(149);
				match(RAM);
				}
				break;
			case RAM_ADDR_DEC:
			case RAM_ADDR_HEX:
			case RAM_ADDR_LABEL:
			case RAM_ADDR_KONST:
				enterOuterAlt(_localctx, 9);
				{
				setState(150);
				ram_direct();
				}
				break;
			case PC:
				enterOuterAlt(_localctx, 10);
				{
				setState(151);
				match(PC);
				}
				break;
			case PCLO:
				enterOuterAlt(_localctx, 11);
				{
				setState(152);
				match(PCLO);
				}
				break;
			case PCHITMP:
				enterOuterAlt(_localctx, 12);
				{
				setState(153);
				match(PCHITMP);
				}
				break;
			case NU:
				enterOuterAlt(_localctx, 13);
				{
				setState(154);
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

	public static class ImmedContext extends ParserRuleContext {
		public Token v;
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
	}

	public final ImmedContext immed() throws RecognitionException {
		ImmedContext _localctx = new ImmedContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_immed);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			((ImmedContext)_localctx).v = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IMMED_DEC) | (1L << IMMED_HEX) | (1L << IMMED_KONST) | (1L << IMMED_LABEL))) != 0)) ) {
				((ImmedContext)_localctx).v = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			 System.out.println("RAM_ADDR_DEC " + (((ImmedContext)_localctx).v!=null?((ImmedContext)_localctx).v.getText():null)); 
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
	}

	public final Ram_directContext ram_direct() throws RecognitionException {
		Ram_directContext _localctx = new Ram_directContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_ram_direct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
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

	public static class OpContext extends ParserRuleContext {
		public TerminalNode ALUOP() { return getToken(SPAM1Parser.ALUOP, 0); }
		public TerminalNode ALUOPS() { return getToken(SPAM1Parser.ALUOPS, 0); }
		public OpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitOp(this);
		}
	}

	public final OpContext op() throws RecognitionException {
		OpContext _localctx = new OpContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			_la = _input.LA(1);
			if ( !(_la==ALUOP || _la==ALUOPS) ) {
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

	public static class PassaContext extends ParserRuleContext {
		public AdevContext adev() {
			return getRuleContext(AdevContext.class,0);
		}
		public TerminalNode FLAG() { return getToken(SPAM1Parser.FLAG, 0); }
		public PassaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_passa; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterPassa(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitPassa(this);
		}
	}

	public final PassaContext passa() throws RecognitionException {
		PassaContext _localctx = new PassaContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_passa);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			adev();
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAG) {
				{
				setState(165);
				match(FLAG);
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

	public static class PassbContext extends ParserRuleContext {
		public ImmedContext immed() {
			return getRuleContext(ImmedContext.class,0);
		}
		public TerminalNode RAM() { return getToken(SPAM1Parser.RAM, 0); }
		public Ram_directContext ram_direct() {
			return getRuleContext(Ram_directContext.class,0);
		}
		public TerminalNode FLAG() { return getToken(SPAM1Parser.FLAG, 0); }
		public PassbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_passb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).enterPassb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SPAM1Listener ) ((SPAM1Listener)listener).exitPassb(this);
		}
	}

	public final PassbContext passb() throws RecognitionException {
		PassbContext _localctx = new PassbContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_passb);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IMMED_DEC:
			case IMMED_HEX:
			case IMMED_KONST:
			case IMMED_LABEL:
				{
				setState(168);
				immed();
				}
				break;
			case RAM:
				{
				setState(169);
				match(RAM);
				}
				break;
			case RAM_ADDR_DEC:
			case RAM_ADDR_HEX:
			case RAM_ADDR_LABEL:
			case RAM_ADDR_KONST:
				{
				setState(170);
				ram_direct();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAG) {
				{
				setState(173);
				match(FLAG);
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
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3#\u00b5\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\5\2$\n"+
		"\2\3\2\6\2\'\n\2\r\2\16\2(\3\3\5\3,\n\3\3\3\7\3/\n\3\f\3\16\3\62\13\3"+
		"\3\3\5\3\65\n\3\3\3\7\38\n\3\f\3\16\3;\13\3\3\3\5\3>\n\3\3\4\3\4\3\4\5"+
		"\4C\n\4\3\5\3\5\7\5G\n\5\f\5\16\5J\13\5\3\5\3\5\7\5N\n\5\f\5\16\5Q\13"+
		"\5\3\5\3\5\6\5U\n\5\r\5\16\5V\3\5\3\5\6\5[\n\5\r\5\16\5\\\3\5\3\5\3\6"+
		"\3\6\7\6c\n\6\f\6\16\6f\13\6\3\6\3\6\7\6j\n\6\f\6\16\6m\13\6\3\6\3\6\3"+
		"\7\3\7\7\7s\n\7\f\7\16\7v\13\7\3\7\3\7\7\7z\n\7\f\7\16\7}\13\7\3\7\3\7"+
		"\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u008f\n\n"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13"+
		"\u009e\n\13\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\5\17\u00a9\n\17\3"+
		"\20\3\20\3\20\5\20\u00ae\n\20\3\20\5\20\u00b1\n\20\3\21\3\21\3\21\2\2"+
		"\22\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \2\6\3\2\13\22\3\2\27\32\3"+
		"\2\34\37\3\2\4\5\2\u00ce\2&\3\2\2\2\4+\3\2\2\2\6B\3\2\2\2\bD\3\2\2\2\n"+
		"`\3\2\2\2\fp\3\2\2\2\16\u0080\3\2\2\2\20\u0082\3\2\2\2\22\u008e\3\2\2"+
		"\2\24\u009d\3\2\2\2\26\u009f\3\2\2\2\30\u00a2\3\2\2\2\32\u00a4\3\2\2\2"+
		"\34\u00a6\3\2\2\2\36\u00ad\3\2\2\2 \u00b2\3\2\2\2\"$\5\4\3\2#\"\3\2\2"+
		"\2#$\3\2\2\2$%\3\2\2\2%\'\7!\2\2&#\3\2\2\2\'(\3\2\2\2(&\3\2\2\2()\3\2"+
		"\2\2)\3\3\2\2\2*,\5 \21\2+*\3\2\2\2+,\3\2\2\2,\60\3\2\2\2-/\7 \2\2.-\3"+
		"\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2"+
		"\2\63\65\5\6\4\2\64\63\3\2\2\2\64\65\3\2\2\2\659\3\2\2\2\668\7 \2\2\67"+
		"\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:=\3\2\2\2;9\3\2\2\2<>\5\16"+
		"\b\2=<\3\2\2\2=>\3\2\2\2>\5\3\2\2\2?C\5\b\5\2@C\5\f\7\2AC\5\n\6\2B?\3"+
		"\2\2\2B@\3\2\2\2BA\3\2\2\2C\7\3\2\2\2DH\5\24\13\2EG\7 \2\2FE\3\2\2\2G"+
		"J\3\2\2\2HF\3\2\2\2HI\3\2\2\2IK\3\2\2\2JH\3\2\2\2KO\7\3\2\2LN\7 \2\2M"+
		"L\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2PR\3\2\2\2QO\3\2\2\2RT\5\20\t\2"+
		"SU\7 \2\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2WX\3\2\2\2XZ\5\32\16"+
		"\2Y[\7 \2\2ZY\3\2\2\2[\\\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]^\3\2\2\2^_\5\22"+
		"\n\2_\t\3\2\2\2`d\5\24\13\2ac\7 \2\2ba\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3"+
		"\2\2\2eg\3\2\2\2fd\3\2\2\2gk\7\3\2\2hj\7 \2\2ih\3\2\2\2jm\3\2\2\2ki\3"+
		"\2\2\2kl\3\2\2\2ln\3\2\2\2mk\3\2\2\2no\5\34\17\2o\13\3\2\2\2pt\5\24\13"+
		"\2qs\7 \2\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2\2\2vt\3\2\2"+
		"\2w{\7\3\2\2xz\7 \2\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2"+
		"\2}{\3\2\2\2~\177\5\36\20\2\177\r\3\2\2\2\u0080\u0081\7\"\2\2\u0081\17"+
		"\3\2\2\2\u0082\u0083\t\2\2\2\u0083\21\3\2\2\2\u0084\u008f\7\13\2\2\u0085"+
		"\u008f\7\f\2\2\u0086\u008f\7\r\2\2\u0087\u008f\7\16\2\2\u0088\u008f\7"+
		"\17\2\2\u0089\u008f\7\20\2\2\u008a\u008f\5\26\f\2\u008b\u008f\7\n\2\2"+
		"\u008c\u008f\5\30\r\2\u008d\u008f\7\22\2\2\u008e\u0084\3\2\2\2\u008e\u0085"+
		"\3\2\2\2\u008e\u0086\3\2\2\2\u008e\u0087\3\2\2\2\u008e\u0088\3\2\2\2\u008e"+
		"\u0089\3\2\2\2\u008e\u008a\3\2\2\2\u008e\u008b\3\2\2\2\u008e\u008c\3\2"+
		"\2\2\u008e\u008d\3\2\2\2\u008f\23\3\2\2\2\u0090\u009e\7\13\2\2\u0091\u009e"+
		"\7\f\2\2\u0092\u009e\7\r\2\2\u0093\u009e\7\16\2\2\u0094\u009e\7\17\2\2"+
		"\u0095\u009e\7\20\2\2\u0096\u009e\7\21\2\2\u0097\u009e\7\n\2\2\u0098\u009e"+
		"\5\30\r\2\u0099\u009e\7\t\2\2\u009a\u009e\7\b\2\2\u009b\u009e\7\7\2\2"+
		"\u009c\u009e\7\22\2\2\u009d\u0090\3\2\2\2\u009d\u0091\3\2\2\2\u009d\u0092"+
		"\3\2\2\2\u009d\u0093\3\2\2\2\u009d\u0094\3\2\2\2\u009d\u0095\3\2\2\2\u009d"+
		"\u0096\3\2\2\2\u009d\u0097\3\2\2\2\u009d\u0098\3\2\2\2\u009d\u0099\3\2"+
		"\2\2\u009d\u009a\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009c\3\2\2\2\u009e"+
		"\25\3\2\2\2\u009f\u00a0\t\3\2\2\u00a0\u00a1\b\f\1\2\u00a1\27\3\2\2\2\u00a2"+
		"\u00a3\t\4\2\2\u00a3\31\3\2\2\2\u00a4\u00a5\t\5\2\2\u00a5\33\3\2\2\2\u00a6"+
		"\u00a8\5\20\t\2\u00a7\u00a9\7\6\2\2\u00a8\u00a7\3\2\2\2\u00a8\u00a9\3"+
		"\2\2\2\u00a9\35\3\2\2\2\u00aa\u00ae\5\26\f\2\u00ab\u00ae\7\n\2\2\u00ac"+
		"\u00ae\5\30\r\2\u00ad\u00aa\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ac\3"+
		"\2\2\2\u00ae\u00b0\3\2\2\2\u00af\u00b1\7\6\2\2\u00b0\u00af\3\2\2\2\u00b0"+
		"\u00b1\3\2\2\2\u00b1\37\3\2\2\2\u00b2\u00b3\7\23\2\2\u00b3!\3\2\2\2\27"+
		"#(+\60\649=BHOV\\dkt{\u008e\u009d\u00a8\u00ad\u00b0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}