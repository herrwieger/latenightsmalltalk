package de.wieger.smalltalk.smile;



public class StringLiteral extends AbstractValue implements Literal {
    //--------------------------------------------------------------------------
    // methods
    //--------------------------------------------------------------------------

    private String fString;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public StringLiteral(String pString) {
        fString = pString;
    }



    //--------------------------------------------------------------------------
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitStringLiteral(this);
    }
    


    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public String getString() {
        return fString;
    }



    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return "StringLiteral[" + fString + "]";
    }
}
