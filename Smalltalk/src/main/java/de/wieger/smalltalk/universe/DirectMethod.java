package de.wieger.smalltalk.universe;




public class DirectMethod {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private String fSelector;
    private int    fParamsLength;


    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    DirectMethod(String pSelector, int pParamsLength) {
        fSelector = pSelector;
        fParamsLength = pParamsLength;
    }

    
    
    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    public String getSelector() {
        return fSelector;
    }
    
    
    
    //--------------------------------------------------------------------------  
    // Coding methods
    //--------------------------------------------------------------------------

    public String getMethodDeclaration() {
        StringBuilder pCode = new StringBuilder();
        pCode.append("public  ");
        pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
        pCode.append(" ");
        pCode.append(JavaCodingUtil.getJavaMethodNameForSelector(fSelector));
        appendParams(pCode);
        return pCode.toString();
    }
    
    void appendParams(StringBuilder pCode) {
        pCode.append("(");
        for (int i=0; i<fParamsLength; i++) {
            pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            pCode.append(" ");
            pCode.append("p");
            pCode.append(i);
            pCode.append(",");
        }
        removeLastComma(pCode);
        pCode.append(")");
    }
    
    private void removeLastComma(StringBuilder pCode) {
        if (fParamsLength > 0) {
            pCode.setLength(pCode.length() - 1);
        }
    }

    
    
    //--------------------------------------------------------------------------  
    // object methods overridden
    //--------------------------------------------------------------------------

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fParamsLength;
        result = prime * result + ((fSelector == null) ? 0 : fSelector.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DirectMethod other = (DirectMethod) obj;
        if (fParamsLength != other.fParamsLength)
            return false;
        if (fSelector == null) {
            if (other.fSelector != null)
                return false;
        } else if (!fSelector.equals(other.fSelector))
            return false;
        return true;
    }

    
}
