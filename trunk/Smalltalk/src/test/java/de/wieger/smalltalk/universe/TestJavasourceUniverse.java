package de.wieger.smalltalk.universe;

import org.testng.annotations.Test;


public class TestJavasourceUniverse {
    @Test
    public void testBoot() {
        JavasourceUniverse universe = new JavasourceUniverse();
        universe.boot();
    }

    @Test
    public void testGenerateJavaSources() {
        JavasourceUniverse universe = new JavasourceUniverse();
        universe.loadSmalltalkKernel();
        universe.load("src/main/smalltalk/sunit/SUnit-Kernel.st");
        universe.load("src/test/smalltalk/ExampleTest.st");
        universe.load("src/main/smalltalk/performance/fibonacci.st");
        universe.compileClasses();
    }
}
