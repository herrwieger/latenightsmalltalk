package de.wieger.smalltalk.smile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wieger.smalltalk.universe.JavaCodingUtil;




public abstract class AbstractMethodDescription extends AbstractMethod {
    // --------------------------------------------------------------------------
    // instance variables
    // --------------------------------------------------------------------------

    private List<DeclaredVariable>          fParameters           = new ArrayList<DeclaredVariable>();
    private Map<String, DeclaredVariable>   fParametersByName     = new HashMap<String, DeclaredVariable>();
    private Map<String, DeclaredVariable>   fLocalVariablesByName = new HashMap<String, DeclaredVariable>();
    private SmileBuilder                    fSmileBuilder         = new SmileBuilder();



    // --------------------------------------------------------------------------
    // constructors
    // --------------------------------------------------------------------------

    public AbstractMethodDescription(String pName) {
        super(pName);
    }



    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    public void addReturn(Value pTempVar) {
        fSmileBuilder.addReturn(pTempVar);
    }

    public void addReturnIfNecessary(Value pResult) {
        fSmileBuilder.addReturnIfNecessary(pResult);
    }



    // --------------------------------------------------------------------------
    // accessor methods
    // --------------------------------------------------------------------------

    public SmileBuilder getSmileBuilder() {
        return fSmileBuilder;
    }

    public void addParameters(List<String> pParameters) {
        for (String paramName : pParameters) {
            addParameter(paramName);
        }
    }

    public void addParameter(String pParamName) {
        DeclaredVariable variable = new DeclaredVariable(pParamName);
        fParameters.add(variable);
        fParametersByName.put(pParamName, variable);
    }

    public List<DeclaredVariable> getParameters() {
        return fParameters;
    }

    @Override
    public int getNumParameters() {
        return fParameters.size();
    }

    public void addLocalVariable(String pVariableName) {
        fLocalVariablesByName.put(pVariableName, new DeclaredVariable(pVariableName));
    }

    public DeclaredVariable getLocalOrParameterVariable(String pVarName) {
        DeclaredVariable variable = fLocalVariablesByName.get(pVarName);
        if (variable != null) {
            return variable;
        }
        return fParametersByName.get(pVarName);
    }

    Collection<DeclaredVariable> getLocalVariables() {
        return fLocalVariablesByName.values();
    }



    // --------------------------------------------------------------------------
    // JavaCoding methods
    // --------------------------------------------------------------------------

    public String getAbstractMethodCode() {
        StringBuilder code = new StringBuilder();

        code.append("public abstract ");
        code.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
        code.append(" ");
        code.append(getMethodName());
        appendParams(code);
        code.append(";");

        return code.toString();
    }

    @Override
    void appendParams(StringBuilder pCode) {
        pCode.append("(");
        for (DeclaredVariable parameter : fParameters) {
            pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            pCode.append(" ");
            pCode.append(JavaCoder.codeValue(parameter));
            pCode.append(",");
        }
        removeLastComma(pCode);
        pCode.append(")");
    }

    private void removeLastComma(StringBuilder pCode) {
        if (fParameters.size() > 0) {
            pCode.setLength(pCode.length() - 1);
        }
    }

    @Override
    protected void appendBodyContent(StringBuilder pCode, JavaCoder pCoder) {
        copySharedParametersToMethodContext(pCode);
        appendVariableDeclarations(pCode);
        pCode.append(pCoder.getCodeForStatements(this));
    }

    private void appendVariableDeclarations(StringBuilder pCode) {
        for (DeclaredVariable var : fLocalVariablesByName.values()) {
            if (var.belongsToMethodContext()) {
                continue;
            }
            pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            pCode.append(" ");
            pCode.append(JavaCoder.codeValue(var));
            pCode.append(";");
        }
    }

    protected void copySharedParametersToMethodContext(StringBuilder pCode) {
        for (DeclaredVariable param : getParameters()) {
            if (!param.belongsToMethodContext()) {
                continue;
            }
            pCode.append(Scope.METHOD_CONTEXT.getName());
            pCode.append(".");
            pCode.append(JavaCoder.codeValue(param));
            pCode.append("=");
            pCode.append(JavaCoder.codeValue(param));
            pCode.append(";");
        }
    }
    
    
    
    //--------------------------------------------------------------------------  
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + "[" + fName + "]";
    }


    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + fName.hashCode();
        result = PRIME * result + fParameters.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractMethodDescription other = (AbstractMethodDescription) obj;
        if (!fName.equals(other.fName)) {
            return false;
        }
        if (!fParameters.equals(other.fParameters)) {
            return false;
        }
        return true;
    }
}
