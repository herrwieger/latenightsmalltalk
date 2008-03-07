package smalltalk.shared;



public class InvocationFailed extends MethodInvocationFailed {
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public InvocationFailed(java.lang.Object pReceiver, java.lang.String pMethodName, java.lang.Object[] pArgs, Exception pEx) {
        super(pReceiver, pMethodName, pArgs, pEx);
    }
}
