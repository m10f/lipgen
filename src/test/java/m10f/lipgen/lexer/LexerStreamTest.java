package m10f.lipgen.lexer;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class LexerStreamTest {
    @Test
    public void TestLexerStream() {
        String input = "foo bar";
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
        LexerStream ls = new LexerStream(new InputStreamReader(bais));

        int pos = ls.position();
        assertEquals('f', ls.readChar());
        assertEquals('o', ls.readChar());
        assertEquals('o', ls.readChar());
        ls.position(pos);
        assertEquals('f', ls.readChar());
        assertEquals('o', ls.readChar());
        assertEquals('o', ls.readChar());
        assertEquals("foo", ls.currentLexeme());
        assertEquals("foo", ls.consume());
        assertEquals(' ', ls.readChar());
        assertEquals('b', ls.readChar());
        assertEquals('a', ls.readChar());
        assertEquals('r', ls.readChar());
    }
}