package experimental;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;

import org.testng.annotations.Test;


public class TestJavaAssist {
    @Test
    public void testJavaAssist() throws CannotCompileException {
        ClassPool   classPool   = new ClassPool();
        classPool.appendSystemPath();

        CtClass     ctClass     = classPool.makeClass("Test");
        CtNewMethod.make("public String foox(){return experimental.TestJavaAssist.foo();}", ctClass);
        CtNewMethod.make("public String foo(){return experimental.TestJavaAssist.foo();}", ctClass);
    }


    public static final String foo() {
        return "foo";
    }
}
