package de.wieger.smalltalk.smile;




public class IntegerLiteral extends AbstractValue implements NumberLiteral {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private int fInt;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public IntegerLiteral(int pInt) {
        fInt = pInt;
    }




    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public int getIntValue() {
        return fInt;
    }



    //--------------------------------------------------------------------------  
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitIntegerLiteral(this);
    }
    
    
    public String getValue() {
        String value = "sfInteger" + fInt;
        value = value.replace("-", "Minus");
        return value;
    }
    
    
    
    //--------------------------------------------------------------------------  
    // NumberLiteral methods (implementation)
    //--------------------------------------------------------------------------

    public String getDeclarationCode() {
        return "new smalltalk.Integer("+ fInt + ")";
    }
    
    

    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + "[" + fInt + "]";
    }
}
