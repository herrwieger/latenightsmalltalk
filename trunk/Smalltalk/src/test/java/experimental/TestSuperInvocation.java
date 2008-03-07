package experimental;

import static org.testng.Assert.*;

import org.testng.annotations.Test;


public class TestSuperInvocation {

    @Test
    public void testSuperInvocation() {
        Child   myChild = new Child();

        assertEquals(myChild.getName(), "Child");
        assertEquals(((Parent)myChild).getName(), "Child");
    }

    @Test
    public void testInnerChild() {
        assertEquals(new Child().new InnerChild().test(), "Parent");
    }


    public static class Parent {
        public String getName() {
            return "Parent";
        }
    }

    public static class Middle extends Parent {
    }

    public static class Child extends Middle {
        @Override
        public String getName() {
            return "Child";
        }

        public class InnerChild {
            public String test() {
                return Child.super.getName();
            }
        }
    }
}
