// Generated from C:/Users/johnl/OneDrive/simplecpu/verilog/assembler/src/main/antlr\SPAM1.g4 by ANTLR 4.8
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "ALUOP", "FLAG", "PCHITMP", "PCLO", "PC", "RAM", "REGA", "REGB", 
			"REGC", "REGD", "MARLO", "MARHI", "UART", "NU", "LABEL", "NAME", "STRING", 
			"INT", "HEX", "OCT", "BIN", "CHAR", "WS", "EOL", "COMMENT", "LETTER", 
			"DIGIT", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", 
			"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2%\u0166\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\6\3\6"+
		"\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00ac\n\f\3\r\3\r"+
		"\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\7\33\u00f2\n\33\f\33"+
		"\16\33\u00f5\13\33\3\34\3\34\7\34\u00f9\n\34\f\34\16\34\u00fc\13\34\3"+
		"\34\3\34\3\35\6\35\u0101\n\35\r\35\16\35\u0102\3\36\3\36\6\36\u0107\n"+
		"\36\r\36\16\36\u0108\3\37\3\37\6\37\u010d\n\37\r\37\16\37\u010e\3 \3 "+
		"\6 \u0113\n \r \16 \u0114\3!\3!\3!\3\"\3\"\3\"\3\"\3#\5#\u011f\n#\3#\6"+
		"#\u0122\n#\r#\16#\u0123\3$\3$\7$\u0128\n$\f$\16$\u012b\13$\3$\3$\3%\3"+
		"%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3"+
		"\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3"+
		"\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3@\3@\2\2A\3\3\5\4"+
		"\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C"+
		"#E$G%I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\2a\2c\2e\2g\2i\2k\2m\2o\2q\2s"+
		"\2u\2w\2y\2{\2}\2\177\2\3\2%\4\2C\\c|\6\2\62;C\\aac|\3\2$$\5\2\62;CHc"+
		"h\3\2\629\3\2\62\63\4\2\13\13\"\"\4\2\f\f\17\17\3\2\62;\4\2CCcc\4\2DD"+
		"dd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2"+
		"MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4"+
		"\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u0156\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61"+
		"\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2"+
		"\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\3\u0081"+
		"\3\2\2\2\5\u0085\3\2\2\2\7\u0087\3\2\2\2\t\u008a\3\2\2\2\13\u008c\3\2"+
		"\2\2\r\u008e\3\2\2\2\17\u0090\3\2\2\2\21\u0092\3\2\2\2\23\u0094\3\2\2"+
		"\2\25\u0096\3\2\2\2\27\u00ab\3\2\2\2\31\u00ad\3\2\2\2\33\u00b0\3\2\2\2"+
		"\35\u00b8\3\2\2\2\37\u00bd\3\2\2\2!\u00c0\3\2\2\2#\u00c4\3\2\2\2%\u00c9"+
		"\3\2\2\2\'\u00ce\3\2\2\2)\u00d3\3\2\2\2+\u00d8\3\2\2\2-\u00de\3\2\2\2"+
		"/\u00e4\3\2\2\2\61\u00e9\3\2\2\2\63\u00ec\3\2\2\2\65\u00ef\3\2\2\2\67"+
		"\u00f6\3\2\2\29\u0100\3\2\2\2;\u0104\3\2\2\2=\u010a\3\2\2\2?\u0110\3\2"+
		"\2\2A\u0116\3\2\2\2C\u0119\3\2\2\2E\u0121\3\2\2\2G\u0125\3\2\2\2I\u012e"+
		"\3\2\2\2K\u0130\3\2\2\2M\u0132\3\2\2\2O\u0134\3\2\2\2Q\u0136\3\2\2\2S"+
		"\u0138\3\2\2\2U\u013a\3\2\2\2W\u013c\3\2\2\2Y\u013e\3\2\2\2[\u0140\3\2"+
		"\2\2]\u0142\3\2\2\2_\u0144\3\2\2\2a\u0146\3\2\2\2c\u0148\3\2\2\2e\u014a"+
		"\3\2\2\2g\u014c\3\2\2\2i\u014e\3\2\2\2k\u0150\3\2\2\2m\u0152\3\2\2\2o"+
		"\u0154\3\2\2\2q\u0156\3\2\2\2s\u0158\3\2\2\2u\u015a\3\2\2\2w\u015c\3\2"+
		"\2\2y\u015e\3\2\2\2{\u0160\3\2\2\2}\u0162\3\2\2\2\177\u0164\3\2\2\2\u0081"+
		"\u0082\7G\2\2\u0082\u0083\7S\2\2\u0083\u0084\7W\2\2\u0084\4\3\2\2\2\u0085"+
		"\u0086\7?\2\2\u0086\6\3\2\2\2\u0087\u0088\7,\2\2\u0088\u0089\7,\2\2\u0089"+
		"\b\3\2\2\2\u008a\u008b\7-\2\2\u008b\n\3\2\2\2\u008c\u008d\7*\2\2\u008d"+
		"\f\3\2\2\2\u008e\u008f\7+\2\2\u008f\16\3\2\2\2\u0090\u0091\7>\2\2\u0091"+
		"\20\3\2\2\2\u0092\u0093\7@\2\2\u0093\22\3\2\2\2\u0094\u0095\7]\2\2\u0095"+
		"\24\3\2\2\2\u0096\u0097\7_\2\2\u0097\26\3\2\2\2\u0098\u0099\7C\2\2\u0099"+
		"\u009a\7P\2\2\u009a\u00ac\7F\2\2\u009b\u009c\7Q\2\2\u009c\u00ac\7T\2\2"+
		"\u009d\u009e\7V\2\2\u009e\u009f\7K\2\2\u009f\u00a0\7O\2\2\u00a0\u00a1"+
		"\7G\2\2\u00a1\u00ac\7U\2\2\u00a2\u00a3\7R\2\2\u00a3\u00a4\7N\2\2\u00a4"+
		"\u00a5\7W\2\2\u00a5\u00ac\7U\2\2\u00a6\u00a7\7R\2\2\u00a7\u00a8\7N\2\2"+
		"\u00a8\u00a9\7W\2\2\u00a9\u00aa\7U\2\2\u00aa\u00ac\7E\2\2\u00ab\u0098"+
		"\3\2\2\2\u00ab\u009b\3\2\2\2\u00ab\u009d\3\2\2\2\u00ab\u00a2\3\2\2\2\u00ab"+
		"\u00a6\3\2\2\2\u00ac\30\3\2\2\2\u00ad\u00ae\7)\2\2\u00ae\u00af\7U\2\2"+
		"\u00af\32\3\2\2\2\u00b0\u00b1\5k\66\2\u00b1\u00b2\5Q)\2\u00b2\u00b3\5"+
		"[.\2\u00b3\u00b4\5]/\2\u00b4\u00b5\5s:\2\u00b5\u00b6\5e\63\2\u00b6\u00b7"+
		"\5k\66\2\u00b7\34\3\2\2\2\u00b8\u00b9\5k\66\2\u00b9\u00ba\5Q)\2\u00ba"+
		"\u00bb\5c\62\2\u00bb\u00bc\5i\65\2\u00bc\36\3\2\2\2\u00bd\u00be\5k\66"+
		"\2\u00be\u00bf\5Q)\2\u00bf \3\2\2\2\u00c0\u00c1\5o8\2\u00c1\u00c2\5M\'"+
		"\2\u00c2\u00c3\5e\63\2\u00c3\"\3\2\2\2\u00c4\u00c5\5o8\2\u00c5\u00c6\5"+
		"U+\2\u00c6\u00c7\5Y-\2\u00c7\u00c8\5M\'\2\u00c8$\3\2\2\2\u00c9\u00ca\5"+
		"o8\2\u00ca\u00cb\5U+\2\u00cb\u00cc\5Y-\2\u00cc\u00cd\5O(\2\u00cd&\3\2"+
		"\2\2\u00ce\u00cf\5o8\2\u00cf\u00d0\5U+\2\u00d0\u00d1\5Y-\2\u00d1\u00d2"+
		"\5Q)\2\u00d2(\3\2\2\2\u00d3\u00d4\5o8\2\u00d4\u00d5\5U+\2\u00d5\u00d6"+
		"\5Y-\2\u00d6\u00d7\5S*\2\u00d7*\3\2\2\2\u00d8\u00d9\5e\63\2\u00d9\u00da"+
		"\5M\'\2\u00da\u00db\5o8\2\u00db\u00dc\5c\62\2\u00dc\u00dd\5i\65\2\u00dd"+
		",\3\2\2\2\u00de\u00df\5e\63\2\u00df\u00e0\5M\'\2\u00e0\u00e1\5o8\2\u00e1"+
		"\u00e2\5[.\2\u00e2\u00e3\5]/\2\u00e3.\3\2\2\2\u00e4\u00e5\5u;\2\u00e5"+
		"\u00e6\5M\'\2\u00e6\u00e7\5o8\2\u00e7\u00e8\5s:\2\u00e8\60\3\2\2\2\u00e9"+
		"\u00ea\5g\64\2\u00ea\u00eb\5u;\2\u00eb\62\3\2\2\2\u00ec\u00ed\5\65\33"+
		"\2\u00ed\u00ee\7<\2\2\u00ee\64\3\2\2\2\u00ef\u00f3\t\2\2\2\u00f0\u00f2"+
		"\t\3\2\2\u00f1\u00f0\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3"+
		"\u00f4\3\2\2\2\u00f4\66\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f6\u00fa\7$\2\2"+
		"\u00f7\u00f9\n\4\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00fc\3\2\2\2\u00fa\u00f8"+
		"\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00fd\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fd"+
		"\u00fe\7$\2\2\u00fe8\3\2\2\2\u00ff\u0101\5K&\2\u0100\u00ff\3\2\2\2\u0101"+
		"\u0102\3\2\2\2\u0102\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103:\3\2\2\2"+
		"\u0104\u0106\7&\2\2\u0105\u0107\t\5\2\2\u0106\u0105\3\2\2\2\u0107\u0108"+
		"\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109<\3\2\2\2\u010a"+
		"\u010c\7B\2\2\u010b\u010d\t\6\2\2\u010c\u010b\3\2\2\2\u010d\u010e\3\2"+
		"\2\2\u010e\u010c\3\2\2\2\u010e\u010f\3\2\2\2\u010f>\3\2\2\2\u0110\u0112"+
		"\7\'\2\2\u0111\u0113\t\7\2\2\u0112\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114"+
		"\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115@\3\2\2\2\u0116\u0117\7)\2\2\u0117"+
		"\u0118\13\2\2\2\u0118B\3\2\2\2\u0119\u011a\t\b\2\2\u011a\u011b\3\2\2\2"+
		"\u011b\u011c\b\"\2\2\u011cD\3\2\2\2\u011d\u011f\7\17\2\2\u011e\u011d\3"+
		"\2\2\2\u011e\u011f\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0122\7\f\2\2\u0121"+
		"\u011e\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2"+
		"\2\2\u0124F\3\2\2\2\u0125\u0129\7=\2\2\u0126\u0128\n\t\2\2\u0127\u0126"+
		"\3\2\2\2\u0128\u012b\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u012a\3\2\2\2\u012a"+
		"\u012c\3\2\2\2\u012b\u0129\3\2\2\2\u012c\u012d\b$\2\2\u012dH\3\2\2\2\u012e"+
		"\u012f\t\2\2\2\u012fJ\3\2\2\2\u0130\u0131\t\n\2\2\u0131L\3\2\2\2\u0132"+
		"\u0133\t\13\2\2\u0133N\3\2\2\2\u0134\u0135\t\f\2\2\u0135P\3\2\2\2\u0136"+
		"\u0137\t\r\2\2\u0137R\3\2\2\2\u0138\u0139\t\16\2\2\u0139T\3\2\2\2\u013a"+
		"\u013b\t\17\2\2\u013bV\3\2\2\2\u013c\u013d\t\20\2\2\u013dX\3\2\2\2\u013e"+
		"\u013f\t\21\2\2\u013fZ\3\2\2\2\u0140\u0141\t\22\2\2\u0141\\\3\2\2\2\u0142"+
		"\u0143\t\23\2\2\u0143^\3\2\2\2\u0144\u0145\t\24\2\2\u0145`\3\2\2\2\u0146"+
		"\u0147\t\25\2\2\u0147b\3\2\2\2\u0148\u0149\t\26\2\2\u0149d\3\2\2\2\u014a"+
		"\u014b\t\27\2\2\u014bf\3\2\2\2\u014c\u014d\t\30\2\2\u014dh\3\2\2\2\u014e"+
		"\u014f\t\31\2\2\u014fj\3\2\2\2\u0150\u0151\t\32\2\2\u0151l\3\2\2\2\u0152"+
		"\u0153\t\33\2\2\u0153n\3\2\2\2\u0154\u0155\t\34\2\2\u0155p\3\2\2\2\u0156"+
		"\u0157\t\35\2\2\u0157r\3\2\2\2\u0158\u0159\t\36\2\2\u0159t\3\2\2\2\u015a"+
		"\u015b\t\37\2\2\u015bv\3\2\2\2\u015c\u015d\t \2\2\u015dx\3\2\2\2\u015e"+
		"\u015f\t!\2\2\u015fz\3\2\2\2\u0160\u0161\t\"\2\2\u0161|\3\2\2\2\u0162"+
		"\u0163\t#\2\2\u0163~\3\2\2\2\u0164\u0165\t$\2\2\u0165\u0080\3\2\2\2\r"+
		"\2\u00ab\u00f3\u00fa\u0102\u0108\u010e\u0114\u011e\u0123\u0129\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}