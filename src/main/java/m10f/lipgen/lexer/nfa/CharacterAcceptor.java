package m10f.lipgen.lexer.nfa;

public interface CharacterAcceptor {
    public boolean accept(char c);

    public static class Literal implements CharacterAcceptor {
        private final char character;
        public Literal(char c) {
            character = c;
        }

        @Override
        public boolean accept(char c) {
            return c == character;
        }
    }

    public static class Any implements CharacterAcceptor {

        @Override
        public boolean accept(char c) {
            return true;
        }
    }
}
