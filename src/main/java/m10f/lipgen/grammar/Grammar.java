package m10f.lipgen.grammar;

import m10f.lipgen.grammar.symbol.GrammarRule;
import m10f.lipgen.grammar.symbol.Nonterminal;
import m10f.lipgen.grammar.symbol.Production;
import m10f.lipgen.grammar.symbol.Terminal;
import m10f.lipgen.lexer.LexicalAnalyzer;
import m10f.lipgen.lexer.nfa.Nfa;
import m10f.lipgen.lexer.nfa.RegexParser;
import m10f.lipgen.parser.lr.LRParseTableConflictException;
import m10f.lipgen.parser.lr.LRParserGenerator;
import m10f.lipgen.parser.lr.LRParsingTable;

import java.util.ArrayList;
import java.util.List;

// TODO: make this the unambigous grammar description?
public class Grammar {
    private Nonterminal startSymbol;
    private List<Terminal> terminals;
    private List<Nonterminal> nonterminals;
    private LexicalAnalyzer lexicalAnalyzer;

    public Grammar() {
        terminals = new ArrayList<>();
        nonterminals = new ArrayList<>();
        lexicalAnalyzer = new LexicalAnalyzer();
    }

    // TODO: write regex->nfa conversion

    public Terminal terminal(String symbolName, String regex) {
        return terminal(symbolName, RegexParser.current().parseRegex(regex));
    }

    public Terminal terminal(String symbolName, Nfa nfa) {
        Terminal t = new Terminal(symbolName);
        terminals.add(t);
        lexicalAnalyzer.addRule(t, nfa);
        return t;
    }

    public Nonterminal nonterminal(String symbolName) {
        Nonterminal nt = new Nonterminal(symbolName);
        nonterminals.add(nt);
        return nt;
    }

    public void skip(Nfa nfa) {
        lexicalAnalyzer.addSkip(nfa);
    }

    public List<GrammarRule> getRules() {
        ArrayList<GrammarRule> rules = new ArrayList<>();
        for(Nonterminal nonterminal : nonterminals) {
            for(Production production : nonterminal.getProductions()) {
                rules.add(new GrammarRule(nonterminal, production));
            }
        }
        return rules;
    }

    public Nonterminal getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(Nonterminal startSymbol) {
        this.startSymbol = startSymbol;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public List<Nonterminal> getNonterminals() {
        return nonterminals;
    }

    public LexicalAnalyzer getLexer() {
        return lexicalAnalyzer;
    }

    public LRParsingTable getLrParseTable() throws LRParseTableConflictException {
        LRParserGenerator generator = new LRParserGenerator(this);
        return generator.generateParsingTable();
    }

}
