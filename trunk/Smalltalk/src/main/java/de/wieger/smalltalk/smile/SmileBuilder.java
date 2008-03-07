package de.wieger.smalltalk.smile;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class SmileBuilder {

    // --------------------------------------------------------------------------
    // class variables
    // --------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private static final Logger LOG         = Logger.getLogger(SmileBuilder.class);



    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private List<Statement>     fStatements = new ArrayList<Statement>();
    private boolean             fHasReturn;



    // --------------------------------------------------------------------------
    // builder methods
    // --------------------------------------------------------------------------

    public void addReturnIfNecessary(Value pResult) {
        if (needReturn()) {
            addReturn(pResult);
        }
    }

    private boolean needReturn() {
        return !fHasReturn;
    }

    public Return addReturn(Value pTempVar) {
        fHasReturn = true;

        Return returnResult = new Return(pTempVar);
        fStatements.add(returnResult);
        return returnResult;
    }

    public BlockReturn addBlockReturn(Value pTempVar) {
        BlockReturn returnResult = new BlockReturn(pTempVar);
        fStatements.add(returnResult);
        return returnResult;
    }


    public Assignment addAssignment(DeclaredVariable pVariable, Scope pScope, Value pTempVar) {
        Assignment assignment = new Assignment(pVariable, pScope, pTempVar);
        fStatements.add(assignment);
        return assignment;
    }


    public TemporaryVariable addDynamicMethodInvocation(Value pReceiver, String pSelector, List<Value> pParams) {

        TemporaryVariable tempVar = new TemporaryVariable();
        fStatements.add(new DynamicMethodInvocation(pReceiver, pSelector, pParams, tempVar));
        return tempVar;
    }

    public Value addDynamicMethodInvocation(Value pReceiver, String pSelector) {
        TemporaryVariable tempVar = new TemporaryVariable();
        fStatements.add(new DynamicMethodInvocation(pReceiver, pSelector, tempVar));
        return tempVar;
    }


    public TemporaryVariable addDirectMethodInvocation(Value pReceiver, String pMethodName, List<Value> pParams) {
        TemporaryVariable tempVar = new TemporaryVariable();
        fStatements.add(new DirectMethodInvocation(pReceiver, pMethodName, pParams, tempVar));
        return tempVar;
    }

    public Value addDirectMethodInvocation(Value pReceiver, String pSelector) {
        TemporaryVariable tempVar = new TemporaryVariable();
        fStatements.add(new DirectMethodInvocation(pReceiver, pSelector, tempVar));
        return tempVar;
    }


    public Value addNilMethodInvocation(Value pReceiver, String pSelector) {
        TemporaryVariable tempVar = new TemporaryVariable();
        fStatements.add(new NilMethodInvocation(pReceiver, pSelector, tempVar));
        return tempVar;
    }


    public TemporaryVariable addLookup(String pIdentifier) {
        TemporaryVariable tempVar = new TemporaryVariable();
        Lookup lookup = new Lookup(tempVar, pIdentifier);
        fStatements.add(lookup);
        return tempVar;
    }


    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public List<Statement> getStatements() {
        return fStatements;
    }
}
