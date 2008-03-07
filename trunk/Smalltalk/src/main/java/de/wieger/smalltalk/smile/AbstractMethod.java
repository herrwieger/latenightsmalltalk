package de.wieger.smalltalk.smile;

import de.wieger.smalltalk.universe.JavaCodingUtil;



public abstract class AbstractMethod {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final BaseclassDynamicMethodInvocationCodingStrategy BASECLASS_DYNAMIC_METHOD_INVOCATION_CODING_STRATEGY = new BaseclassDynamicMethodInvocationCodingStrategy();
    
    
    
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    protected String fName;


    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public AbstractMethod(String pName) {
        fName   = pName;
    }


    //--------------------------------------------------------------------------
    // AbstractMethod methods
    //--------------------------------------------------------------------------

    public String getName() {
        return fName;
    }

    abstract String getMethodName();
    abstract int getNumParameters();
    abstract void appendParams(StringBuilder pCode);
    abstract void appendBodyContent(StringBuilder pCode, JavaCoder pCoder);



    //--------------------------------------------------------------------------
    // JavaCoding methods
    //--------------------------------------------------------------------------

    public String getCodeWithSimpleNames() {
        return getCode(new JavaCoder(BASECLASS_DYNAMIC_METHOD_INVOCATION_CODING_STRATEGY, true, true));
    }

    public String getCode(JavaCoder pCoder) {
        StringBuilder code = new StringBuilder();

        appendMethodDeclaration(code);
        appendBody(code, pCoder);

        return code.toString();
    }

    public String getMethodDeclaration() {
        StringBuilder code = new StringBuilder();
        appendMethodDeclaration(code);
        return code.toString();
    }
    
    private void appendMethodDeclaration(StringBuilder pCode) {
        pCode.append("public  ");
        pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
        pCode.append(" ");
        pCode.append(getMethodName());
        appendParams(pCode);
    }

    private void appendBody(StringBuilder pCode, JavaCoder pCoder) {
        pCode.append("{");
        appendBodyContent(pCode, pCoder);
        pCode.append("}");
    }
}
