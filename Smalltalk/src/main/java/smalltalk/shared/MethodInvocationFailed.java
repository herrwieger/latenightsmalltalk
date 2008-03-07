package smalltalk.shared;

import java.util.Arrays;

import de.wieger.commons.lang.ReflectionUtil;


public abstract class MethodInvocationFailed extends RuntimeException {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private java.lang.Object      fReceiver;
    private java.lang.String      fMethodName;
    private java.lang.Object[]    fArgs;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public MethodInvocationFailed(java.lang.Object pReceiver, java.lang.String pMethodName, java.lang.Object[] pArgs, Exception pEx) {
        super(pEx);

        fReceiver       = pReceiver;
        fMethodName     = pMethodName;
        fArgs           = pArgs;
    }



    //--------------------------------------------------------------------------
    // object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public java.lang.String toString() {
        return getClass().getName()
            + "[receiverClass=" + ReflectionUtil.getClassNameNullSafe(fReceiver) + ",receiver=" + fReceiver
            + ",methodName=" + fMethodName + ",args=" + Arrays.toString(fArgs)
            + ",rootCause=" + getCause() + "]";
    }
}
