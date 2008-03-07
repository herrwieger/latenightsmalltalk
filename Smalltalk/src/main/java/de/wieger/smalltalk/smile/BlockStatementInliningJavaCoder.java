package de.wieger.smalltalk.smile;


public class BlockStatementInliningJavaCoder extends JavaCoder {

    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private Value   fResultTempVar;
    private boolean fNeedsFinalReturn = true;



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public BlockStatementInliningJavaCoder(DynamicMethodInvocationCodingStrategy pDynamicMethodInvocationCodingStrategy, Value pResultTempVar, boolean pUseSimpleNames) {
        super(pDynamicMethodInvocationCodingStrategy, pUseSimpleNames, false);

        fResultTempVar = pResultTempVar;
    }


    // --------------------------------------------------------------------------
    // 
    // --------------------------------------------------------------------------

    @Override
    protected String getScopeAccessPrefix(Assignment pAssignment) {
        if(!pAssignment.getScope().equals(Scope.OUTER_CLASS)) {
            return super.getScopeAccessPrefix(pAssignment);
        }
        return Scope.SELF.getAccessPrefix();
    }
    
    @Override
    protected void appendOuterClassVariable() {
        getCode().append("this");
    }

    @Override
    public void visitDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation) {
        codeDynamicMethodInvocation(pDynamicMethodInvocation);
    }
    
    
    @Override
    public void visitReturn(Return pReturn) {
        if (fNeedsFinalReturn) {
            fResultTempVar.accept(this);
            getCode().append(" = ");
            pReturn.getValue().accept(this);
            getCode().append(";");
        }
    }

    @Override
    public void visitBlockReturn(BlockReturn pBlockReturn) {
        fNeedsFinalReturn = false;
        getCode().append("return ");
        pBlockReturn.getValue().accept(this);
        getCode().append(";");
    }


    //--------------------------------------------------------------------------  
    // ValueVisitor methods
    //--------------------------------------------------------------------------

    public void visitOuterSelf(AbstractValue pAbstractValue) {
        getCode().append("this");
    }

    public void visitOuterSuper(AbstractValue pAbstractValue) {
        getCode().append("this");
    }

    
    protected String getAccessPrefixForScope(ScopedVariable pScopedVariable) {
        if (pScopedVariable.getScope().equals(Scope.OUTER_CLASS)) {
            return "this.";
        }
        return super.getAccessPrefixForScope(pScopedVariable);
    }
}
