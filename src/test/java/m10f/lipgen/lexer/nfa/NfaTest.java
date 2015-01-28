package m10f.lipgen.lexer.nfa;

import m10f.lipgen.lexer.LexerStream;
import org.junit.Assert;
import org.junit.Test;


public class NfaTest {
    @Test
    public void testBasic() {
        // (a*b)|cc
        Nfa nfa = new Nfa('a')
                .closure()
                .concat(new Nfa('b'))
                .alternate(new Nfa('c').concat(new Nfa('c')));

        Assert.assertTrue(nfaAcceptsEntire(nfa, "b"));
        Assert.assertTrue(nfaAcceptsEntire(nfa, "ab"));
        Assert.assertTrue(nfaAcceptsEntire(nfa, "aab"));
        Assert.assertTrue(nfaAcceptsEntire(nfa, "aaaaaaaaaaaaab"));
        Assert.assertTrue(nfaAcceptsEntire(nfa, "cc"));

        Assert.assertFalse(nfaAcceptsEntire(nfa, "a"));
        Assert.assertFalse(nfaAcceptsEntire(nfa, "bcc"));
        Assert.assertFalse(nfaAcceptsEntire(nfa, "abcc"));
        Assert.assertFalse(nfaAcceptsEntire(nfa, "c"));
        Assert.assertFalse(nfaAcceptsEntire(nfa, ""));
        Assert.assertFalse(nfaAcceptsEntire(nfa, "ba"));
        Assert.assertFalse(nfaAcceptsEntire(nfa, ""));
    }

    private boolean nfaAcceptsEntire(Nfa nfa, String input) {
        LexerStream stream = new LexerStream(input);
        return nfa.accept(stream) && stream.endOfStream();
    }
}