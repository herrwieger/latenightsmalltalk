package de.wieger.smalltalk.smile;


public class BlockReturn implements Statement {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Value               fValue;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public BlockReturn(Value pValue) {
        fValue          = pValue;

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
        pVisitor.visitBlockReturn(this);
    }
}
