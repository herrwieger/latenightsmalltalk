package experimental;

import java.lang.reflect.Method;

import org.testng.annotations.Test;


public class TestDynaInvokeByCast {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final int     MAX_ITERATION = 100000000;
    private static final Integer VALUE         = Integer.valueOf(42);

    

    //--------------------------------------------------------------------------  
    // class variables
    //--------------------------------------------------------------------------

    private static Foo sfFoo = new Foo();


    
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    @Test(invocationCount=2)
    public void testInvokeByClassCast() {
        long    startTime = System.currentTimeMillis();

        for (long i=0; i<MAX_ITERATION; i++) {
            ((Foo)getFooObject()).doFoo(VALUE);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("testInvokeByClassCast=" + elapsedTime + " ,numFoos=" + getFoo().fNumFoos);
    }

    @Test(invocationCount=2)
    public void testInvokeByInterfaceCast() {
        long    startTime = System.currentTimeMillis();

        for (long i=0; i<MAX_ITERATION; i++) {
            ((FooInterface)getFooObject()).doFoo(VALUE);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("testInvokeByInterfaceCast=" + elapsedTime + " ,numFoos=" + getFoo().fNumFoos);
    }
    
    
    @Test(invocationCount=2)
    public void testInvokeNatural() {
        long    startTime = System.currentTimeMillis();

        Integer value = Integer.valueOf(42);
        for (long i=0; i<MAX_ITERATION; i++) {
            getFoo().doFoo(value);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("testInvokeNatural=" + elapsedTime + " ,numFoos=" + getFoo().fNumFoos);
    }

    @Test(invocationCount=2)
    public void testInvokeByReflection() throws Exception {
        Method method = Foo.class.getMethod("doFoo", new Class[]{Object.class});
        long    startTime = System.currentTimeMillis();

        for (long i=0; i<MAX_ITERATION; i++) {
            method.invoke(getFooObject(), VALUE);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("testInvokeByReflection=" + elapsedTime + " ,numFoos=" + getFoo().fNumFoos);
    }
    

    private Foo getFoo() {
        return sfFoo;
    }

    private Object getFooObject() {
        return sfFoo;
    }


    
    //--------------------------------------------------------------------------  
    // interface and class foo
    //--------------------------------------------------------------------------

    public static interface FooInterface {

        public abstract boolean doFoo(Object pNum);

    }

    public static class Foo implements FooInterface {
        long fNumFoos;

        /* (non-Javadoc)
         * @see experimental.FooInterface#doFoo(java.lang.Object)
         */
        public boolean doFoo(Object pNum) {
            int intValue = ((Integer) pNum).intValue();
            fNumFoos++;
            return intValue % 2 == 0;
        }
    }
}
