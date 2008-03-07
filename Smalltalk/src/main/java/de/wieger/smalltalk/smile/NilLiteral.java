package de.wieger.smalltalk.smile;




public class NilLiteral extends AbstractValue implements Literal {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    public static final NilLiteral NIL = new NilLiteral();



    //--------------------------------------------------------------------------
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitNilLiteral(this);
    }



    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return "NilLiteral";
    }
}
