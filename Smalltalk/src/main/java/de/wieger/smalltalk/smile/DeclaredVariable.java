package de.wieger.smalltalk.smile;


public class DeclaredVariable implements Value {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private String  fName;
    private boolean fReadAccess;
    private boolean fBelongsToMethodContext;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    DeclaredVariable(String pName) {
        fName   = pName;
    }



    //--------------------------------------------------------------------------
    // Variable methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitDeclaredVariable(this);
    }



    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public String getName() {
        return fName;
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


    public void markMethodContext() {
        fBelongsToMethodContext = true;
    }

    public boolean belongsToMethodContext() {
        return fBelongsToMethodContext;
    }



    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + "[" + fName + "]";
    }
}
