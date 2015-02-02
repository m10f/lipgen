package m10f.lipgen.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nonterminal implements Symbol {
    private final String symbolName;
    private final List<Production> productions;

    public Nonterminal(String symbolName) {
        this.symbolName = symbolName;
        this.productions = new ArrayList<>();
    }

    @Override
    public String getName() {
        return symbolName;
    }

    @Override
    public String getTag() {
        return null;
    }

    public Tagged<Nonterminal> tag(String tag) {
        return new Tagged<>(this, tag);
    }

    public Nonterminal withRule(String ruleTag, Symbol... symbols) {
        productions.add(new Production(ruleTag, Arrays.asList(symbols)));
        return this;
    }

    public GrammarRule addRule(String ruleTag, Symbol... productionSymbols) {
        Production p = new Production(ruleTag, Arrays.asList(productionSymbols));
        productions.add(p);
        return new GrammarRule(this, p);
    }

    public List<Production> getProductions() {
        return productions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Symbol)) return false;

        Symbol that = (Symbol) o;

        if (!symbolName.equals(that.getName())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return symbolName.hashCode();
    }
}
