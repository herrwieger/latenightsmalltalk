package de.wieger.smalltalk.smile;

import de.wieger.smalltalk.universe.JavaCodingUtil;




public class BlockDescription extends AbstractMethodDescription {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    public static final String      BLOCK_QNAME      = "smalltalk.Block";
    private static final String[]   METHOD_NAMES = {
        "value",
        "value",
        "valueValue",
        "valueValueValue",
        "valueWithArguments"
    };






    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private String              fSimpleName;
    private MethodDescription   fDeclaringMethod;
    private boolean             fIsDeclaredInBlock;
    private boolean             fUsesMethodContext;
    private boolean             fNeedsOuterClass;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public BlockDescription(String pName, String pSimpleName,
            MethodDescription pDeclaringMethod, boolean pIsDeclaredInBlock) {

        super(pName);
        fSimpleName         = pSimpleName;
        fDeclaringMethod    = pDeclaringMethod;
        fIsDeclaredInBlock  = pIsDeclaredInBlock;
    }



    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public String getSimpleName() {
        return fSimpleName;
    }
    
    public MethodDescription getDeclaringMethod() {
        return fDeclaringMethod;
    }

    @Override
    public String getMethodName() {
        return METHOD_NAMES[getNumParameters()];
    }

    public String getMethodContextName() {
        return fDeclaringMethod.getMethodContextName();
    }

    public String getMethodContextSimpleName() {
        return fDeclaringMethod.getMethodContextSimpleName();
    }

    public boolean isDeclaredInBlock() {
        return fIsDeclaredInBlock;
    }

    public boolean isDeclaredInMethod() {
        return !fIsDeclaredInBlock;
    }

    public String getHomeActivationExceptionSimpleName() {
        return fDeclaringMethod.getHomeActivationExceptionSimpleName();
    }

    public void markNeedsOuterClass() {
        fNeedsOuterClass = true;
    }

    public boolean isUsingOuterClass() {
        return fNeedsOuterClass;
    }

    public void markUsesMethodContext() {
        fUsesMethodContext = true;
    }

    public boolean isUsingMethodContext() {
        return fUsesMethodContext;
    }

    public boolean belongsToNativeMethod() {
        return fDeclaringMethod.isNative();
    }



    //--------------------------------------------------------------------------
    // coding methods
    //--------------------------------------------------------------------------


    public String getInstanceVariablesCode() {
        StringBuilder   code    = new StringBuilder();
        if (isUsingOuterClass()) {
            String simpleClassname = JavaCodingUtil.getSimpleClassname(fDeclaringMethod.getDeclaringClassname());
            code.append(simpleClassname);
            code.append(" ");
            code.append(Scope.OUTER_CLASS.getName());
            code.append(";\n");
        }
        if (isUsingMethodContext()) {
            code.append(getMethodContextSimpleName());
            code.append(" ");
            code.append(Scope.METHOD_CONTEXT.getName());
            code.append(";\n");
        }
        return code.toString();
    }

    public String getConstructorCode() {
        StringBuilder constructor = new StringBuilder();
        constructor.append(getSimpleName());
        constructor.append("(");
        int argI = 1;
        if (isUsingOuterClass()) {
            String simpleClassname = JavaCodingUtil.getSimpleClassname(fDeclaringMethod.getDeclaringClassname());
            constructor.append(simpleClassname);
            constructor.append(" $");
            constructor.append(argI++);
        }
        if (isUsingMethodContext()) {
            if (argI>1) {
                constructor.append(", ");
            }
            constructor.append(getMethodContextSimpleName());
            constructor.append(" $");
            constructor.append(argI);
        }
        constructor.append(")");
        appendConstructorBody(constructor);
        return constructor.toString();
    }

    public String getConstructorBody() {
        StringBuilder code = new StringBuilder();
        appendConstructorBody(code);
        return code.toString();
    }



    private void appendConstructorBody(StringBuilder pCode) {
        pCode.append("{");
        pCode.append("super(smalltalk.Integer.valueOf(");
        pCode.append(getNumParameters());
        pCode.append("));");

        int paramI = 1;
        if (isUsingOuterClass()) {
            addAssignParam(Scope.OUTER_CLASS.getName(), paramI, pCode);
            paramI++;
        }
        if (isUsingMethodContext()) {
            addAssignParam(Scope.METHOD_CONTEXT.getName(), paramI, pCode);
            paramI++;
        }
        pCode.append("}");
    }



    private void addAssignParam(String pVarName, int pParamI, StringBuilder pCode) {
        pCode.append(pVarName);
        pCode.append("= $");
        pCode.append(pParamI);
        pCode.append(";");
    }

    public String getReturnMethodCode() {
        return getReturnMethodCode(fDeclaringMethod.getHomeActivationExceptionName());
    }

    public String getReturnMethodCodeWithSimpleNames() {
        return getReturnMethodCode(fDeclaringMethod.getHomeActivationExceptionSimpleName());
    }

    private String getReturnMethodCode(String pExceptionClassName) {
        return "public " + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " $return(" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " pValue)"
                    + "{throw new " + pExceptionClassName + "(pValue);}";
    }


    public String getReturnFromExceptionMethodCode() {
        return getReturnFromExceptionMethodCode(fDeclaringMethod.getHomeActivationExceptionName());
    }

    public String getReturnFromExceptionMethodCodeWithSimpleNames() {
        return getReturnFromExceptionMethodCode(fDeclaringMethod.getHomeActivationExceptionSimpleName());
    }

    private String getReturnFromExceptionMethodCode(String pExceptionClassName) {
        return "public " + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " $returnFromException(" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + " pValue){throw new " + pExceptionClassName + "(pValue, true);}";
    }
}
