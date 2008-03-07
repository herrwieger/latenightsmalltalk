package de.wieger.smalltalk.smile;

import java.util.ArrayList;
import java.util.List;

import de.wieger.smalltalk.universe.JavaCodingUtil;



public abstract class AbstractMethodInvocation implements Statement {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Value       fReceiver;
    private String      fSelector;
    private List<Value> fParams;
    private Value       fResultTempVar;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    AbstractMethodInvocation(Value pReceiver, String pSelector, List<Value> pParams, Value pResultTempVar) {
        fReceiver       = pReceiver;
        fSelector       = pSelector;
        fParams         = pParams;
        fResultTempVar  = pResultTempVar;

        fReceiver.markReadAccess();
        for (Value param : pParams) {
            param.markReadAccess();
        }
    }

    AbstractMethodInvocation(Value pReceiver, String pMethodName, Value pResultTempVar) {
        this(pReceiver, pMethodName, new ArrayList<Value>(), pResultTempVar);
    }



    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    boolean isReceiverOuterSuper() {
        return fReceiver.equals(Value.OUTER_SUPER);
    }

    public Value getReceiver() {
        return fReceiver;
    }
    
    public String getSelector() {
        return fSelector;
    }
    
    public String getMethodName() {
        return JavaCodingUtil.getJavaMethodNameForSelector(fSelector);
    }

    public List<Value> getParams() {
        return fParams;
    }
    
    public int getNumParams() {
        return fParams.size();
    }
    
    public Value getResultTempVar() {
        return fResultTempVar;
    }
}
