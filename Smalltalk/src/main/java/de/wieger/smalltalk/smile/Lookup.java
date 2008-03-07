package de.wieger.smalltalk.smile;



public class Lookup implements Statement {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private Value    fValue;
    protected String fIdentifier;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    Lookup(Value pValue, String pIdentifier) {
        fValue      = pValue;
        fIdentifier = pIdentifier;
    }



    //--------------------------------------------------------------------------
    // Statement methods
    //--------------------------------------------------------------------------

    public void accept(StatementVisitor pVisitor) {
        pVisitor.visitLookup(this);
    }
    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public Value getValue() {
        return fValue;
    }

    public String getIdentifier() {
        return fIdentifier;
    }
}
