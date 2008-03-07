package de.wieger.smalltalk.smile;





public class FloatLiteral extends AbstractValue implements NumberLiteral {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private String fNumber;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public FloatLiteral(String pNumber) {
        fNumber = pNumber;
    }



    //--------------------------------------------------------------------------  
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitFloatLiteral(this);
    }
    
    public String getValue() {
        String value = "sfFloat" + fNumber;
        value = value.replace("+", "Plus");
        value = value.replace("-", "Minus");
        value = value.replace(".", "Dot");
        return value;
    }

    
    
    //--------------------------------------------------------------------------  
    // NumberLiteral methods (implementation)
    //--------------------------------------------------------------------------

    public String getDeclarationCode() {
        return "new smalltalk.Float(\""+ fNumber + "\")";
    }


    
    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + "[" + fNumber + "]";
    }
}
