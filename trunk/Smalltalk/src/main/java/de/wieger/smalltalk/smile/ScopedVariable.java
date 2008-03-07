/**
 *
 */
package de.wieger.smalltalk.smile;


public class ScopedVariable implements Value {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private DeclaredVariable    fVariable;
    private Scope               fScope;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public ScopedVariable(DeclaredVariable pVariable, Scope pScope) {
        assert pVariable != null;
        assert pScope != null;

        fVariable   = pVariable;
        fScope      = pScope;
    }



    //--------------------------------------------------------------------------
    // Value methods (implementation)
    //--------------------------------------------------------------------------

    public void accept(ValueVisitor pValueVisitor) {
        pValueVisitor.visitScopedVariable(this);
    }
    

    
    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public void markReadAccess() {
        fVariable.markReadAccess();
    }

    public boolean hasReadAccess() {
        return fVariable.hasReadAccess();
    }

    public boolean isNeverRead() {
        return !hasReadAccess();
    }

    public void markMethodContext() {
        fVariable.markMethodContext();
    }

    public Scope getScope() {
        return fScope;
    }

    public DeclaredVariable getVariable() {
        return fVariable;
    }
}
