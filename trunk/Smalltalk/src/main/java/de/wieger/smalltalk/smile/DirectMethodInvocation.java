package de.wieger.smalltalk.smile;

import java.util.List;


public class DirectMethodInvocation extends AbstractMethodInvocation {
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public DirectMethodInvocation(Value pReceiver, String pSelector, List<Value> pParams, Value pTempVar) {
        super(pReceiver, pSelector, pParams, pTempVar);
    }

    public DirectMethodInvocation(Value pReceiver, String pMethodName, Value pTempVar) {
        super(pReceiver, pMethodName, pTempVar);
    }



    //--------------------------------------------------------------------------
    // Statement methods
    //--------------------------------------------------------------------------

    public void accept(StatementVisitor pVisitor) {
        pVisitor.visitDirectMethodInvocation(this);
    }
}
