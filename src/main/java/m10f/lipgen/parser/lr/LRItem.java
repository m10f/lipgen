package m10f.lipgen.parser.lr;

import m10f.lipgen.parser.Symbol;
import m10f.lipgen.parser.GrammarProductionElement;
import m10f.lipgen.parser.GrammarRule;

import java.util.Optional;

public class LRItem {
    private GrammarRule rule;
    private int parseLocation;
    private Symbol lookahead;

    public LRItem(GrammarRule rule, int parseLocation, Symbol lookahead) {
        this.rule = rule;
        this.parseLocation = parseLocation;
        this.lookahead = lookahead;
    }

    public int getParseLocation() {
        return parseLocation;
    }

    public Optional<Symbol> nextSymbol() {
        if(parseLocation < rule.getProduction().size())
            return Optional.of(rule.getProduction().get(parseLocation).getSymbol());
        else
            return Optional.empty();
    }

    public GrammarRule getGrammarRule() {
        return rule;
    }

    public Symbol getLookahead() {
        return lookahead;
    }

    public LRItem nextParseLocation() {
        if(parseLocation >= rule.getProduction().size())
            throw new IllegalArgumentException();
        return new LRItem(rule, parseLocation + 1, lookahead);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LRItem that = (LRItem) o;

        if (parseLocation != that.parseLocation) return false;
        if (!lookahead.equals(that.lookahead)) return false;
        return rule.equals(that.rule);

    }

    @Override
    public int hashCode() {
        int result = rule.hashCode();
        result = 31 * result + parseLocation;
        result = 31 * result + lookahead.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getNonterminalSymbol().getName());
        sb.append(" -> ");
        int i = 0;
        for(GrammarProductionElement gpe : rule.getProduction()) {
            if(i++ == parseLocation)
                sb.append(" . ");
            sb.append(" ");
            sb.append(gpe.getSymbol().getName());
            sb.append(" ");
        }
        if(!nextSymbol().isPresent())
            sb.append(" . ");
        sb.append(",");
        sb.append(lookahead.getName());
        return sb.toString();
    }
}
