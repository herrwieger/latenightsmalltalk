package smalltalk.shared;


public class DoesNotUnderstand extends MethodInvocationFailed {
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public DoesNotUnderstand(java.lang.Object pReceiver, java.lang.String pMethodName, java.lang.Object[] pArgs, Exception pEx) {
        super(pReceiver, pMethodName, pArgs, pEx);
    }
}
