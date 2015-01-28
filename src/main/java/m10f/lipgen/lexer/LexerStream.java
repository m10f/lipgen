package m10f.lipgen.lexer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class LexerStream {
    private Reader inner;
    private StringBuilder buffer;
    private int bufferIdx;

    public LexerStream(Reader inner) {
        this.inner = inner;
        buffer = new StringBuilder();
    }

    public LexerStream(String string) {
        this(new StringReader(string));
    }

    public int readChar() {
        if(bufferIdx <  buffer.length())
            return buffer.charAt(bufferIdx++);
        else {
            try {
                int readResult = inner.read();
                if(readResult == -1) {
                    return -1;
                } else {
                    buffer.append((char)readResult);
                    bufferIdx++;
                    return (char)readResult;
                }
            } catch(IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public boolean endOfStream() {
        int initialPosition = position();
        boolean result = readChar() < 0;
        position(initialPosition);
        return result;
    }

    public int position() {
        return bufferIdx;
    }

    public void position(int newPos) {
        if(newPos > buffer.length())
            throw new IllegalArgumentException();
        bufferIdx = newPos;
    }

    public String currentLexeme() {
        return buffer.substring(0, bufferIdx);
    }

    public int currentLexemeLength() {
        return bufferIdx;
    }

    public String consume() {
        String lexeme = currentLexeme();
        buffer = new StringBuilder(buffer.substring(bufferIdx));
        bufferIdx = 0;
        return lexeme;
    }
}
