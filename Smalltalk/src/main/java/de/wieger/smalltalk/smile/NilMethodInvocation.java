package de.wieger.smalltalk.smile;



public class NilMethodInvocation implements Statement {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Value   fReceiver;
    private String  fMethodName;
    private Value   fResultTempVar;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public NilMethodInvocation(Value pReceiver, String pSelector, TemporaryVariable pResultTempVar) {
        fReceiver       = pReceiver;
        fMethodName     = pSelector;
        fResultTempVar  = pResultTempVar;

        fReceiver.markReadAccess();
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public Value getReceiver() {
        return fReceiver;
    }
    
    public Value getResultTempVar() {
        return fResultTempVar;
    }    

    public String getMethodName() {
        return fMethodName;
    }

    
    
    //--------------------------------------------------------------------------
    // Statement methods
    //--------------------------------------------------------------------------

    public void accept(StatementVisitor pVisitor) {
        pVisitor.visitNilMethodInvocation(this);
    }
}
