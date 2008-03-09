package de.wieger.smalltalk.smile;

import de.wieger.smalltalk.universe.AbstractUniverse;
import de.wieger.smalltalk.universe.JavaCodingUtil;


public class JavaCoder implements StatementVisitor, ValueVisitor {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final BaseclassDynamicMethodInvocationCodingStrategy BASECLASS_DYNAMIC_METHOD_INVOCATION_CODING_STRATEGY = new BaseclassDynamicMethodInvocationCodingStrategy();

    
    
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private DynamicMethodInvocationCodingStrategy fDynamicMethodInvocationCodingStrategy;
    private boolean                               fUseSimpleNames;
    private boolean                               fOptimizeByInlining;
    
    private StringBuilder                         fCode = new StringBuilder();
    private boolean                               fIsOptimizingByInlining;
    private String                                fOuterClassPrefix;
    


    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public JavaCoder(DynamicMethodInvocationCodingStrategy pDynamicMethodInvocationCodingStrategy,
            boolean pUseSimpleNames, boolean pOptimizeByInlining) {
        fDynamicMethodInvocationCodingStrategy  = pDynamicMethodInvocationCodingStrategy;
        fUseSimpleNames                         = pUseSimpleNames;
        fOptimizeByInlining                     = pOptimizeByInlining;
    }



    // --------------------------------------------------------------------------
    // class methods
    // --------------------------------------------------------------------------

    public static StringBuilder codeValue(Value pValue) {
        JavaCoder javaCoder = new JavaCoder(BASECLASS_DYNAMIC_METHOD_INVOCATION_CODING_STRATEGY, false, false);
        pValue.accept(javaCoder);
        return javaCoder.getCode();
    }



    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    public String getCodeForStatements(AbstractMethodDescription pAbstractMethodDescription) {
        fIsOptimizingByInlining     = fOptimizeByInlining && (pAbstractMethodDescription instanceof MethodDescription);
        if (pAbstractMethodDescription instanceof BlockDescription) {
            String declaringClassname = ((BlockDescription)pAbstractMethodDescription).getDeclaringMethod().getDeclaringClassname();
            fOuterClassPrefix = JavaCodingUtil.getQualifiedClassname(declaringClassname) + ".";
        } else {
            fOuterClassPrefix = "";
        }
        for (Statement statement : pAbstractMethodDescription.getStatements()) {
            statement.accept(this);
        }
        return fCode.toString();
    }

    protected StringBuilder getCode() {
        return fCode;
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public boolean getUseSimpleNames() {
        return fUseSimpleNames;
    }

    
    
    // --------------------------------------------------------------------------
    // StatementVisitor methods (implementation)
    // --------------------------------------------------------------------------

    public void visitAssignment(Assignment pAssignment) {
        fCode.append(getAccessPrefix(pAssignment));
        pAssignment.getVariable().accept(this);
        fCode.append(" = ");
        pAssignment.getValue().accept(this);
        fCode.append(";");
    }

    private String getAccessPrefix(Assignment pAssignment) {
        if (pAssignment.getVariable().belongsToMethodContext()) {
            return Scope.METHOD_CONTEXT.getAccessPrefix();
        }
        return getScopeAccessPrefix(pAssignment);
    }

    protected String getScopeAccessPrefix(Assignment pAssignment) {
        return pAssignment.getScope().getAccessPrefix();
    }


    public void visitLookup(Lookup pLookup) {
        fCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " ");
        pLookup.getValue().accept(this);
        fCode.append(" = " + "(" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ")" + AbstractUniverse.class.getName()
                + ".getCurrentUniverse().lookup(" + "new smalltalk.String(\"" + pLookup.getIdentifier() + "\"));");
    }



    public void visitNilMethodInvocation(NilMethodInvocation pNilMethodInvocation) {
        appendResultTempVarAssignment(pNilMethodInvocation);
        fCode.append("smalltalk.internal.NilUtil." + pNilMethodInvocation.getMethodName() + "(");
        pNilMethodInvocation.getReceiver().accept(this);
        fCode.append(");");
    }

    private void appendResultTempVarAssignment(NilMethodInvocation pNilMethodInvocation) {
        if (pNilMethodInvocation.getResultTempVar().isNeverRead()) {
            return;
        }
        fCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " ");
        pNilMethodInvocation.getResultTempVar().accept(this);
        fCode.append(" = (" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ")");
    }


    public void visitDirectMethodInvocation(DirectMethodInvocation pDirectMethodInvocation) {
        appendResultTempVarAssignment(pDirectMethodInvocation);
        pDirectMethodInvocation.getReceiver().accept(this);
        fCode.append(".");
        if (pDirectMethodInvocation.isReceiverOuterSuper()) {
            fCode.append(SuperWrapperMethod.SUPER_WRAPPER_PREFIX);
        }
        fCode.append(pDirectMethodInvocation.getMethodName());
        fCode.append("(");
        for (Value param : pDirectMethodInvocation.getParams()) {
            param.accept(this);
            fCode.append(",");
        }
        if (pDirectMethodInvocation.getNumParams() > 0) {
            fCode.setLength(fCode.length() - 1);
        }
        fCode.append(");");
    }

    public void visitDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation) {
        if (!fIsOptimizingByInlining) {
            codeDynamicMethodInvocation(pDynamicMethodInvocation);
        } else {
            if (isIfTrueInvocation(pDynamicMethodInvocation)) {
                inlineIfTrueInvocation(pDynamicMethodInvocation);
            } else {
                codeDynamicMethodInvocation(pDynamicMethodInvocation);
            }
        }
    }



    protected void codeDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation) {
        fDynamicMethodInvocationCodingStrategy.codeDynamicMethodInvocation(pDynamicMethodInvocation, this, fCode);
    }

    private boolean isIfTrueInvocation(DynamicMethodInvocation pDynamicMethodInvocation) {
        return pDynamicMethodInvocation.getMethodName().equals("ifTrue")
                && pDynamicMethodInvocation.getNumParams() == 1
                && pDynamicMethodInvocation.getParams().get(0) instanceof BlockConstructor;
    }

    private void inlineIfTrueInvocation(DynamicMethodInvocation pDynamicMethodInvocation) {
        Value firstValue = pDynamicMethodInvocation.getParams().get(0);
        BlockDescription block = ((BlockConstructor) firstValue).getBlockDescription();
        String codeForBlockStatements = 
            new BlockStatementInliningJavaCoder(fDynamicMethodInvocationCodingStrategy, pDynamicMethodInvocation.getResultTempVar(), fUseSimpleNames).getCodeForStatements(block);
        fCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " ");
        pDynamicMethodInvocation.getResultTempVar().accept(this);
        fCode.append(";");
        fCode.append("if (");
        pDynamicMethodInvocation.getReceiver().accept(this);
        fCode.append(".getClass()==smalltalk.True.class) {" + codeForBlockStatements + "} else {");
        pDynamicMethodInvocation.getResultTempVar().accept(this);
        fCode.append("= null; }");
    }



    void appendResultTempVarAssignment(AbstractMethodInvocation pMethodInvocation) {
        if (pMethodInvocation.getResultTempVar().isNeverRead()) {
            return;
        }
        fCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
        fCode.append("  ");
        pMethodInvocation.getResultTempVar().accept(this);
        fCode.append(" = ");
    }



    public void visitReturn(Return pReturn) {
        fCode.append("return ");
        pReturn.getValue().accept(this);
        fCode.append(";");
    }

    public void visitBlockReturn(BlockReturn pBlockReturn) {
        fCode.append("$return(");
        pBlockReturn.getValue().accept(this);
        fCode.append(");");
    }


    // --------------------------------------------------------------------------
    // ValueVisitor methods (implementation)
    // --------------------------------------------------------------------------

    public void visitSelf(AbstractValue pAbstractValue) {
        fCode.append("this");
    }

    public void visitSuper(AbstractValue pAbstractValue) {
        fCode.append("super");
    }

    public void visitThisContext(AbstractValue pAbstractValue) {
        fCode.append("");
    }

    public void visitOuterSelf(AbstractValue pAbstractValue) {
        fCode.append(Scope.OUTER_CLASS.getName());
    }

    public void visitOuterSuper(AbstractValue pAbstractValue) {
        fCode.append(Scope.OUTER_CLASS.getName());
    }


    public void visitBlockConstructor(BlockConstructor pBlockConstructor) {
        BlockDescription blockDescription = pBlockConstructor.getBlockDescription();

        fCode.append("new ");
        fCode.append(fUseSimpleNames ? blockDescription.getSimpleName() : blockDescription.getName());
        fCode.append("(");
        if (blockDescription.isUsingOuterClass()) {
            if (blockDescription.isDeclaredInMethod()) {
                fCode.append("this");
            } else {
                appendOuterClassVariable();
            }
        }
        if (blockDescription.isUsingMethodContext()) {
            if (blockDescription.isUsingOuterClass()) {
                fCode.append(",");
            }
            fCode.append(Scope.METHOD_CONTEXT.getName());
        }
        fCode.append(")");
    }
    
    protected void appendOuterClassVariable() {
        fCode.append(Scope.OUTER_CLASS.getName());
    }
    
    
    public void visitBooleanLiteral(BooleanLiteral pBooleanLiteral) {
        if (pBooleanLiteral.getValue()) {
            fCode.append("(" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ")" + AbstractUniverse.class.getName()
                    + ".getTrue()");
        } else {
            fCode.append("(" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ")" + AbstractUniverse.class.getName()
                    + ".getFalse()");
        }
    }

    public void visitCharLiteral(CharLiteral pCharLiteral) {
        fCode.append(pCharLiteral.getValue());
    }

    public void visitFloatLiteral(FloatLiteral pFloatLiteral) {
        fCode.append(fOuterClassPrefix);
        fCode.append(pFloatLiteral.getValue());
    }

    public void visitIntegerLiteral(IntegerLiteral pIntegerLiteral) {
        fCode.append(fOuterClassPrefix);
        fCode.append(pIntegerLiteral.getValue());
    }


    public void visitNilLiteral(NilLiteral pNilLiteral) {
        fCode.append("null");
    }

    public void visitStringLiteral(StringLiteral pStringLiteral) {
        fCode.append("new smalltalk.String(\"" + pStringLiteral.getString() + "\")");
    }

    public void visitSymbolLiteral(SymbolLiteral pSymbolLiteral) {
        fCode.append("new smalltalk.Symbol(\"" + pSymbolLiteral.getSymbol() + "\")");
    }


    public void visitDeclaredVariable(DeclaredVariable pDeclaredVariable) {
        fCode.append(pDeclaredVariable.getName());
    }

    public void visitScopedVariable(ScopedVariable pScopedVariable) {
        fCode.append(getAccessPrefix(pScopedVariable));
        pScopedVariable.getVariable().accept(this);
    }

    private String getAccessPrefix(ScopedVariable pScopedVariable) {
        if (pScopedVariable.getVariable().belongsToMethodContext()) {
            return Scope.METHOD_CONTEXT.getAccessPrefix();
        }
        return getAccessPrefixForScope(pScopedVariable);
    }

    protected String getAccessPrefixForScope(ScopedVariable pScopedVariable) {
        return pScopedVariable.getScope().getAccessPrefix();
    }


    public void visitTemporaryVariable(TemporaryVariable pTemporaryVariable) {
        fCode.append("$v" + pTemporaryVariable.getId());
    }
}
