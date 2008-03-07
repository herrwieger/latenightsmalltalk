package de.wieger.smalltalk.smile;




public class BooleanLiteral extends AbstractValue implements Literal {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    public static final BooleanLiteral TRUE    = new BooleanLiteral(true);
    public static final BooleanLiteral FALSE   = new BooleanLiteral(false);



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private boolean fValue;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    BooleanLiteral(boolean pValue) {
        fValue  = pValue;
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public boolean getValue() {
        return fValue;
    }

    

    //--------------------------------------------------------------------------  
    // Value methods
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitBooleanLiteral(this);
    }



    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return "BooleanLiteral[" + fValue + "]";
    }
}
