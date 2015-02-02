package m10f.lipgen.grammar.symbol;

public class Tagged<T extends Symbol> implements Symbol {
    private final T sub;
    private final String tag;

    public Tagged(T sub, String tag) {
        this.sub = sub;
        this.tag = tag;
    }

    @Override
    public String getName() {
        return sub.getName();
    }

    @Override
    public String getTag() {
        return tag;
    }

    public T getTarget() {
        return sub;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Symbol))
            return false;
        return ((Symbol) o).getName() == getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
