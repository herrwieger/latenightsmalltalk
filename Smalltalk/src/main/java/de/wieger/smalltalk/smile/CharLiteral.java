package de.wieger.smalltalk.smile;



public class CharLiteral extends AbstractValue implements Literal {
    //--------------------------------------------------------------------------
    // methods
    //--------------------------------------------------------------------------

    private char fChar;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public CharLiteral(char pChar) {
        fChar = pChar;
    }



    //--------------------------------------------------------------------------  
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitCharLiteral(this);
    }

    public String getValue() {
        return "new smalltalk.Char('" + fChar + "')";
    }



    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return "StringLiteral[" + fChar + "]";
    }
}
