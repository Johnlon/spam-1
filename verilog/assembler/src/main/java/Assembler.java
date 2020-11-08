
import org.antlr.v4.runtime.*;

public class Assembler {
    public static void main(String[] args) {
        System.err.println("RUN");
        SPAM1Lexer lexer = new SPAM1Lexer(CharStreams.fromString(
            "start:\n" +
                    "VAL1: EQU #1\n" +
                    "VAL2: EQU #$ff\n" +
                    "VAL3: EQU #$abcd\n" +
                    "VAL4: EQU #:VAL1\n" +
                    "start1: ;comment\n" +
                    "\n" +
                    "label: REGA=#1\n" +
                    "\n" +
                    "; assign ram\n" +
                    "[#:label]=REGB\n" +
                    "RAM=REGB\n" +
                    "[#:label]=REGB'S\n" +
                    "RAM=REGB'S\n" +
                    "\n" +
                    "REGA=REGB\n" +
                    "REGA=REGA PLUS [#:label]\n" +
                    "REGA=REGB PLUS'S REGC\n" +
                    "REGA=REGB PLUS REGC\n" +
                    "\n" +
                    "\n" +
                    "; imediate values\n" +
                    "REGA=#12\n" +
                    "REGA=#12'S\n" +
                    "REGA=#$ff\n" +
                    "REGA=#$ff'S\n" +
                    "; imediate values by labels\n" +
                    "REGA=#:label\n" +
                    "REGA=#:label'S\n" +
                    "\n" +
                    "; ram access by register\n" +
                    "REGA=RAM\n" +
                    "REGA=RAM'S\n" +
                    "; ram access direct\n" +
                    "REGA=[#12]\n" +
                    "REGA=[#12]'S\n" +
                    "REGA=[#$ff]\n" +
                    "REGA=[#$ff]'S\n" +
                    "REGA=[#:label]\n" +
                    "REGA=[#:label]'S\n" +
                    ";ops\n"+
                "\n"
        ));

//        Token token = lexer.nextToken();
//        while (token.getType() != Token.EOF) {
//            System.err.println(token);
//            token = lexer.nextToken();
//        }
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SPAM1Parser parser = new SPAM1Parser(tokens);
        SPAM1Parser.CompileContext ctx = parser.compile();
//
//        SPAM1Listener l = new SPAM1BaseListener() {
//            @Override
//            public void enterCompile(SPAM1Parser.CompileContext ctx) {
//                System.err.println("\n=====================================\n");
//                System.err.println("COMPILE:");
//                System.err.println("RI  : " + ctx.getRuleIndex());
//                System.err.println(ctx.getText().substring(0,40));
//                ctx.line().forEach(l -> l.enterRule(this));
//            }
//
//            @Override
//            public void enterLine(SPAM1Parser.LineContext ctx) {
//                System.err.println("\n=====================================\n");
//                System.err.println("LINE  :");
//                System.err.println("COUNT : " + ctx.getChildCount());
//                System.err.println("RI    : " + ctx.getRuleIndex());
//                System.err.println(ctx.getText());
//
//                ctx.children.forEach(t-> System.err.println("CHILD  " + t.getText()));
//                for (int i = 0; i < ctx.getChildCount(); i++) {
//                    System.err.println("-> " + ctx.getChild(i).getText());
//                }
//            }
//
//            @Override
//            public void enterComment(SPAM1Parser.CommentContext ctx) {
//                System.err.println("\n=====================================\n");
//                System.err.println("COMMENT:");
//                System.err.println("RI  : " + ctx.getRuleIndex());
//                System.err.println(ctx.getText().substring(0,40));
//            }
//
//            @Override
//            public void enterInstruction(SPAM1Parser.InstructionContext ctx) {
//                System.err.println("\n=====================================\n");
//                System.err.println("INSTRUCTION  :");
//                System.err.println("COUNT : " + ctx.getChildCount());
//                System.err.println("RI    : " + ctx.getRuleIndex());
//                System.err.println(ctx.getText());
//            }
//
//
//        };

//        parser.compile().enterRule(l)
//        System.err.println(ctx);
        System.out.flush();
        System.err.println("done");
    }
}


