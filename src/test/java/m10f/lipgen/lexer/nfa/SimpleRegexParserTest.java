package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleRegexParserTest {
    @Test
    public void testBasic() {
        assertMatch("k", "k");
        assertMatch("kk", "kk");
        assertMatch(".k", "mk", "sk", "kk");
        assertMatch("(..)k", "mmk", "ssk");
        assertMatch("kk|mm", "kk", "mm");
        assertMatch("km*|bb", "k", "km", "kmmmmm", "bb");
        assertMatch("(mu)*ppy", "ppy", "muppy", "mumuppy");
        assertMatch("mu.*?ppy", "muppy", "mueppy");
        assertMatch("mu+ppy", "muppy", "muuppy");
        assertMatch("mu?ppy", "mppy", "muppy");
    }

    private void assertMatch(String regex, String... inputs) {
        for(String input : inputs) {
            LexerStream lxStream = new LexerStream(input);
            Nfa matcher = RegexParser.current().parseRegex(regex);
            assertTrue(regex + " did not match string '" + input + "'", matcher.accept(lxStream));
            assertTrue(regex + " did not match entire string '" + input + "'", lxStream.endOfStream());
        }
    }
}