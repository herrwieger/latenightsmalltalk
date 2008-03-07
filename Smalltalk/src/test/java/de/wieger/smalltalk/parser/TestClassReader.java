package de.wieger.smalltalk.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.testng.annotations.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.wieger.smalltalk.universe.JavassistUniverse;


public class TestClassReader {
    @Test
    public void testClassReader() throws FileNotFoundException, RecognitionException, TokenStreamException {
        ClassReaderLexer    lexer   = new ClassReaderLexer(new FileReader("src/main/smalltalk/object.st"));
        ClassReader         reader  = new ClassReader(lexer);
        reader.setClassDescriptionManager(new JavassistUniverse());
        reader.fileIn();
    }
}
