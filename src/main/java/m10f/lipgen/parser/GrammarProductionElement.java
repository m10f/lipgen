package m10f.lipgen.parser;

import java.util.Optional;

public class GrammarProductionElement {
    private Symbol symbol;
    private Optional<String> tag;

    public GrammarProductionElement(Symbol symbol) {
        this.symbol = symbol;
        this.tag = Optional.empty();
    }

    public GrammarProductionElement(Symbol symbol, String tag) {
        this.symbol = symbol;
        this.tag = Optional.of(tag);
    }

    public Optional<String> getTag() {
        return tag;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
