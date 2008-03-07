package de.wieger.smalltalk.smile;



public interface Value {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    Value SELF = new AbstractValue() {
        public void accept(ValueVisitor pValueVisitor) {
            pValueVisitor.visitSelf(this);
        }
    };
    Value OUTER_SELF = new AbstractValue() {
        public void accept(ValueVisitor pValueVisitor) {
            pValueVisitor.visitOuterSelf(this);
        }
    };
    Value SUPER = new AbstractValue() {
        public void accept(ValueVisitor pValueVisitor) {
            pValueVisitor.visitSuper(this);
        }
    };
    Value OUTER_SUPER = new AbstractValue() {
        public void accept(ValueVisitor pValueVisitor) {
            pValueVisitor.visitOuterSuper(this);
        }
    };
    Value THIS_CONTEXT = new AbstractValue() {
        public void accept(ValueVisitor pValueVisitor) {
            pValueVisitor.visitThisContext(this);
        }
    };



    //--------------------------------------------------------------------------
    // methods
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor);

    void markReadAccess();
    boolean hasReadAccess();
    boolean isNeverRead();
}
