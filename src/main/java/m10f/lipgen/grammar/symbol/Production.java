package m10f.lipgen.grammar.symbol;

import java.util.List;

public class Production {
    private String tag;
    private List<Symbol> elements;

    public Production(String tag, List<Symbol> elements) {
        this.tag = tag;
        this.elements = elements;
    }

    public String getTag() {
        return tag;
    }

    public List<Symbol> getElements() {
        return elements;
    }

}
