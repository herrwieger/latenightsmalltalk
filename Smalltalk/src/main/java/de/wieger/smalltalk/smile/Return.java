package de.wieger.smalltalk.smile;


public class Return implements Statement {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Value    fValue;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public Return(Value pValue) {
        assert pValue != null;

        fValue    = pValue;
        fValue.markReadAccess();
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public Value getValue() {
        return fValue;
    }

    
    
    //--------------------------------------------------------------------------
    // Statement methods
    //--------------------------------------------------------------------------

    public void accept(StatementVisitor pVisitor) {
        pVisitor.visitReturn(this);
    }
}
