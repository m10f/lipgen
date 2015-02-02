package m10f.lipgen.lexer;

import m10f.lipgen.grammar.symbol.Terminal;
import org.junit.Test;
import m10f.lipgen.lexer.nfa.Nfa;
import static org.junit.Assert.*;

public class LexicalAnalyzerTest {
    @Test
    public void basicLexerTest() throws Exception {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        Terminal integerSymbol = new Terminal("integer");
        Terminal plusSymbol = new Terminal("plus");
        Terminal minusSymbol = new Terminal("minus");
        Terminal doublePlusSymbol = new Terminal("plusplus");
        Terminal commentSymbol = new Terminal("comment");

        Nfa integerNfa = new Nfa('1').concat(new Nfa('0').alternate(new Nfa('1')).closure());
        analyzer.addRule(integerSymbol, integerNfa);
        analyzer.addRule(plusSymbol, new Nfa('+'));
        analyzer.addRule(doublePlusSymbol, Nfa.fromString("++"));
        analyzer.addRule(minusSymbol, new Nfa('-'));
        analyzer.addSkip(new Nfa(' ').concat(new Nfa(' ').closure()));
        analyzer.addRule(commentSymbol, Nfa.fromString("(*").concat(Nfa.matchAny().closure().withGreed(false)).concat(Nfa.fromString("*)")));

        LexerStream stream = new LexerStream("(*comment1*) 10 + 11 -   1 ++ (*comment2*)");
        Token token = analyzer.getNextToken(stream);
        assertEquals(commentSymbol, token.getSymbol());
        assertEquals("(*comment1*)", token.getLexeme());

        token = analyzer.getNextToken(stream);
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

        token = analyzer.getNextToken(stream);
        assertEquals(commentSymbol, token.getSymbol());
        assertEquals("(*comment2*)", token.getLexeme());
    }
}