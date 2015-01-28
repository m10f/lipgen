package m10f.lipgen.parser;

import m10f.lipgen.lexer.LexerStream;
import m10f.lipgen.lexer.LexicalAnalyzer;
import m10f.lipgen.lexer.TokenIterator;
import m10f.lipgen.lexer.nfa.Nfa;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import m10f.lipgen.parser.lr.*;

import java.util.*;

import static org.junit.Assert.*;


// Test generation primitives for a simple grammar (taken from the dragon book)
// S -> CC
// C -> cC | d
public class CdGrammarTests {
    Symbol S = new Symbol("S");
    Symbol C = new Symbol("C");
    Symbol c = new Symbol("c");
    Symbol d = new Symbol("d");

    GrammarRule SCC;
    GrammarRule CcC;
    GrammarRule Cd;
    ArrayList<GrammarRule> rules;
    Symbol startSymbol;
    LRParserGenerator generator;

    LRParserGenerator makeGenerator() {
        return new LRParserGenerator(rules, startSymbol);
    }

    private GrammarRule makeRule(Symbol nt, Symbol... symbols) {
        ArrayList<GrammarProductionElement> gpes = new ArrayList<>();
        for(Symbol s : symbols)
            gpes.add(new GrammarProductionElement(s));

        return new GrammarRule(nt, gpes);
    }

    @Before
    public void setup() {
        SCC = makeRule(S, C, C);
        CcC = makeRule(C, c, C);
        Cd = makeRule(C, d);
        rules = new ArrayList<>();
        rules.add(SCC);
        rules.add(CcC);
        rules.add(Cd);
        startSymbol = S;
        generator = makeGenerator();
    }
    
    
    @Test
    public void testFirst() {
        Set<Symbol> symbols = generator.first(new LRItem(SCC, 1, generator.getEndSymbol()));

        assertEquals(2, symbols.size());
        assertTrue(symbols.contains(c));
        assertTrue(symbols.contains(d));
    }

    @Test
    public void testInitialClosure() {
        LRItem item = new LRItem(generator.getAugmentedStartRule(), 0, generator.getEndSymbol());
        Set<LRItem> startSingleton = Collections.singleton(item);
        Set<LRItem> closure = generator.itemSetClosure(startSingleton);

        assertEquals(6, closure.size());
        assertTrue(closure.contains(new LRItem(generator.getAugmentedStartRule(), 0, generator.getEndSymbol())));
        assertTrue(closure.contains(new LRItem(SCC, 0, generator.getEndSymbol())));
        assertTrue(closure.contains(new LRItem(CcC, 0, c)));
        assertTrue(closure.contains(new LRItem(CcC, 0, d)));
        assertTrue(closure.contains(new LRItem(Cd, 0, c)));
        assertTrue(closure.contains(new LRItem(Cd, 0, d)));
    }

    public Set<LRItem> makeItemSet(LRItem... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    @Test
    public void testCanonicalCollection() {
         Set<Set<LRItem>> itemSets = generator.computeItemSets();

        assertEquals(10, itemSets.size());
        Set<LRItem> i0 =  generator.itemSetClosure(makeItemSet(new LRItem(generator.getAugmentedStartRule(), 0, generator.getEndSymbol())));

        assertTrue(itemSets.contains(generator.itemSetGoto(i0, S)));
        assertTrue(itemSets.contains(generator.itemSetGoto(i0, C)));
        assertTrue(itemSets.contains(generator.itemSetGoto(i0, c)));
        assertTrue(itemSets.contains(generator.itemSetGoto(i0, d)));
    }

    @Test
    public void testParseTableGenerationTotals() {
        LRParsingTable table = generator.generateParsingTable();
        long actionStateCount = table.getActionTable().keySet().stream().map(m -> m.getState()).distinct().count();

        // Do some general size checks
        Assert.assertEquals(generator.computeItemSets().size(), actionStateCount);
        Assert.assertEquals(16, table.getActionTable().size());
        Assert.assertEquals(5, table.getGotoTable().size());

        // TODO: broader tests (possibly expose to the package the set->state mapping for unit testing purposes)
    }

    // transcribed from the dragon book
    public LRParsingTable buildCdTable() {
        Symbol endSymbol = new Symbol("!END", true);

        LRParsingTable table = new LRParsingTable();
        table.setInitialState(0);
        table.setEndSymbol(endSymbol);

        table.putAction(0, c, LRParsingAction.shiftAction(3));
        table.putAction(0, d, LRParsingAction.shiftAction(4));

        table.putAction(1, endSymbol, LRParsingAction.acceptAction());

        table.putAction(2, c, LRParsingAction.shiftAction(6));
        table.putAction(2, d, LRParsingAction.shiftAction(7));

        table.putAction(3, c, LRParsingAction.shiftAction(3));
        table.putAction(3, d, LRParsingAction.shiftAction(4));

        table.putAction(4, c, LRParsingAction.reduceAction(Cd));
        table.putAction(4, d, LRParsingAction.reduceAction(Cd));

        table.putAction(5, endSymbol, LRParsingAction.reduceAction(SCC));

        table.putAction(6, c, LRParsingAction.shiftAction(6));
        table.putAction(6, d, LRParsingAction.shiftAction(7));

        table.putAction(7, endSymbol, LRParsingAction.reduceAction(Cd));

        table.putAction(8, c, LRParsingAction.reduceAction(CcC));
        table.putAction(8, d, LRParsingAction.reduceAction(CcC));

        table.putAction(9, endSymbol, LRParsingAction.reduceAction(CcC));

        table.putGoto(0, S, 1);
        table.putGoto(0, C, 2);
        table.putGoto(2, C, 5);
        table.putGoto(3, C, 8);
        table.putGoto(6, C, 9);

        return table;
    }

    @Test
    public void testParser() throws Exception {
        ddTest(buildCdTable());
    }

    @Test
    public void testGeneratedParser() throws Exception {
        ddTest(generator.generateParsingTable());
    }

    public void ddTest(LRParsingTable table) throws Exception {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        lexer.addRule(c.getName(), new Nfa('c'));
        lexer.addRule(d.getName(), new Nfa('d'));

        LRParser parser = new LRParser(table);
        LexerStream stream = new LexerStream("dd");

        ParseTree parseTree = parser.parse(new TokenIterator(lexer, stream));
        Assert.assertEquals(S, parseTree.getSymbol());
        Assert.assertEquals(2, parseTree.getChildren().size());

        Assert.assertEquals(C, parseTree.getChildren().get(0).getSymbol());
        Assert.assertEquals(d, parseTree.getChildren().get(0).getChildren().get(0).getSymbol());

        Assert.assertEquals(C, parseTree.getChildren().get(1).getSymbol());
        Assert.assertEquals(d, parseTree.getChildren().get(1).getChildren().get(0).getSymbol());
    }

}