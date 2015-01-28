package m10f.lipgen.parser;

import java.util.*;

public class GrammarRule {
    private Symbol nonterminalSymbol;
    private Optional<String> tag;
    private List<GrammarProductionElement> production;

    public GrammarRule(Symbol nonterminalSymbol, List<GrammarProductionElement> production) {
        this.nonterminalSymbol = nonterminalSymbol;
        this.production = production;
        this.tag = Optional.empty();
    }

    public GrammarRule(Symbol nonterminalSymbol, String tag, List<GrammarProductionElement> production) {
        this.nonterminalSymbol = nonterminalSymbol;
        this.tag = Optional.of(tag);
        this.production = production;
    }

    public Symbol getNonterminalSymbol() {
        return nonterminalSymbol;
    }

    public Optional<String> getTag() {
        return tag;
    }

    public List<GrammarProductionElement> getProduction() {
        return production;
    }
}
