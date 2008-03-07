package de.wieger.smalltalk.smile;




public class SymbolLiteral extends AbstractValue implements Literal {
    //--------------------------------------------------------------------------
    // methods
    //--------------------------------------------------------------------------

    private String fSymbol;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public SymbolLiteral(String pString) {
        fSymbol = pString;
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public String getSymbol() {
        return fSymbol;
    }
    
    
    
    //--------------------------------------------------------------------------
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitSymbolLiteral(this);
    }
    


    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return "SymbolLiteral[" + fSymbol + "]";
    }
}
