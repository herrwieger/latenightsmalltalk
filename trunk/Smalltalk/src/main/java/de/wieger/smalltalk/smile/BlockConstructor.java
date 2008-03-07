package de.wieger.smalltalk.smile;




public class BlockConstructor extends AbstractValue {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private BlockDescription                fBlockDescription;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public BlockConstructor(BlockDescription pBlockDescription) {
        fBlockDescription   = pBlockDescription;
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public BlockDescription getBlockDescription() {
        return fBlockDescription;
    }
    
    

    //--------------------------------------------------------------------------  
    // Value methods
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitBlockConstructor(this);
    }
}
