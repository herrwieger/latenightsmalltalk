package de.wieger.smalltalk.parser;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import de.wieger.commons.lang.StringUtil;
import de.wieger.smalltalk.smile.AbstractMethodDescription;
import de.wieger.smalltalk.smile.BlockConstructor;
import de.wieger.smalltalk.smile.BlockDescription;
import de.wieger.smalltalk.smile.CharLiteral;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.DeclaredVariable;
import de.wieger.smalltalk.smile.FloatLiteral;
import de.wieger.smalltalk.smile.IntegerLiteral;
import de.wieger.smalltalk.smile.Literal;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.smile.NumberLiteral;
import de.wieger.smalltalk.smile.Scope;
import de.wieger.smalltalk.smile.ScopedVariable;
import de.wieger.smalltalk.smile.SmileBuilder;
import de.wieger.smalltalk.smile.Statement;
import de.wieger.smalltalk.smile.StringLiteral;
import de.wieger.smalltalk.smile.SymbolLiteral;
import de.wieger.smalltalk.smile.Value;
import de.wieger.smalltalk.smile.ClassDescription.VariabilityType;


abstract class AbstractSmalltalkParser extends antlr.LLkParser {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(AbstractSmalltalkParser.class);



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private ClassDescription                 fCurrentClass;
    private Stack<AbstractMethodDescription> fBlockScopeStack = new Stack<AbstractMethodDescription>();

    private ClassDescription                 fDummyClass      = new ClassDescription(null, "Dummy", null, null,
                                                                        VariabilityType.NONE);
    private MethodDescription                fDummyMethod     = new MethodDescription("dummy", "dummy", fDummyClass);
    


    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    AbstractSmalltalkParser(int pK) {
        super(pK);
    }

    AbstractSmalltalkParser(ParserSharedInputState pInputState, int pK) {
        super(pInputState, pK);
    }

    AbstractSmalltalkParser(TokenBuffer pTokenBuffer, int pK) {
        super(pTokenBuffer, pK);
    }

    AbstractSmalltalkParser(TokenStream pTokenStream, int pK) {
        super(pTokenStream, pK);
    }



    //--------------------------------------------------------------------------  
    // parsing context control methods
    //--------------------------------------------------------------------------

    public void setCurrentClass(ClassDescription pClass) {
        fCurrentClass   = pClass;
    }

    ClassDescription getCurrentClass() {
        return fCurrentClass;
    }


    public void pushBlockScope(AbstractMethodDescription pBlockDescription) {
        fBlockScopeStack.push(pBlockDescription);
    }

    public void popBlockScope() {
        fBlockScopeStack.pop();
    }
    
    AbstractMethodDescription getBlockScope(int pIndex) {
        return fBlockScopeStack.get(pIndex);
    }

    AbstractMethodDescription getCurrentBlockScope() {
        return fBlockScopeStack.peek();
    }

    MethodDescription getMethodScope() {
        return (MethodDescription)fBlockScopeStack.get(0);
    }

    boolean currentScopeIsMethodScope() {
        return fBlockScopeStack.size() == 1;
    }

    boolean currentScopeIsBlockScope() {
        return fBlockScopeStack.size() > 1;
    }

    
    
    //--------------------------------------------------------------------------  
    // parsing methods
    //--------------------------------------------------------------------------

    public List<Statement> parseExpression() throws RecognitionException, TokenStreamException {
        SmileBuilder    builder     = new SmileBuilder();
        setCurrentClass(fDummyClass);
        pushBlockScope(fDummyMethod);
        expression(builder);
        return builder.getStatements();
    }

    
    
    public abstract Value expression(SmileBuilder pBuilder) throws RecognitionException, TokenStreamException;

    

    //--------------------------------------------------------------------------
    // builder methods
    //--------------------------------------------------------------------------


    Literal buildNumberLiteral(String pValue) {
        NumberLiteral literal = fCurrentClass.getNumberLiteral(pValue);
        if (literal!=null) {
            return literal;
        }
        
        if (pValue.matches("[^eds.]*(\\.|e|d|s)[^eds.]*")) {
            literal = new FloatLiteral(pValue);
        } else {
            literal = new IntegerLiteral(Integer.valueOf(pValue));
        }
        fCurrentClass.addNumberLiteral(pValue, literal);
        return literal;
    }

    CharLiteral buildCharLiteral(String pValue) {
        LOG.debug("Character Literal=" + pValue);
        return new CharLiteral(pValue.charAt(1));
    }

    StringLiteral buildStringLiteral(String pValue) {
        LOG.debug("String Literal=" + pValue);
        return new StringLiteral(getContentFromStringLiteral(pValue));
    }

    SymbolLiteral buildSymbolLiteral(String pValue) {
        LOG.debug("Symbol Literal=" + pValue);
        return new SymbolLiteral(pValue);
    }


    BlockConstructor buildBlockConstructor(BlockDescription pBlockDescription) {
        return new BlockConstructor(pBlockDescription);
    }

    public Value buildMethodInvocation(Value pReceiver, String pSelector, SmileBuilder pStatements) {
        assert pReceiver    != null;
        assert pSelector    != null;

        if (isDirectMethodInvocation(pReceiver) ) {
            if (needsSuperWrapper(pReceiver)) {
                fCurrentClass.addSuperWrapperMethod(pSelector, 0);
            }
            return pStatements.addDirectMethodInvocation(pReceiver, pSelector);
        }
        if (pSelector.equals("isNil")) {
            return pStatements.addNilMethodInvocation(pReceiver, "isNil");
        }
        if (pSelector.equals("notNil")) {
            return pStatements.addNilMethodInvocation(pReceiver, "notNil");
        }
        return pStatements.addDynamicMethodInvocation(pReceiver, pSelector);
    }

    public Value buildMethodInvocation(Value pReceiver, String pSelector, List<Value> pParams, SmileBuilder pStatements) {
        assert pReceiver    != null;
        assert pSelector    != null;
        assert pParams      != null;

        if (isDirectMethodInvocation(pReceiver) ) {
            if (needsSuperWrapper(pReceiver)) {
                fCurrentClass.addSuperWrapperMethod(pSelector, pParams.size());
            }
            return pStatements.addDirectMethodInvocation(pReceiver, pSelector, pParams);
        }
        return pStatements.addDynamicMethodInvocation(pReceiver, pSelector, pParams);
    }

    private boolean isDirectMethodInvocation(Value pReceiver) {
        return pReceiver.equals(Value.SELF) || pReceiver.equals(Value.SUPER) || pReceiver.equals(Value.OUTER_SELF) || pReceiver.equals(Value.OUTER_SUPER);
    }

    private boolean needsSuperWrapper(Value pReceiver) {
        return pReceiver.equals(Value.OUTER_SUPER);
    }



    // --------------------------------------------------------------------------
    // variable management
    //--------------------------------------------------------------------------

    public Value getValue(Token pVarNameToken, SmileBuilder pStatements) {
        assert fCurrentClass != null;
        assert fBlockScopeStack.size() > 0;

        String varName = pVarNameToken.getText();
        ScopedVariable scopedVariable = getScopedVariable(varName);
        if (scopedVariable==null) {
            return pStatements.addLookup(varName);
        }
        if (scopedVariable.getScope().equals(Scope.METHOD_CONTEXT)) {
            getMethodScope().addToMethodContext(varName);
            scopedVariable.markMethodContext();
        }
        getMethodScope().addVariableNameToken(pVarNameToken);
        return scopedVariable;
    }
    
    
    Value buildAssignmentFromKeyword(Token pKeywordToken, Value pTempVar, SmileBuilder pStatements) {
        getMethodScope().addVariableNameToken(pKeywordToken);
        
        return buildAssignment(getVarNameFromKeyword(pKeywordToken.getText()), pTempVar, pStatements);
    }

    Value buildAssignment(Token pVarNameToken, Value pTempVar, SmileBuilder pStatements) {
        getMethodScope().addVariableNameToken(pVarNameToken);
        
        return buildAssignment(pVarNameToken.getText(), pTempVar, pStatements);
    }
    
    private Value buildAssignment(String pVarName, Value pTempVar, SmileBuilder pStatements) {
        ScopedVariable scopedVariable = getScopedVariable(pVarName);
        if (scopedVariable == null) {
            LOG.warn("identifier " + pVarName + " is not known in current context. class=" + getCurrentClass().getName() + ",scope=" + getCurrentBlockScope().getName());;
            return pTempVar;
        }
        if (scopedVariable.getScope().equals(Scope.METHOD_CONTEXT)) {
            getMethodScope().addToMethodContext(pVarName);
            scopedVariable.markMethodContext();
        }
        pStatements.addAssignment(scopedVariable.getVariable(), scopedVariable.getScope(), pTempVar);
        return scopedVariable;
    }
    
    public ScopedVariable getScopedVariable(String pVarName) {
        DeclaredVariable variable = fCurrentClass.getInstanceVariable(pVarName);
        if (variable!=null) {
            if (getCurrentBlockScope() instanceof MethodDescription) {
                return new ScopedVariable(variable, Scope.SELF);
            } else {
                markNeedsOuterClass();
                return new ScopedVariable(variable, Scope.OUTER_CLASS);
            }
        }

        variable = getCurrentBlockScope().getLocalOrParameterVariable(pVarName);
        if (variable!=null ) {
            return new ScopedVariable(variable, Scope.SELF);
        }

        variable = getVariableFromOuterScopes(pVarName);
        if (variable!=null ) {
            markUsesMethodContext();
            return new ScopedVariable(variable, Scope.METHOD_CONTEXT);
        }
        return null;
    }

    void markNeedsOuterClass() {
        for (int i=fBlockScopeStack.size() -1; i > 0; i--) {
            BlockDescription   blockDescription = (BlockDescription)fBlockScopeStack.get(i);
            blockDescription.markNeedsOuterClass();
        }
    }

    private void markUsesMethodContext() {
        for (int i=fBlockScopeStack.size() -1; i > 0; i--) {
            BlockDescription   blockDescription = (BlockDescription)fBlockScopeStack.get(i);
            blockDescription.markUsesMethodContext();
        }
    }

    private DeclaredVariable getVariableFromOuterScopes(String pVarName) {
        for (int i=fBlockScopeStack.size() - 2; i>=0; i-- ) {
            DeclaredVariable    variable = getBlockScope(i).getLocalOrParameterVariable(pVarName);
            if (variable!=null) {
                return variable;
            }
        }
        return null;
    }

    
    
    //--------------------------------------------------------------------------
    // method and block descriptions
    //--------------------------------------------------------------------------


    MethodDescription buildKeywordMethodDescription(List<String> pKeywords) {
        MethodDescription methodDescription = new MethodDescription(getMethodNameFromKeywords(pKeywords), getSelectorFromKeywords(pKeywords), fCurrentClass);
        fCurrentClass.addMethodDescription(methodDescription);
        return methodDescription;
    }

    MethodDescription buildBinaryMethodDescription(String pSelector) {
        MethodDescription methodDescription = new MethodDescription(pSelector, pSelector, fCurrentClass);
        fCurrentClass.addMethodDescription(methodDescription);
        return methodDescription;
    }

    MethodDescription buildUnaryMethodDescription(String pSelector) {
        MethodDescription methodDescription =  new MethodDescription(pSelector, pSelector, fCurrentClass);
        fCurrentClass.addMethodDescription(methodDescription);
        return methodDescription;
    }

    BlockDescription buildBlockDescription() {
        BlockDescription blockDescription = new BlockDescription(getMethodScope().getBlockName(),
                getMethodScope().getBlockSimpleName(),
                getMethodScope(), currentScopeIsBlockScope());
        getMethodScope().nextBlockId();
        fCurrentClass.addBlockDescription(blockDescription);
        getMethodScope().addBlockDescription(blockDescription);

        return blockDescription;
    }



    //--------------------------------------------------------------------------
    // pragma handling
    //--------------------------------------------------------------------------

    public void processPragma(String pKeyword, MethodDescription pMethodDescription) {
        processPragma(pKeyword, null, pMethodDescription);
    }

    public void processPragma(String pKeyword, Literal pLiteral, MethodDescription pMethodDescription) {
        LOG.debug("pragma='" + pKeyword + "' " + pLiteral);
        if (pKeyword.equals("native")) {
            pMethodDescription.markAsNative();
        } else if (pKeyword.equals("native:")) {
            pMethodDescription.markAsNative();
            pMethodDescription.setJavaCode(((StringLiteral)pLiteral).getString());
        } else{
            LOG.debug("ignoring pragma");
        }
    }



    //--------------------------------------------------------------------------
    // String utility methods
    //--------------------------------------------------------------------------

    static String getMethodNameFromKeywords(List<String> pKeywords) {
        assert pKeywords.size() > 0;

        StringBuilder   methodName  = new StringBuilder();
        for (String keyword : pKeywords) {
            StringBuilder namePart  = new StringBuilder(keyword);
            StringUtil.removeLastChar(namePart);
            StringUtil.firstCharToUpper(namePart);
            methodName.append(namePart);
        }
        StringUtil.firstCharToLower(methodName);
        return methodName.toString();
    }

    private String getSelectorFromKeywords(List<String> pKeywords) {
        assert pKeywords.size() > 0;

        StringBuilder   selector  = new StringBuilder();
        for (String keyword : pKeywords) {
            selector.append(keyword);
        }
        return selector.toString();
    }

    static String getParameterNameFromBlockArgument(String pArgument) {
        assert pArgument.length() > 0;
        return pArgument.substring(1);
    }


    static String getContentFromStringLiteral(String pString) {
        assert pString.length() >= 2;

        return pString.substring(1, pString.length() - 1);
    }

    static String getVarNameFromKeyword(String pKeyword) {
        StringBuilder varName = new StringBuilder(pKeyword);
        StringUtil.removeLastChar(varName);
        return varName.toString();
    }
}