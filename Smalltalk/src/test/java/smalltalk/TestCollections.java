package smalltalk;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.wieger.smalltalk.parser.TestCompileMethod;
import de.wieger.smalltalk.script.SmalltalkScriptEngine;
import de.wieger.smalltalk.script.SmalltalkScriptEngineFactory;
import de.wieger.smalltalk.smile.ClassDescription;


public class TestCollections {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(TestCompileMethod.class);



    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private SmalltalkScriptEngine    fScriptEngine;


    //--------------------------------------------------------------------------  
    // initialization
    //--------------------------------------------------------------------------

    @BeforeClass
    public void setUp() {
        fScriptEngine = (SmalltalkScriptEngine)new SmalltalkScriptEngineFactory().getScriptEngine();
    }

    
    
    //--------------------------------------------------------------------------
    // test methods
    //--------------------------------------------------------------------------

    @Test
    public void testArrayBasics() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ArrayBasics",
                "testArrayBasics " +
                "|myArray|" +
                "myArray := Array new:1. " +
                "myArray at:1 put:42." +
                "((myArray at:1) = 42) ifFalse:[Exception signal]." +
                "((myArray size) = 1) ifFalse:[Exception signal]."
            );
        fScriptEngine.compileAndRun(classDescription, "testArrayBasics");
    }

    @Test
    public void testStringLiteral() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestStringLiteral",
                "testStringLiteral " +
                "^ 'Yoda'"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testStringLiteral").toString(), "Yoda");
    }

    @Test
    public void testStringBasics() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestStringBasics",
                "testStringBasics " +
                "('Vader' copyFrom:3 to:5) = 'der' ifFalse:[Exception signal]." +
                "$V < $Y ifFalse:[Exception signal]." +
                "('Yoda' at:1) = $Y ifFalse:[Exception signal]." +
                "('Yoda' at:3) = $d ifFalse:[Exception signal]." +
                "'Yoda' ~= 'Yod' ifFalse:[Exception signal]." +
                "'Yoda' = 'Yoda' ifFalse:[Exception signal]." +
                "'Vader' < 'Yoda' ifFalse:[Exception signal]." +
                "('Vader' at:3 put:$t) = 'Vater' ifFalse:[Exception signal]." +
                "('Vader' at:3 put:('Vader' at:3)) = 'Vader' ifFalse:[Exception signal]."
            );
        fScriptEngine.compileAndRun(classDescription, "testStringBasics");
    }


    @Test
    public void testStringCopyFromTo() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestStringCopyFromTo",
                "testStringCopyFromTo " +
                "(('Vader' copyFrom:3 to:5) = 'der') ifFalse:[Exception signal]."
        );
        fScriptEngine.compileAndRun(classDescription, "testStringCopyFromTo");
    }

    @Test
    public void testStringMatch() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestStringMatch",
                "testStringMatch " +
                "('*der' match:'Vader') ifFalse:[Exception signal]." +
                "('#a*e#' match:'Vader') ifFalse:[Exception signal]."
        );
        fScriptEngine.compileAndRun(classDescription, "testStringMatch");
    }


    @Test
    public void testOrderedCollectionBasics() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "OrderedCollectionBasics",
                "testOrderedCollectionBasics " +
                "|myCollection|" +
                "myCollection := OrderedCollection new. " +
                "myCollection add:'Tick';add:'Trick';add:'Track'." +
                "(myCollection at:3) = 'Track' ifFalse:[Exception signal]." +
                "myCollection add:'Donald' beforeIndex:2." +
                "(myCollection at:2) = 'Donald' ifFalse:[Exception signal]." +
                "(myCollection at:3) = 'Trick' ifFalse:[Exception signal]." +
                "myCollection sort"
            );
        fScriptEngine.compileAndRun(classDescription, "testOrderedCollectionBasics");
    }


    @Test
    public void testLinkedListBasics() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "LinkedListBasics",
                "testLinkedListBasics " +
                "|myList|" +
                "myList := List new. " +
                "myList add:42." +
                "((myList size) = 1) ifFalse:[Exception signal]."
            );
        fScriptEngine.compileAndRun(classDescription, "testLinkedListBasics");
    }
}
