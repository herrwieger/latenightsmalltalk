package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import java.io.FileNotFoundException;

import org.testng.annotations.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.wieger.smalltalk.universe.JavassistUniverse;


public class TestClassReader {
    @Test
    public void testFileIn() throws FileNotFoundException, RecognitionException, TokenStreamException {
        ClassReader      reader = ParserFactory.getClassReaderForFile("src/main/smalltalk/object.st", new JavassistUniverse());
        reader.fileIn();
    }


    @Test
    public void testParseExpressions() {
        String      expression = "Behavior class subclass: #Class instanceVariableNames: '' classVariableNames: '' "
                                         + "poolDictionaries: '' category: ''";
        ClassReader reader     = new ClassReader((ClassReaderLexer) null);
        reader.setup(new JavassistUniverse(), reader);
        reader.parseExpression(expression, 0, 0);
        assertEquals(reader.getParsedClassDescriptions().size(), 2);
    }
}
