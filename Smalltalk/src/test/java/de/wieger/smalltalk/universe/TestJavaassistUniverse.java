package de.wieger.smalltalk.universe;

import static org.testng.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javassist.CtMethod;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import smalltalk.shared.MethodInvoker;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.MethodDescription;


public class TestJavaassistUniverse {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    private static final String YOGI                = "Yogi";

    private static final String STATIC_HELLO_INT    = "static hello int";
    private static final String STATIC_HELLO_STRING = "static hello string";

    private static final String HELLO_INT           = "hello int";
    private static final String HELLO_STRING        = "hello string";

    

    //--------------------------------------------------------------------------
    // test methods
    //--------------------------------------------------------------------------

    @BeforeMethod
    public void setUp() {
        AbstractUniverse.clearCurrentUniverse();
    }


    @Test
    public void testMetaclassesHierarchy() {
        JavassistUniverse    universe = new JavassistUniverse();
        assertEquals(7, universe.getNumberOfClassDescriptions());

        ClassDescription objectClazz = universe.getClassDescription("Object");
        assertEquals(objectClazz.getSuperClass(), null);

        ClassDescription clazzClazz = objectClazz.getClazz();
        assertEquals(clazzClazz.getName(), "Class");
        ClassDescription classMetaclass = clazzClazz.getClazz();
        assertEquals(classMetaclass.getName(), "Class class");
        assertEquals(classMetaclass.getClazz().getName(), "Metaclass");

        ClassDescription behaviorClazz = clazzClazz.getSuperClass();
        assertEquals(behaviorClazz.getName(), "Behavior");
        assertEquals(behaviorClazz.getClazz().getName(), "Behavior class");
    }

    @Test
    public void testInheritance() {
        JavassistUniverse universe = new JavassistUniverse();
        universe.load("src/test/smalltalk/inheritance.st");
    }

    @Test
    public void testVariableSubClass() throws Exception {
        JavassistUniverse universe = new JavassistUniverse();
        universe.boot();
        ClassDescription variableSubclass = universe.getBaseClass().variableSubclass("Yoda", "", "", "", "");
        universe.compilePass1(variableSubclass.getClazz());
        universe.compilePass1(variableSubclass);
        universe.compilePass2(variableSubclass, new HashMap<MethodDescription, CtMethod>());
    }


    @Test
    public void testNewObject() throws InstantiationException, IllegalAccessException {
        JavassistUniverse universe = new JavassistUniverse();
        universe.makeCurrentUniverse();
        universe.boot();
        universe.getClassNamed("Object").newInstance();
    }


    @Test
    public void testNewInstance() throws InstantiationException, IllegalAccessException {
        JavassistUniverse universe = new JavassistUniverse();
        universe.makeCurrentUniverse();
        universe.boot();
        universe.getBaseClass().subclass(YOGI);
        universe.compileClasses();
        universe.getClassNamed(YOGI).newInstance();
    }

    @Test
    public void testBootNewInstance() throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        JavassistUniverse universe = new JavassistUniverse();
        universe.makeCurrentUniverse();
        universe.boot();
        universe.getClassNamed("Object").newInstance();
        universe.getClassNamed("Block").newInstance();
        universe.getClassNamed("Boolean").newInstance();
        universe.newInteger(3);
    }

    @Test
    public void testBootAndWriteToFile() {
        JavassistUniverse universe = new JavassistUniverse();
        universe.makeCurrentUniverse();
        universe.boot();
        universe.storeClasses("C:/Temp");
    }


    @Test
    public void testBootTwice() {
        bootUniverse();
        bootUniverse();
    }

    private JavassistUniverse bootUniverse() {
        JavassistUniverse universe = new JavassistUniverse();
        universe.makeCurrentUniverse();
        universe.boot();

        return universe;
    }

    @Test
    public void testClassClassMethod() throws InstantiationException, IllegalAccessException {
        JavassistUniverse universe = new JavassistUniverse();
        universe.makeCurrentUniverse();
        universe.boot();
        MethodInvoker.initParameterTypes(universe.getClassNamed("Object"));

        universe.getBaseClass().subclass(YOGI);
        universe.compileClasses();

        Class clazz = universe.getClassNamed(YOGI);
        assertNotNull(clazz);
        Object classClass = MethodInvoker.invokeStatic(clazz, JavaCodingUtil.GET_CLAZZ_METHOD_NAME);// trigger static initializer
        assertEquals(classClass.getClass().getName(), JavaCodingUtil.getQualifiedClassname(YOGI+ " class"));

        clazz = universe.getClassNamed("Class");
        classClass = MethodInvoker.invokeStatic(clazz, JavaCodingUtil.GET_CLAZZ_METHOD_NAME);// trigger static initializer
        assertEquals(classClass.getClass().getName(), JavaCodingUtil.getQualifiedClassname("Class class"));
    }


    //--------------------------------------------------------------------------  
    // test java integration methods
    //--------------------------------------------------------------------------

    @Test
    public void testGetJavaClassBridge() {
        JavassistUniverse javassistUniverse   = new JavassistUniverse();
        javassistUniverse.makeCurrentUniverse();
        javassistUniverse.boot();
        MethodInvoker.initParameterTypes(javassistUniverse.getClassNamed("Object"));
        
        Object            bridgeClassInstance = javassistUniverse.getJavaClassBridge(HashMap.class.getName());
        Object            newHashMap          = MethodInvoker.invoke(bridgeClassInstance,
                                                        JavaCodingUtil.getJavaMethodNameForSelector("new"));
        assertEquals(newHashMap.getClass().getName(), javassistUniverse.getJavaInstanceBridgeClassname(HashMap.class));
        
        javassistUniverse.getJavaClassBridge("javax.swing.JFrame");
    }

    @Test
    public void testInvokeStaticJavaClassBridgeMethod() {
        JavassistUniverse javassistUniverse   = new JavassistUniverse();
        javassistUniverse.makeCurrentUniverse();
        javassistUniverse.boot();
        MethodInvoker.initParameterTypes(javassistUniverse.getClassNamed("Object"));
        
        Object bridgeClassInstance = javassistUniverse.getJavaClassBridge(ObjectWithOverriddenMethods.class.getName());
        Object result              = MethodInvoker.invoke(bridgeClassInstance, "staticHelloWorld",
                                             new Object[] { javassistUniverse.newInteger(3) });
        assertEquals(result, javassistUniverse.newString(STATIC_HELLO_INT));
        
        result = MethodInvoker.invoke(bridgeClassInstance, "staticHelloWorld",
                         new Object[] { javassistUniverse.newString("Fred") });
        assertEquals(result, javassistUniverse.newString(STATIC_HELLO_STRING));
    }
    
    @Test
    public void testGetJavaInstanceBridgeForNull()  {
        JavassistUniverse javassistUniverse = new JavassistUniverse();
        javassistUniverse.makeCurrentUniverse();
        javassistUniverse.boot();
        
        Object bridgedNull = javassistUniverse.getJavaInstanceBridge(null);
        assertNull(bridgedNull);
    }
    
    @Test
    public void testGetJavaInstanceBridgeForSmalltalkObject()  {
        JavassistUniverse javassistUniverse = new JavassistUniverse();
        javassistUniverse.makeCurrentUniverse();
        javassistUniverse.boot();
        
        Object newString = javassistUniverse.newString("leonidas");
        Object bridgedSmalltalkobject = javassistUniverse.getJavaInstanceBridge(newString);
        assertSame(bridgedSmalltalkobject, newString);
    }

    @Test
    public void testGetJavaInstanceBridgeForObjectWithOverriddenMethods()  {
        JavassistUniverse javassistUniverse = new JavassistUniverse();
        javassistUniverse.makeCurrentUniverse();
        javassistUniverse.boot();
        MethodInvoker.initParameterTypes(javassistUniverse.getClassNamed("Object"));
        
        Object bridgedSmalltalkobject = javassistUniverse.getJavaInstanceBridge(new ObjectWithOverriddenMethods());
        Object result = MethodInvoker.invoke(bridgedSmalltalkobject, "helloWorld", new Object[]{javassistUniverse.newInteger(3)});
        assertEquals(result, javassistUniverse.newString(HELLO_INT));
        
        result = MethodInvoker.invoke(bridgedSmalltalkobject, "helloWorld",
                         new Object[] { javassistUniverse.newString("leonidas") });
        assertEquals(result, javassistUniverse.newString(HELLO_STRING));
    }
    
    @Test
    public void testGetJavaInstanceBridge()  {
        JavassistUniverse javassistUniverse = new JavassistUniverse();
        javassistUniverse.makeCurrentUniverse();
        javassistUniverse.boot();
        MethodInvoker.initParameterTypes(javassistUniverse.getClassNamed("Object"));
        
        Object bridgedMap          = javassistUniverse.getJavaInstanceBridge(new HashMap());
        Object key                 = javassistUniverse.newString("key");
        Object value               = javassistUniverse.newString("value");
        MethodInvoker.invoke(bridgedMap, "put", new Object[]{key, value});
        Object result = MethodInvoker.invoke(bridgedMap, "get", new Object[]{key});
        assertEquals(result, value);
    }

    public static class ObjectWithOverriddenMethods {
        public static String staticHelloWorld(int pHello) {
            return STATIC_HELLO_INT;
        }
        
        public static String staticHelloWorld(String pHello) {
            return STATIC_HELLO_STRING;
        }
        
        public String helloWorld(int pHello) {
            return HELLO_INT;
        }

        public String helloWorld(String pHello) {
            return HELLO_STRING;
        }
    }
}
