package de.wieger.smalltalk.smile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import antlr.Token;
import de.wieger.commons.lang.StringUtil;
import de.wieger.smalltalk.universe.JavaCodingUtil;



public class MethodDescription extends AbstractMethodDescription {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    private static final String METHOD_CONTEXT_CLASSNAME      = "Context";
    private static final String HOME_ACTIVATION_EXCEPTIONNAME = "Exception";
    private static final String BLOCK_NAME                    = "Block";



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private String                 fSelector;
    private ClassDescription       fDeclaringClass;
    private String                 fCategory;
    private Set<String>            fMethodContextVariableNames   = new HashSet<String>();
    private List<BlockDescription> fBlockDescriptions            = new ArrayList<BlockDescription>();
    private boolean                fIsNative;
    private int                    fBlockId;
    private String                 fJavaCode;

    private int                    fStart;
    private int                    fEnd;
    private List<Token>            fVariableNameTokens = new ArrayList<Token>();


    
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------


    public MethodDescription(String pName, String pSelector, ClassDescription pDeclaringClass) {
        super(pName);
        fSelector       = pSelector;
        fDeclaringClass = pDeclaringClass;
    }



    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public String getSelector() {
        return fSelector;
    }
        
    public String getDeclaringClassname() {
        return fDeclaringClass.getName();
    }

    @Override
    public String getMethodName() {
        return JavaCodingUtil.getJavaMethodNameForSelector(getName());
    }

    public String getMethodContextName() {
        return getDeclaringClassPrefix() + getMethodContextSimpleName();
    }

    public String getMethodContextSimpleName() {
        return getMethodSimplePrefix() + METHOD_CONTEXT_CLASSNAME;
    }

    public String getHomeActivationExceptionName() {
        return getDeclaringClassPrefix() + getHomeActivationExceptionSimpleName();
    }

    public String getHomeActivationExceptionSimpleName() {
        return getMethodSimplePrefix() + HOME_ACTIVATION_EXCEPTIONNAME;
    }

    public String getBlockName() {
        return getDeclaringClassPrefix() + getBlockSimpleName();
    }

    public String getBlockSimpleName() {
        return getMethodSimplePrefix() + BLOCK_NAME + fBlockId;
    }

    private String getDeclaringClassPrefix() {
        return JavaCodingUtil.getQualifiedClassname(fDeclaringClass.getName()) + "$";
    }

    public String getMethodSimplePrefix() {
        return StringUtil.firstCharToUpper(getMethodName()) + "$" + getParameters().size() + "$";
    }


    public void nextBlockId() {
        fBlockId++;
    }

    public void addToMethodContext(String pVariableName) {
        fMethodContextVariableNames.add(pVariableName);
    }

    public Set<String> getMethodContextVariableNames() {
        return fMethodContextVariableNames;
    }

    public boolean isWithContext() {
        return fMethodContextVariableNames.size() > 0;
    }

    public void addBlockDescription(BlockDescription pBlockDescription) {
        fBlockDescriptions.add(pBlockDescription);
    }

    public List<BlockDescription> getBlockDescriptions() {
        return fBlockDescriptions;
    }

    public boolean isWithBlocks() {
        return fBlockDescriptions.size() > 0;
    }

    public boolean hasNoBlockDescriptions() {
        return !isWithBlocks();
    }


    public void setCategory(String pCategory) {
        fCategory   = pCategory;
    }

    public String getCategory() {
        return fCategory;
    }

    public void markAsNative() {
        fIsNative = true;
    }

    public boolean isNative() {
        return fIsNative;
    }

    public boolean isNotNative() {
        return !fIsNative;
    }

    public void setJavaCode(String pJavaCode) {
        fJavaCode = pJavaCode;
    }

    public boolean isNativeToSkip() {
        return fIsNative && fJavaCode == null;
    }

    public boolean isNotNativeToSkip() {
        return !isNativeToSkip();
    }

    
    
    //--------------------------------------------------------------------------  
    // parser info accessor methods
    //--------------------------------------------------------------------------

    public void setSourceRange(int pStart, int pEnd) {
        fStart  = pStart;
        fEnd    = pEnd;
    }
        
    public int getStart() {
        return fStart;
    }

    public int getEnd() {
        return fEnd;
    }


    public int getNameStart() {
        return getStart();
    }

    public int getNameEnd() {
        return getStart() + fSelector.length();
    }
    

    
    public void addVariableNameToken(Token pVarNameToken) {
        fVariableNameTokens.add(pVarNameToken);
    }

    public void addVariableNameTokens(List<Token> pVarNameTokens) {
        fVariableNameTokens.addAll(pVarNameTokens);
    }
    
    public List<Token> getVariableNameTokens() {
        return fVariableNameTokens;
    }

    public int getStart(Token pToken) {
        return getStart() + pToken.getColumn() - 1;
    }
    

    
    //--------------------------------------------------------------------------
    // JavaCoding methods
    //--------------------------------------------------------------------------


    public String getCode(JavaCoder pCoder) {
        if(isNotNative()) {
            return super.getCode(pCoder);
        }
        return fJavaCode;
    }


    @Override
    protected void appendBodyContent(StringBuilder pCode, JavaCoder pCoder) {
        if (isWithContext()) {
            appendMethodContextDefinition(pCode, pCoder.getUseSimpleNames());
        }
        if (isWithBlocks()) {
            pCode.append("try");
        }
        pCode.append("{");
        super.appendBodyContent(pCode, pCoder);
        pCode.append("}");
        if (isWithBlocks()) {
            pCode.append(" catch (");
            pCode.append(pCoder.getUseSimpleNames() ? getHomeActivationExceptionSimpleName() : getHomeActivationExceptionName());
            pCode.append(" ex){return (" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ") ex.getResult();}");
        }
    }

    private void appendMethodContextDefinition(StringBuilder pCode, boolean pUseSimpleNames) {
        pCode.append(pUseSimpleNames ? getMethodContextSimpleName() : getMethodContextName());
        pCode.append(" ");
        pCode.append(Scope.METHOD_CONTEXT.getName());
        pCode.append(" = new ");
        pCode.append(pUseSimpleNames ? getMethodContextSimpleName() : getMethodContextName());
        pCode.append("();");
    }



    //--------------------------------------------------------------------------
    // object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getName() + "[" + fDeclaringClass + "#" + getName() + "]";
    }


    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + fDeclaringClass.hashCode();
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MethodDescription other = (MethodDescription) obj;
        if (!fDeclaringClass.equals(other.fDeclaringClass)) {
            return false;
        }
        return true;
    }
}
