package de.wieger.smalltalk.smile;

import java.util.List;




public class DynamicMethodInvocation extends AbstractMethodInvocation {
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public DynamicMethodInvocation(Value pReceiver, String pSelector, List<Value> pParams, Value pTempVar) {

        super(pReceiver, pSelector, pParams, pTempVar);
    }

    DynamicMethodInvocation(Value pReceiver, String pSelector, Value pTempVar) {
        super(pReceiver, pSelector, pTempVar);
    }



    //--------------------------------------------------------------------------
    // Statement methods
    //--------------------------------------------------------------------------

    public void accept(StatementVisitor pVisitor) {
        pVisitor.visitDynamicMethodInvocation(this);
    }
}
