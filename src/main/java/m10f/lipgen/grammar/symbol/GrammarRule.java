package m10f.lipgen.grammar.symbol;

public class GrammarRule {
    private final Nonterminal nonterminal;
    private final Production production;

    public GrammarRule(Nonterminal nonterminal, Production production) {
        this.nonterminal = nonterminal;
        this.production = production;
    }

    public Nonterminal getNonterminal() {
        return nonterminal;
    }

    public Production getProduction() {
        return production;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GrammarRule that = (GrammarRule) o;

        if (!nonterminal.equals(that.nonterminal)) return false;
        if (!production.equals(that.production)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nonterminal.hashCode();
        result = 31 * result + production.hashCode();
        return result;
    }
}
