package de.wieger.smalltalk.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import de.wieger.smalltalk.universe.ClassDescriptionManager;


public class ParserFactory {

    // --------------------------------------------------------------------------
    // SmalltalkParser methods
    // --------------------------------------------------------------------------

    public static SmalltalkParser getParser(String pStringToParse) {
        SmalltalkLexer  lexer  = new SmalltalkLexer(new StringReader(pStringToParse));
        SmalltalkParser parser = new SmalltalkParser(lexer);
        return parser;
    }



    // --------------------------------------------------------------------------
    // ClassReader methods
    // --------------------------------------------------------------------------

    public static ClassReader getClassReader(String pStringToParse, ClassDescriptionManager pClassDescriptionManager,
            MethodDescriptionFactory pMethodDescriptionFactory) {
        ClassReaderLexer lexer  = new ClassReaderLexer(new StringReader(pStringToParse));
        ClassReader      reader = getClassReader(pClassDescriptionManager, pMethodDescriptionFactory, lexer);

        return reader;
    }

    public static ClassReader getClassReaderForFile(String pFileName, ClassDescriptionManager pClassDescriptionManager)
            throws FileNotFoundException {
        ClassReaderLexer lexer  = new ClassReaderLexer(new FileReader(pFileName));
        ClassReader reader = new ClassReader(lexer);
        reader.setup(pClassDescriptionManager, reader);

        return reader;
    }

    private static ClassReader getClassReader(ClassDescriptionManager pClassDescriptionManager,
            MethodDescriptionFactory pMethodDescriptionFactory, ClassReaderLexer lexer) {
        ClassReader reader = new ClassReader(lexer);
        reader.setup(pClassDescriptionManager, pMethodDescriptionFactory);
        return reader;
    }
}
