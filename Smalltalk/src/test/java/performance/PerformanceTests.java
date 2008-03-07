package performance;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.wieger.smalltalk.script.SmalltalkScriptEngine;
import de.wieger.smalltalk.script.SmalltalkScriptEngineFactory;


public class PerformanceTests {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final int FIBONACCI = 36;
    private static Class sfFibonacciClass;

    
    
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
    // fibonacci
    //--------------------------------------------------------------------------


    @Test(invocationCount=2)
    public void testSmalltalkFibonacci() throws Exception {
        java.lang.Class exampleTestClass = getSmalltalkFibonacciClass();
        long millis = System.currentTimeMillis();
        Object fibonacci = fScriptEngine.run(exampleTestClass, "fibonacci", fScriptEngine.newInteger(20));
        long elapsedMillis = (System.currentTimeMillis() - millis);
        System.out.println("smalltalk fibonacci=" + fibonacci + ",millis=" + elapsedMillis);
    }

    private java.lang.Class getSmalltalkFibonacciClass() {
        if (sfFibonacciClass!=null) {
            return sfFibonacciClass;
        }
        fScriptEngine.load("src/main/smalltalk/performance/fibonacci.st");
        fScriptEngine.compileClasses();
        sfFibonacciClass    = fScriptEngine.getClassNamed("Fibonacci class");
        return sfFibonacciClass;
    }
}
