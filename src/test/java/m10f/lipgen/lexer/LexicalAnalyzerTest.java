package m10f.lipgen.lexer;

import org.junit.Test;
import m10f.lipgen.lexer.nfa.Nfa;
import static org.junit.Assert.*;

public class LexicalAnalyzerTest {
    @Test
    public void basicLexerTest() throws Exception {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        String integerSymbol = "integer";
        String plusSymbol = "plus";
        String minusSymbol = "minus";
        String doublePlusSymbol = "plusplus";

        analyzer.addRule(integerSymbol, new Nfa('1').concat(new Nfa('0').alternate(new Nfa('1')).closure()));
        analyzer.addRule(plusSymbol, new Nfa('+'));
        analyzer.addRule(doublePlusSymbol, Nfa.fromString("++"));
        analyzer.addRule(minusSymbol, new Nfa('-'));
        analyzer.addSkip(new Nfa(' ').closure());

        LexerStream stream = new LexerStream("10 + 11 -   1 ++");
        Token token = analyzer.getNextToken(stream);
        assertEquals(integerSymbol, token.getSymbol());
        assertEquals("10", token.getLexeme());

        token = analyzer.getNextToken(stream);
        assertEquals(plusSymbol, token.getSymbol());

        token = analyzer.getNextToken(stream);
        assertEquals(integerSymbol, token.getSymbol());
        assertEquals("11", token.getLexeme());

        token = analyzer.getNextToken(stream);
        assertEquals(minusSymbol, token.getSymbol());

        token = analyzer.getNextToken(stream);
        assertEquals(integerSymbol, token.getSymbol());
        assertEquals("1", token.getLexeme());

        token = analyzer.getNextToken(stream);
        assertEquals(doublePlusSymbol, token.getSymbol());
    }
}