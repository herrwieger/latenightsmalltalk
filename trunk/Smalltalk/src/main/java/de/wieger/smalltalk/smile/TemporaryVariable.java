package de.wieger.smalltalk.smile;


public class TemporaryVariable implements Value {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static int sfId;


    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private int         fId;
    private boolean     fReadAccess;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    TemporaryVariable() {
        fId = sfId++;
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public int getId() {
        return fId;
    }
    
    public void markReadAccess() {
        fReadAccess = true;
    }

    public boolean hasReadAccess() {
        return fReadAccess;
    }

    public boolean isNeverRead() {
        return !hasReadAccess();
    }
    
    
    
    //--------------------------------------------------------------------------
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitTemporaryVariable(this);
    }
}
