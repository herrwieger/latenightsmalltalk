package de.wieger.smalltalk.smile;

import de.wieger.smalltalk.universe.JavaCodingUtil;


public class SuperWrapperMethod extends AbstractMethod {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    static final String SUPER_WRAPPER_PREFIX    = "super$";



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private int    fNumParameters;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public SuperWrapperMethod(String pName, int pNumArgs) {
        super(pName);
        fNumParameters    = pNumArgs;
    }


    //--------------------------------------------------------------------------
    // AbstractMethod methods (implementation)
    //--------------------------------------------------------------------------

    public String getMethodName() {
        return SUPER_WRAPPER_PREFIX + getSuperMethodName();
    }

    public int getNumParameters() {
        return fNumParameters;
    }

    private String getSuperMethodName() {
        return JavaCodingUtil.getJavaMethodNameForSelector(getName());
    }

    void appendParams(StringBuilder pCode) {
        pCode.append("(");
        for(int i=0; i<fNumParameters; i++) {
            pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            pCode.append(" pParam");
            pCode.append(i);
            pCode.append(",");
        }
        removeLastComma(pCode);
        pCode.append(")");
    }

    private void removeLastComma(StringBuilder pCode) {
        if (fNumParameters > 0) {
            pCode.setLength(pCode.length() - 1);
        }
    }

    void appendBodyContent(StringBuilder pCode, JavaCoder pCoder) {
        pCode.append("return super.");
        pCode.append(getSuperMethodName());
        pCode.append("(");
        for(int i=0; i<fNumParameters; i++) {
            pCode.append("pParam");
            pCode.append(i);
            pCode.append(",");
        }
        removeLastComma(pCode);
        pCode.append(");");
    }
}
