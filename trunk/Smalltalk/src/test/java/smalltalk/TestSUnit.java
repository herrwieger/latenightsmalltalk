package smalltalk;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.wieger.smalltalk.parser.TestCompileMethod;
import de.wieger.smalltalk.script.SmalltalkScriptEngine;
import de.wieger.smalltalk.script.SmalltalkScriptEngineFactory;


public class TestSUnit {
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
        fScriptEngine.load("src/main/smalltalk/sunit/SUnit-Kernel.st");
    }
    
    
    
    //--------------------------------------------------------------------------
    // test methods
    //--------------------------------------------------------------------------

    @Test
    public void testSUnit() throws java.lang.Exception {
        fScriptEngine.load("src/test/smalltalk/ExampleTest.st");
        fScriptEngine.compileClasses();
        java.lang.Class     exampleTestClass    = fScriptEngine.getClassNamed("ExampleTest class");
        fScriptEngine.run(exampleTestClass, "run", fScriptEngine.newSymbol("testArray"));
    }
}
