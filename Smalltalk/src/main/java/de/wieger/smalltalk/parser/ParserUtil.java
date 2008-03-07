package de.wieger.smalltalk.parser;

import java.io.StringReader;


public class ParserUtil {
    public static SmalltalkParser getParser(String pStringToParse) {
        SmalltalkLexer  lexer   = new SmalltalkLexer(new StringReader(pStringToParse));
        SmalltalkParser parser  = new SmalltalkParser(lexer);
        return parser;
    }
}
