package m10f.lipgen.parser.lr;

import m10f.lipgen.grammar.symbol.GrammarRule;
import m10f.lipgen.lexer.LexicalException;
import m10f.lipgen.lexer.Token;
import m10f.lipgen.lexer.TokenIterator;
import m10f.lipgen.parser.ParseTree;
import m10f.lipgen.parser.SyntaxException;

import java.util.*;

public class LRParser {
    LRParsingTable parseTable;

    public LRParser(LRParsingTable parseTable) {
        this.parseTable = parseTable;
    }

    public ParseTree parse(TokenIterator tokenStream) throws LexicalException, SyntaxException {
        Stack<Long> stateStack = new Stack<>();
        Stack<ParseTree> symbolStack = new Stack<>();
        stateStack.push(parseTable.getInitialState());

        ParseTree.Leaf currentToken = getNextToken(tokenStream);

        while(true) {
            Optional<LRParsingAction> action = parseTable.getAction(stateStack.peek(), currentToken.getSymbol());
            if(!action.isPresent()) {
                if (currentToken.getSymbol() != parseTable.getEndSymbol())
                    throw new SyntaxException("syntax error before " + currentToken.getLexeme()); // TODO: give some more information
                else
                    throw new SyntaxException("unexpected end of input");
            }


            switch (action.get().actionType()) {
                case SHIFT:
                    stateStack.push(action.get().getShiftState());
                    symbolStack.push(currentToken);
                    currentToken = getNextToken(tokenStream);
                    break;

                case REDUCE:
                    GrammarRule reductionRule = action.get().getReduceRule();
                    ParseTree[] children = new ParseTree[reductionRule.getProduction().getElements().size()];
                    for(int idx = reductionRule.getProduction().getElements().size() - 1; idx >= 0; idx--) {
                        stateStack.pop();
                        children[idx] = symbolStack.pop();
                    }
                    ParseTree reduction = new ParseTree.Node(reductionRule, Arrays.asList(children));
                    symbolStack.push(reduction);
                    stateStack.push(parseTable.getGoto(stateStack.peek(), reductionRule.getNonterminal()).get());
                    break;

                case ACCEPT:
                    return symbolStack.pop();
            }
        }
    }

    private ParseTree.Leaf getNextToken(TokenIterator tokenStream) throws LexicalException {
        if(tokenStream.hasNext()) {
            Token token = tokenStream.next();
            return new ParseTree.Leaf(token.getSymbol(), token.getLexeme());
        }
        return new ParseTree.Leaf(parseTable.getEndSymbol(), "");
    }
}
