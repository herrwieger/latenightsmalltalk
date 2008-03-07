package de.wieger.smalltalk.universe;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.wieger.smalltalk.smile.ClassDescription;



public class TestCompilerUtil {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private JavassistUniverse fUniverse;
    private CompilerUtil      fCompilerUtil;



    // --------------------------------------------------------------------------
    // test methods
    // --------------------------------------------------------------------------

    @BeforeMethod
    public void setUp() throws NotFoundException, CannotCompileException {
        fUniverse = new JavassistUniverse();
        fUniverse.makeCurrentUniverse();
        fCompilerUtil = fUniverse.getCompilerUtil();
    }

    @AfterMethod
    public void tearDown() {
        AbstractUniverse.clearCurrentUniverse();
    }


    @Test(expectedExceptions = (RuntimeException.class))
    public void testFindOrSubclassInheritanceCheck() throws Exception {
        ClassDescription parentClass = fUniverse.getBaseClass().subclass("Parent");
        ClassDescription wrongChildClass = parentClass.subclass("WrongChild");

        fUniverse.createClasses(fUniverse.getClassDescriptionsToCompile());
        fCompilerUtil.findOrSubclass(parentClass);
        fCompilerUtil.findOrSubclass(wrongChildClass);
    }
}
