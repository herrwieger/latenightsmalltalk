package smalltalk;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.wieger.smalltalk.parser.TestCompileMethod;
import de.wieger.smalltalk.script.SmalltalkScriptEngine;
import de.wieger.smalltalk.script.SmalltalkScriptEngineFactory;
import de.wieger.smalltalk.smile.ClassDescription;


public class TestReflection {
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
    public void testClassOfObject() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ClassOfObject",
                "getClass " +
                "^ ClassOfObject new class name"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "getClass").toString(), "ClassOfObject class");
    }

    @Test
    public void testIsKindOf() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "IsKindOfClass",
                "testIsKindOf" +
                "^ (self isKindOf:Object) & (self isKindOf:IsKindOfClass)"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testIsKindOf"), fScriptEngine.getTrue());
    }

    @Test
    public void testIsMemberOf() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "IsMemberOfClass",
                "testIsMemberOf" +
                "^ ((self isMemberOf:Object) not) & (self isMemberOf:IsMemberOfClass)"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testIsMemberOf"), fScriptEngine.getTrue());
    }

    @Test
    public void testAllSuperclasses() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestAllSuperclasses",
                "testAllSuperclasses " +
                "|mySuperclasses|" +
                "mySuperclasses := OrderedCollection allSuperclasses." +
                "((mySuperclasses at:1) = IndexedCollection) ifFalse:[Exception signal]." +
                "((mySuperclasses at:2) = Collection) ifFalse:[Exception signal]." +
                "((mySuperclasses at:3) = Magnitude) ifFalse:[Exception signal]." +
                "((mySuperclasses at:4) = Object) ifFalse:[Exception signal]."
            );
        fScriptEngine.compileAndRun(classDescription, "testAllSuperclasses");
    }


    @Test
    public void testSelectors() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestSelectors",
                "testSelectors " +
                "(Object selectors includes: #isKindOf:) ifFalse:[Error signal]." +
                "(Object selectors includes: #yourself) ifFalse:[Error signal]." +
                "(Object selectors includes: #perform:with:with:with:) ifFalse:[Error signal]." +
                "Transcript show: (Object selectors sort " +
                "select:[:each |('test*' match: each) and: [each numArgs isZero]])."
            );
        fScriptEngine.compileAndRun(classDescription, "testSelectors");
    }


    @Test
    public void testReflection() throws java.lang.Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestBasicReflection",
                "testReflection "
            );
        fScriptEngine.compileAndRun(classDescription, "testReflection");
    }
}
