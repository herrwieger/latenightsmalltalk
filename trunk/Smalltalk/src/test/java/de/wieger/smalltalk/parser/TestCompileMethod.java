package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.wieger.smalltalk.script.SmalltalkScriptEngine;
import de.wieger.smalltalk.script.SmalltalkScriptEngineFactory;
import de.wieger.smalltalk.smile.ClassDescription;


public class TestCompileMethod {
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
    // test hello world
    //--------------------------------------------------------------------------

    @Test
    public void testHelloWorld() throws Exception {
        fScriptEngine.eval("Transcript show: 'Hello World!';cr;cr.");
    }



    //--------------------------------------------------------------------------
    // test basics
    //--------------------------------------------------------------------------

    @Test
    public void testReturn42() throws Exception {
        assertEquals(fScriptEngine.eval("^ 42"), fScriptEngine.newInteger(42));
    }



    @Test
    public void testCalc35() throws Exception {
        assertEquals(fScriptEngine.eval("^3 + 4 * 5 ."), fScriptEngine.newInteger(35));
    }

    @Test
    public void testCalc23() throws Exception {
        assertEquals(fScriptEngine.eval("^3 + (4 * 5) ."), fScriptEngine.newInteger(23));
    }

    @Test
    public void testCalcBoolean() throws Exception {
        assertSame(fScriptEngine.eval("^true & false ."), fScriptEngine.getFalse());
    }

    @Test
    public void testCalcBoolean1() throws Exception {
        assertSame(fScriptEngine.eval("^true & false | true ."), fScriptEngine.getTrue());
    }

    @Test
    public void testCalcBoolean2() throws Exception {
        assertSame(fScriptEngine.eval("^(true & false | true) not."), fScriptEngine.getFalse());
    }


    @Test
    public void testCascade() throws Exception {
        ClassDescription smallClass  = fScriptEngine.parseMethods(
                "Cascade",
                "noop",
                "return42 ^ 42",
                "testCascade ^ Cascade new noop;noop;return42"
        );
        assertEquals(fScriptEngine.compileAndRun(smallClass, "testCascade"), fScriptEngine.newInteger(42));
    }



    //--------------------------------------------------------------------------
    // nil handling
    //--------------------------------------------------------------------------

    @Test
    public void testObjectIsNotNil() throws Exception {
        assertSame(fScriptEngine.eval("^self notNil"), fScriptEngine.getTrue());
    }

    @Test
    public void testNilIsNil() throws Exception {
        assertSame(fScriptEngine.eval("^nil isNil"), fScriptEngine.getTrue());
    }


    //--------------------------------------------------------------------------
    // test blocks
    //--------------------------------------------------------------------------

    @Test
    public void testBlock() throws Exception {
        fScriptEngine.eval("[Transcript show:'Hello Block';cr.] value.");
    }


    @Test
    public void testSelfInsideBlock() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
            "TestSelfInsideBlock",
            "block " +
            "^ true ifTrue: [self class name].");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "block"), fScriptEngine.newString("TestSelfInsideBlock class"));
    }

    @Test
    public void testParseMethodWithBlock() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
            "TestParseMethodWithBlock",
            "block | i |" +
            "i := 0. " +
            "^ i = 0 ifTrue: [ i := 3].");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "block"), fScriptEngine.newInteger(3));
    }


    @Test
    public void testBlock1() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestBlock1",
                "block [:arg || doubleArg | doubleArg:=arg + arg."
                + " Transcript show:doubleArg;cr.] value: 7");
        fScriptEngine.compileAndRun(classDescription, "block");
    }

    // @Test
    public void testBlock2() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestBlock2",
                "testScope "+
                "| index block | " +
                "index:=5. " +
                "block:=[Transcript show: index. index:=42]. " +
                "index:=13. " +
                "block value. " +
                "index. ");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testScope"), fScriptEngine.newInteger(42));
    }


    @Test
    public void testBlock3() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestBlock3",
                "testScope: pIndex "+
                "|block sum| " +
                "sum:=2." +
                "block:=[Transcript show: pIndex. sum:=sum + pIndex. pIndex:=23]. " +
                "block value. " +
                "block value. " +
                "pIndex:=15. " +
                "block value. " +
                "^ sum. ");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testScope", fScriptEngine.newInteger(42)), fScriptEngine.newInteger(2+42+23+15));
    }

    @Test
    public void testBlockReturn() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestBlockReturn",
                "testBlock "+
                "[true ifTrue:[^42]. 23] value." +
                "13." );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testBlock"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testBlockSuperInvocation() throws Exception {
        ClassDescription parentClassDescription  = fScriptEngine.parseMethods(
                "TestBlockSuperInvocationParent",
                "method "+
                "^ 'Parent'" );

        ClassDescription childClassDescription = parentClassDescription.subclass("TestBlockSuperInvocation");
        fScriptEngine.parseMethod(
                childClassDescription,
                "method "+
                "^ [super method] value" );
        assertEquals(fScriptEngine.compileAndRun(childClassDescription, "method").toString(), "Parent");
    }

    @Test
    public void testParameterAccessFromInnerBlock() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "TestParameterAccessFromInnerBlock",
                "testBlock "+
                "[:x| [^x] value] value:42.");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testBlock"), fScriptEngine.newInteger(42));
    }



    //--------------------------------------------------------------------------
    // test control structures
    //--------------------------------------------------------------------------


    @Test
    public void testIfTrue() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "IfTrue",
                "myMethod ^true ifTrue:[^42]");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "myMethod"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testIfTrue1() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "IfTrue1",
                "myMethod ^false ifTrue:[^42] ifFalse:[^23]");
        assertEquals(fScriptEngine.compileAndRun(classDescription, "myMethod"), fScriptEngine.newInteger(23));
    }

    @Test
    public void testSuperMethod() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "SuperClass",
                "superClass ^super class name");
        assertNotNull(fScriptEngine.compileAndRun(classDescription, "superClass"));
//        assertEquals(fScriptEngine.compileAndRun(classDescription, "superClass"), fScriptEngine.newString("SuperClass"));//TODO
    }


    @Test
    public void testMethodWithMultipleArgs() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "MultipleArgs",
                "add: v0 to: v1 to: v2 to:v3 " +
                "|sum| " +
                "sum:= v0 + v1 + v2 + v3." +
                "^ sum.",
                "getSum " +
                "^ self add:1+2 to:2*2 to:3-1 to:4+5"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "getSum"), fScriptEngine.newInteger(1+2 + 2*2 + (3-1) + 4+5));
    }



    //--------------------------------------------------------------------------
    // test basicNew
    //--------------------------------------------------------------------------

    @Test
    public void testVariableSubclass() throws Exception {
        fScriptEngine.getBaseClass().variableSubclass("MyArray", "", "", "", "");
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "VariableSubclass",
                "newArray " +
                "|myArray| " +
                "myArray := MyArray basicNew:20." +
                "myArray basicAt:3 put:'Hello'"
            );
        fScriptEngine.compileAndRun(classDescription, "newArray");
    }



    //--------------------------------------------------------------------------
    // test exceptions
    //--------------------------------------------------------------------------

    @Test
    public void testExceptionReturn() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ExceptionReturn",
                "testExceptionReturn" +
                "[Exception signal. 13] on:Exception do:[:ex | ex return: 42]"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testExceptionReturn"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testExceptionPass() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ExceptionPass",
                "testExceptionPass" +
                "[[Exception signal. 13] on:Exception do:[:ex | ex pass. 23]] on:Exception do:[:ex | ex return:42]"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testExceptionPass"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testExceptionRetry() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ExceptionRetry",
                "testExceptionRetry " +
                "|i| " +
                "i:=0." +
                "[(i = 0) ifTrue:[Exception signal]. i] on:Exception do:[:ex | i:=42. ex retry]"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testExceptionRetry"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testExceptionRetryUsing() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ExceptionRetryUsing",
                "testExceptionRetryUsing " +
                "|i| " +
                "i:=0." +
                "[(i = 0) ifTrue:[Exception signal]] on:Exception do:[:ex | ex retryUsing:[21 * 2]]"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testExceptionRetryUsing"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testExceptionResume() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ExceptionResume",
                "testExceptionResume " +
                "|i| " +
                "i:=0." +
                "[(i = 0) ifTrue:[Exception signal]] on:Exception do:[:ex | i:=42. ex resume]." +
                "^ i"
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testExceptionResume"), fScriptEngine.newInteger(42));
    }

    @Test
    public void testExceptionResumeWith() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "ExceptionResumeWith",
                "testExceptionResumeWith " +
                "|i| " +
                "i:=0." +
                "^ [(i = 0) ifTrue:[Exception signal]] on:Exception do:[:ex | ex resume:42]."
            );
        assertEquals(fScriptEngine.compileAndRun(classDescription, "testExceptionResumeWith"), fScriptEngine.newInteger(42));
    }


    @Test(expectedExceptions={RuntimeException.class})
    public void testDefaultAction() throws Exception {
        ClassDescription classDescription  = fScriptEngine.parseMethods(
                "DefaultAction",
                "testDefaultAction " +
                "Exception signal"
            );
        fScriptEngine.compileAndRun(classDescription, "testDefaultAction");
    }
}
