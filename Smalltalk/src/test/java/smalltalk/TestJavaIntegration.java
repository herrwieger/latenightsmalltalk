package smalltalk;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.wieger.smalltalk.parser.TestCompileMethod;
import de.wieger.smalltalk.script.SmalltalkScriptEngine;
import de.wieger.smalltalk.script.SmalltalkScriptEngineFactory;


public class TestJavaIntegration {
    // --------------------------------------------------------------------------
    // class variables
    // --------------------------------------------------------------------------

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
    
    
    
    // --------------------------------------------------------------------------
    // test methods
    // --------------------------------------------------------------------------

    @Test
    public void testJavaIntegration() throws java.lang.Exception {
        fScriptEngine.load("src/test/smalltalk/JavaIntegration.st");
        fScriptEngine.compileClasses();
        java.lang.Class javaTestClass = fScriptEngine.getClassNamed("JavaIntegration");
        fScriptEngine.run(javaTestClass, "helloWorld");
    }
}
