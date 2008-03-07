package smalltalk.internal;

import smalltalk.shared.MethodInvoker;
import de.wieger.smalltalk.universe.AbstractUniverse;


public class NilUtil {
    //--------------------------------------------------------------------------
    // class methods
    //--------------------------------------------------------------------------

    public static Object isNil(Object pObject) {
        if (pObject == null) {
            return AbstractUniverse.getTrue();
        }
        return MethodInvoker.invoke(pObject, "isNil");
    }



    public static Object notNil(Object pObject) {
        if (pObject == null) {
            return AbstractUniverse.getFalse();
        }
        return MethodInvoker.invoke(pObject, "notNil");
    }
}
