package de.wieger.smalltalk.smile;


public class Assignment implements Statement {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private DeclaredVariable fVariable;
    private Scope            fScope;
    private Value            fValue;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    Assignment(DeclaredVariable pVariable, Scope pScope, Value pValue) {
        fVariable   = pVariable;
        fScope      = pScope;
        fValue      = pValue;

        fValue.markReadAccess();
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //------------------------------------------------------------------------
    
    public Value getValue() {
        return fValue;
    }
    

    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public DeclaredVariable getVariable() {
        return fVariable;
    }
    
    public Scope getScope() {
        return fScope;
    }


    
    //--------------------------------------------------------------------------
    // Statement methods
    //--------------------------------------------------------------------------

    public void accept(StatementVisitor pVisitor) {
        pVisitor.visitAssignment(this);
    }
}
