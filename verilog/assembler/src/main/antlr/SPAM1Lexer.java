// Generated from src\main\antlr\SPAM1.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SPAM1Lexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "ALUOP", "ALUOPS", "FLAG", "PCHITMP", "PCLO", "PC", "RAM", "REGA", 
			"REGB", "REGC", "REGD", "MARLO", "MARHI", "UART", "NU", "LABEL", "NAME", 
			"DIGIT", "HEX", "IMMED_DEC", "IMMED_HEX", "IMMED_KONST", "IMMED_LABEL", 
			"DIGIT5", "RAM_ADDR_DEC", "RAM_ADDR_HEX", "RAM_ADDR_LABEL", "RAM_ADDR_KONST", 
			"WS", "EOL", "COMMENT", "STRING", "A", "B", "C", "D", "E", "F", "G", 
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", 
			"V", "W", "X", "Y", "Z"
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


	public SPAM1Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SPAM1.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2#\u0172\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\3\2\3"+
		"\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\5\3\u008f\n\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3"+
		"\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3"+
		"\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\7\23\u00d8"+
		"\n\23\f\23\16\23\u00db\13\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\5\26\u00e4"+
		"\n\26\3\26\5\26\u00e7\n\26\3\27\3\27\3\27\3\27\3\27\5\27\u00ee\n\27\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\5\32\u00fd"+
		"\n\32\3\32\5\32\u0100\n\32\3\32\5\32\u0103\n\32\3\32\5\32\u0106\n\32\3"+
		"\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\5\34\u0111\n\34\3\34\5\34"+
		"\u0114\n\34\3\34\5\34\u0117\n\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3"+
		"\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3 \6 \u012b\n \r \16"+
		" \u012c\3!\3!\7!\u0131\n!\f!\16!\u0134\13!\3\"\3\"\7\"\u0138\n\"\f\"\16"+
		"\"\u013b\13\"\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3"+
		"*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63"+
		"\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\39\3:\3:\3;\3;\3<\3"+
		"<\2\2=\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17"+
		"\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\35"+
		"9\36;\37= ?!A\"C#E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\2a\2c\2e\2g"+
		"\2i\2k\2m\2o\2q\2s\2u\2w\2\3\2\"\4\2C\\c|\6\2\62;C\\aac|\5\2\62;CHch\4"+
		"\2\13\13\"\"\4\2\f\f\17\17\3\2$$\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2G"+
		"Ggg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4"+
		"\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXx"+
		"x\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u0169\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2"+
		"\2\2A\3\2\2\2\2C\3\2\2\2\3y\3\2\2\2\5\u008e\3\2\2\2\7\u0090\3\2\2\2\t"+
		"\u0093\3\2\2\2\13\u0096\3\2\2\2\r\u009e\3\2\2\2\17\u00a3\3\2\2\2\21\u00a6"+
		"\3\2\2\2\23\u00aa\3\2\2\2\25\u00af\3\2\2\2\27\u00b4\3\2\2\2\31\u00b9\3"+
		"\2\2\2\33\u00be\3\2\2\2\35\u00c4\3\2\2\2\37\u00ca\3\2\2\2!\u00cf\3\2\2"+
		"\2#\u00d2\3\2\2\2%\u00d5\3\2\2\2\'\u00dc\3\2\2\2)\u00de\3\2\2\2+\u00e0"+
		"\3\2\2\2-\u00e8\3\2\2\2/\u00ef\3\2\2\2\61\u00f5\3\2\2\2\63\u00fa\3\2\2"+
		"\2\65\u0107\3\2\2\2\67\u010b\3\2\2\29\u011a\3\2\2\2;\u0120\3\2\2\2=\u0127"+
		"\3\2\2\2?\u012a\3\2\2\2A\u012e\3\2\2\2C\u0135\3\2\2\2E\u013e\3\2\2\2G"+
		"\u0140\3\2\2\2I\u0142\3\2\2\2K\u0144\3\2\2\2M\u0146\3\2\2\2O\u0148\3\2"+
		"\2\2Q\u014a\3\2\2\2S\u014c\3\2\2\2U\u014e\3\2\2\2W\u0150\3\2\2\2Y\u0152"+
		"\3\2\2\2[\u0154\3\2\2\2]\u0156\3\2\2\2_\u0158\3\2\2\2a\u015a\3\2\2\2c"+
		"\u015c\3\2\2\2e\u015e\3\2\2\2g\u0160\3\2\2\2i\u0162\3\2\2\2k\u0164\3\2"+
		"\2\2m\u0166\3\2\2\2o\u0168\3\2\2\2q\u016a\3\2\2\2s\u016c\3\2\2\2u\u016e"+
		"\3\2\2\2w\u0170\3\2\2\2yz\7?\2\2z\4\3\2\2\2{|\7C\2\2|}\7P\2\2}\u008f\7"+
		"F\2\2~\177\7Q\2\2\177\u008f\7T\2\2\u0080\u0081\7V\2\2\u0081\u0082\7K\2"+
		"\2\u0082\u0083\7O\2\2\u0083\u0084\7G\2\2\u0084\u008f\7U\2\2\u0085\u0086"+
		"\7R\2\2\u0086\u0087\7N\2\2\u0087\u0088\7W\2\2\u0088\u008f\7U\2\2\u0089"+
		"\u008a\7R\2\2\u008a\u008b\7N\2\2\u008b\u008c\7W\2\2\u008c\u008d\7U\2\2"+
		"\u008d\u008f\7E\2\2\u008e{\3\2\2\2\u008e~\3\2\2\2\u008e\u0080\3\2\2\2"+
		"\u008e\u0085\3\2\2\2\u008e\u0089\3\2\2\2\u008f\6\3\2\2\2\u0090\u0091\5"+
		"\5\3\2\u0091\u0092\5\t\5\2\u0092\b\3\2\2\2\u0093\u0094\7)\2\2\u0094\u0095"+
		"\7U\2\2\u0095\n\3\2\2\2\u0096\u0097\5c\62\2\u0097\u0098\5I%\2\u0098\u0099"+
		"\5S*\2\u0099\u009a\5U+\2\u009a\u009b\5k\66\2\u009b\u009c\5]/\2\u009c\u009d"+
		"\5c\62\2\u009d\f\3\2\2\2\u009e\u009f\5c\62\2\u009f\u00a0\5I%\2\u00a0\u00a1"+
		"\5[.\2\u00a1\u00a2\5a\61\2\u00a2\16\3\2\2\2\u00a3\u00a4\5c\62\2\u00a4"+
		"\u00a5\5I%\2\u00a5\20\3\2\2\2\u00a6\u00a7\5g\64\2\u00a7\u00a8\5E#\2\u00a8"+
		"\u00a9\5]/\2\u00a9\22\3\2\2\2\u00aa\u00ab\5g\64\2\u00ab\u00ac\5M\'\2\u00ac"+
		"\u00ad\5Q)\2\u00ad\u00ae\5E#\2\u00ae\24\3\2\2\2\u00af\u00b0\5g\64\2\u00b0"+
		"\u00b1\5M\'\2\u00b1\u00b2\5Q)\2\u00b2\u00b3\5G$\2\u00b3\26\3\2\2\2\u00b4"+
		"\u00b5\5g\64\2\u00b5\u00b6\5M\'\2\u00b6\u00b7\5Q)\2\u00b7\u00b8\5I%\2"+
		"\u00b8\30\3\2\2\2\u00b9\u00ba\5g\64\2\u00ba\u00bb\5M\'\2\u00bb\u00bc\5"+
		"Q)\2\u00bc\u00bd\5K&\2\u00bd\32\3\2\2\2\u00be\u00bf\5]/\2\u00bf\u00c0"+
		"\5E#\2\u00c0\u00c1\5g\64\2\u00c1\u00c2\5[.\2\u00c2\u00c3\5a\61\2\u00c3"+
		"\34\3\2\2\2\u00c4\u00c5\5]/\2\u00c5\u00c6\5E#\2\u00c6\u00c7\5g\64\2\u00c7"+
		"\u00c8\5S*\2\u00c8\u00c9\5U+\2\u00c9\36\3\2\2\2\u00ca\u00cb\5m\67\2\u00cb"+
		"\u00cc\5E#\2\u00cc\u00cd\5g\64\2\u00cd\u00ce\5k\66\2\u00ce \3\2\2\2\u00cf"+
		"\u00d0\5_\60\2\u00d0\u00d1\5m\67\2\u00d1\"\3\2\2\2\u00d2\u00d3\5%\23\2"+
		"\u00d3\u00d4\7<\2\2\u00d4$\3\2\2\2\u00d5\u00d9\t\2\2\2\u00d6\u00d8\t\3"+
		"\2\2\u00d7\u00d6\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9"+
		"\u00da\3\2\2\2\u00da&\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00dd\4\62;\2"+
		"\u00dd(\3\2\2\2\u00de\u00df\t\4\2\2\u00df*\3\2\2\2\u00e0\u00e1\7%\2\2"+
		"\u00e1\u00e3\5\'\24\2\u00e2\u00e4\5\'\24\2\u00e3\u00e2\3\2\2\2\u00e3\u00e4"+
		"\3\2\2\2\u00e4\u00e6\3\2\2\2\u00e5\u00e7\5\'\24\2\u00e6\u00e5\3\2\2\2"+
		"\u00e6\u00e7\3\2\2\2\u00e7,\3\2\2\2\u00e8\u00e9\7%\2\2\u00e9\u00ea\7&"+
		"\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ed\5)\25\2\u00ec\u00ee\5)\25\2\u00ed"+
		"\u00ec\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee.\3\2\2\2\u00ef\u00f0\7%\2\2\u00f0"+
		"\u00f1\7*\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f3\5%\23\2\u00f3\u00f4\7+\2"+
		"\2\u00f4\60\3\2\2\2\u00f5\u00f6\7%\2\2\u00f6\u00f7\7<\2\2\u00f7\u00f8"+
		"\3\2\2\2\u00f8\u00f9\5%\23\2\u00f9\62\3\2\2\2\u00fa\u00fc\5\'\24\2\u00fb"+
		"\u00fd\5\'\24\2\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00ff\3"+
		"\2\2\2\u00fe\u0100\5\'\24\2\u00ff\u00fe\3\2\2\2\u00ff\u0100\3\2\2\2\u0100"+
		"\u0102\3\2\2\2\u0101\u0103\5\'\24\2\u0102\u0101\3\2\2\2\u0102\u0103\3"+
		"\2\2\2\u0103\u0105\3\2\2\2\u0104\u0106\5\'\24\2\u0105\u0104\3\2\2\2\u0105"+
		"\u0106\3\2\2\2\u0106\64\3\2\2\2\u0107\u0108\7]\2\2\u0108\u0109\5\63\32"+
		"\2\u0109\u010a\7_\2\2\u010a\66\3\2\2\2\u010b\u010c\7]\2\2\u010c\u010d"+
		"\7&\2\2\u010d\u010e\3\2\2\2\u010e\u0110\5)\25\2\u010f\u0111\5)\25\2\u0110"+
		"\u010f\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0113\3\2\2\2\u0112\u0114\5)"+
		"\25\2\u0113\u0112\3\2\2\2\u0113\u0114\3\2\2\2\u0114\u0116\3\2\2\2\u0115"+
		"\u0117\5)\25\2\u0116\u0115\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\3\2"+
		"\2\2\u0118\u0119\7_\2\2\u01198\3\2\2\2\u011a\u011b\7]\2\2\u011b\u011c"+
		"\7<\2\2\u011c\u011d\3\2\2\2\u011d\u011e\5%\23\2\u011e\u011f\7_\2\2\u011f"+
		":\3\2\2\2\u0120\u0121\7]\2\2\u0121\u0122\7*\2\2\u0122\u0123\3\2\2\2\u0123"+
		"\u0124\5%\23\2\u0124\u0125\7+\2\2\u0125\u0126\7_\2\2\u0126<\3\2\2\2\u0127"+
		"\u0128\t\5\2\2\u0128>\3\2\2\2\u0129\u012b\t\6\2\2\u012a\u0129\3\2\2\2"+
		"\u012b\u012c\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d@\3"+
		"\2\2\2\u012e\u0132\7=\2\2\u012f\u0131\n\6\2\2\u0130\u012f\3\2\2\2\u0131"+
		"\u0134\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133B\3\2\2\2"+
		"\u0134\u0132\3\2\2\2\u0135\u0139\7$\2\2\u0136\u0138\n\7\2\2\u0137\u0136"+
		"\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a"+
		"\u013c\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u013d\7$\2\2\u013dD\3\2\2\2\u013e"+
		"\u013f\t\b\2\2\u013fF\3\2\2\2\u0140\u0141\t\t\2\2\u0141H\3\2\2\2\u0142"+
		"\u0143\t\n\2\2\u0143J\3\2\2\2\u0144\u0145\t\13\2\2\u0145L\3\2\2\2\u0146"+
		"\u0147\t\f\2\2\u0147N\3\2\2\2\u0148\u0149\t\r\2\2\u0149P\3\2\2\2\u014a"+
		"\u014b\t\16\2\2\u014bR\3\2\2\2\u014c\u014d\t\17\2\2\u014dT\3\2\2\2\u014e"+
		"\u014f\t\20\2\2\u014fV\3\2\2\2\u0150\u0151\t\21\2\2\u0151X\3\2\2\2\u0152"+
		"\u0153\t\22\2\2\u0153Z\3\2\2\2\u0154\u0155\t\23\2\2\u0155\\\3\2\2\2\u0156"+
		"\u0157\t\24\2\2\u0157^\3\2\2\2\u0158\u0159\t\25\2\2\u0159`\3\2\2\2\u015a"+
		"\u015b\t\26\2\2\u015bb\3\2\2\2\u015c\u015d\t\27\2\2\u015dd\3\2\2\2\u015e"+
		"\u015f\t\30\2\2\u015ff\3\2\2\2\u0160\u0161\t\31\2\2\u0161h\3\2\2\2\u0162"+
		"\u0163\t\32\2\2\u0163j\3\2\2\2\u0164\u0165\t\33\2\2\u0165l\3\2\2\2\u0166"+
		"\u0167\t\34\2\2\u0167n\3\2\2\2\u0168\u0169\t\35\2\2\u0169p\3\2\2\2\u016a"+
		"\u016b\t\36\2\2\u016br\3\2\2\2\u016c\u016d\t\37\2\2\u016dt\3\2\2\2\u016e"+
		"\u016f\t \2\2\u016fv\3\2\2\2\u0170\u0171\t!\2\2\u0171x\3\2\2\2\22\2\u008e"+
		"\u00d9\u00e3\u00e6\u00ed\u00fc\u00ff\u0102\u0105\u0110\u0113\u0116\u012c"+
		"\u0132\u0139\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}