package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import java.io.StringReader;

import org.testng.annotations.Test;

import antlr.TokenStreamException;


public class TestLexer {
    @Test
    public void testLexer() throws TokenStreamException {
        SmalltalkLexer  lexer = new SmalltalkLexer(new StringReader("super"));
        assertEquals( lexer.nextToken().getType(), SmalltalkTokenTypes.SUPER);

        lexer = new SmalltalkLexer(new StringReader("'Doedel von Schnoesel'"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.STRING);

        lexer = new SmalltalkLexer(new StringReader("keyword:"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.KEYWORD);

        lexer = new SmalltalkLexer(new StringReader(":blockarg"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.BLOCK_ARGUMENT);

        lexer = new SmalltalkLexer(new StringReader("snack\"smalltak ist dufte\":=3"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.IDENTIFIER);
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.ASSIGNMENT_OPERATOR);
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.NUMBER);

        lexer = new SmalltalkLexer(new StringReader("java.util.Map.myMap"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.SCOPED_IDENTIFIER);

        lexer = new SmalltalkLexer(new StringReader("+"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.MOST_BINARY_SELECTORS);

        lexer = new SmalltalkLexer(new StringReader("|"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.PIPE);

        lexer = new SmalltalkLexer(new StringReader("||"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.PIPES_2);

        lexer = new SmalltalkLexer(new StringReader("|||"));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.PIPES_3);

        lexer = new SmalltalkLexer(new StringReader("1."));
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.NUMBER);
        assertEquals(lexer.nextToken().getType(), SmalltalkTokenTypes.DOT);
    }
}
