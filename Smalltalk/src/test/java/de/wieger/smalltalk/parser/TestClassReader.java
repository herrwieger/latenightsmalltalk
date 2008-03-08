package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.testng.annotations.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.wieger.smalltalk.universe.JavassistUniverse;


public class TestClassReader implements ErrorListener {

    @Test
    public void testClassReader() throws FileNotFoundException, RecognitionException, TokenStreamException {
        ClassReaderLexer lexer  = new ClassReaderLexer(new FileReader("src/main/smalltalk/object.st"));
        ClassReader      reader = new ClassReader(lexer);
        reader.setClassDescriptionManager(new JavassistUniverse());
        reader.fileIn();
    }


    @Test
    public void testParseExpressions() {
        String      expression = "Behavior class subclass: #Class instanceVariableNames: '' classVariableNames: '' "
                                         + "poolDictionaries: '' category: ''";
        ClassReader reader     = new ClassReader((ClassReaderLexer) null);
        reader.addErrorListener(this);
        reader.setClassDescriptionManager(new JavassistUniverse());
        reader.parseExpression(expression, 0, 0);
        assertEquals(reader.getParsedClassDescriptions().size(), 2);
    }
    


    // --------------------------------------------------------------------------
    // ErrorListener methods
    // --------------------------------------------------------------------------

    @Override
    public void error(String pMessage, int pStart, int pEnd) {
        fail(pMessage);
    }
}
