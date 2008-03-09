package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.universe.JavassistUniverse;
import de.wieger.smalltalk.universe.Universe;



public class TestAbstractClassReader  implements ErrorListener {
    //--------------------------------------------------------------------------
    // inner classes
    //--------------------------------------------------------------------------

    private static class ClassReader extends AbstractClassReader {
        public ClassReader(int pK_) {
            super(pK_);
        }
    }


    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Universe               sfUniverse      = new JavassistUniverse();
    private static final AbstractClassReader    sfClassReader   = new ClassReader(0);



    //--------------------------------------------------------------------------
    // class methods
    //--------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() {
        sfClassReader.setup(sfUniverse, sfClassReader);
    }


    @BeforeTest
    public void setUp() {
        sfClassReader.addErrorListener(this);
    }

    
    
    //--------------------------------------------------------------------------
    // test methods
    //--------------------------------------------------------------------------

    @Test
    public void testGetString() {
        assertEquals(sfClassReader.getString(sfClassReader.getTokenizer("''")), "");
        assertEquals(sfClassReader.getString(sfClassReader.getTokenizer(" '' ")), "");
        assertEquals(sfClassReader.getString(sfClassReader.getTokenizer(" ' ' ")), " ");
        assertEquals(sfClassReader.getString(sfClassReader.getTokenizer(" ' dodi dido ' ")), " dodi dido ");
    }

    @Test
    public void testParseSubclassExpression() {
        final String SUBCLASS_EXPRESSION =
            "Object subclass: #TestClass\n"
            + "instanceVariableNames: ''\n"
            + "classVariableNames: ''\n"
            + "poolDictionaries: ''\n"
            + "category: ''\n";

        sfClassReader.parseExpression(SUBCLASS_EXPRESSION, 0, 0);
        ClassDescription  smallClass = sfUniverse.getClassDescription("TestClass");
        assertEquals(smallClass.getName(), "TestClass");


        final String SUBCLASS_EXPRESSION_2 =
            "Object subclass: #Behavior\n"
              + "instanceVariableNames: 'name instanceSize methods superClass "
              + "variables classVariables poolDictionaries category'\n"
              + "classVariableNames: ''\n" + "poolDictionaries: ''\n"
              + "category: ''";

        sfClassReader.parseExpression(SUBCLASS_EXPRESSION_2, 0, 0);
        smallClass = sfUniverse.getClassDescription("Behavior");
        assertEquals(smallClass.getName(), "Behavior");
        assertNotNull(smallClass.getInstanceVariable("name"));
        assertNotNull(smallClass.getInstanceVariable("category"));
    }

    @Test
    public void testParseAddInterface() {
        final String INTERFACE_EXPRESSION =
            "Object addInterface:'de.wieger.IFooBar'";
        sfClassReader.parseExpression(INTERFACE_EXPRESSION, 0, 0);
        ClassDescription smallClass = sfUniverse.getClassDescription("Object");
        assertEquals(smallClass.getInterfaces().size(), 1);
    }

    @Test
    public void testParseAddInterfaceToClass() {
        final String INTERFACE_EXPRESSION =
            "Object class addInterface:'de.wieger.IFooBar'";
        sfClassReader.parseExpression(INTERFACE_EXPRESSION, 0, 0);
        ClassDescription smallClass = sfUniverse.getClassDescription("Class");
        assertEquals(smallClass.getInterfaces().size(), 1);
    }

    @Test
    public void testParseAddConstructor() {
        final String CONSTRUCTOR_EXPRESSION =
            "Object addConstructor:'public Object(int pValue){fValue=pValue;}'";
        sfClassReader.parseExpression(CONSTRUCTOR_EXPRESSION, 0, 0);
        ClassDescription smallClass = sfUniverse.getClassDescription("Object");
        assertEquals(smallClass.getNativeConstructors().size(), 1);
    }

    @Test
    public void testParseAddMethod() {
        final String METHOD_EXPRESSION =
            "Object addMethod:'private java.lang.Object $perform(java.lang.Object pSymbol, java.lang.Object[] pArguments) {"
                + "Symbol  methodName  = (Symbol)pSymbol;"
                + "return MethodInvoker.invoke(this, methodName.toString(), pArguments);"
                + "}'";
        sfClassReader.parseExpression(METHOD_EXPRESSION, 0, 0);
        ClassDescription smallClass = sfUniverse.getClassDescription("Object");
        assertEquals(smallClass.getNativeMethods().size(), 1);
    }

    @Test
    public void testParseAddField() {
        final String FIELD_EXPRESSION =
            "Object addField:'private int fValue;'";
        sfClassReader.parseExpression(FIELD_EXPRESSION, 0, 0);
        ClassDescription smallClass = sfUniverse.getClassDescription("Object");
        assertEquals(smallClass.getNativeFields().size(), 1);
    }


    @Test
    public void testParseMethodsFor() {
        sfUniverse.getBaseClass().subclass("Class");

        sfClassReader.parseMethodsFor("Class methodsFor: 'accessing'", 0, 0);
        assertEquals(sfClassReader.getClassForMethods().getName(), "Class");
        assertEquals(sfClassReader.getCategoryForMethods(), "accessing");
    }

   

    // --------------------------------------------------------------------------
    // ErrorListener methods
    // --------------------------------------------------------------------------

    @Override
    public void error(String pMessage, int pStart, int pEnd) {
        fail(pMessage);
    }
}