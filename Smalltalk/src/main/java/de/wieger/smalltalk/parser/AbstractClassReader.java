package de.wieger.smalltalk.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import antlr.LLkParser;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.TokenBuffer;
import antlr.TokenStream;
import de.wieger.smalltalk.smile.AbstractMethodInvocation;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.Lookup;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.smile.Statement;
import de.wieger.smalltalk.smile.StringLiteral;
import de.wieger.smalltalk.smile.SymbolLiteral;
import de.wieger.smalltalk.smile.Value;
import de.wieger.smalltalk.smile.ClassDescription.VariabilityType;
import de.wieger.smalltalk.universe.ClassDescriptionManager;

/**
 * The AbstractClassReader is the base class for parsing class and method
 * definitions using Smalltalks fileout format.
 *
 * DESIGN_DECISION: I'm not using ANTLR to parse "methodsFor"
 * expressions. Currently it seems quite to complicated for me. Doing it manually
 * was easier. I might use the SmalltalkLexer later on parsing these expressions.
 *
 * @author thomas
 */
abstract class AbstractClassReader extends LLkParser implements ErrorListener, MethodDescriptionFactory {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(AbstractClassReader.class);



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------
    
    private ClassDescriptionManager     fClassDescriptionManager;
    private MethodDescriptionFactory    fMethodDescriptionFactory;

    private ClassDescription        fClassForExpressions        = new ClassDescription(null, "Dummy", null, null,
                                                                          VariabilityType.NONE);
    
    private Set<ClassDescription>   fParsedClassDescriptions    = new HashSet<ClassDescription>();
    private ClassDescription        fClassForMethods;
    private String                  fCategoryForMethods;

    private List<ErrorListener>     fErrorListeners          = new ArrayList<ErrorListener>();






    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public AbstractClassReader(int pK_) {
        super(pK_);
        addErrorListener(this);
    }

    public AbstractClassReader(ParserSharedInputState pState, int pK_) {
        super(pState, pK_);
        addErrorListener(this);
    }

    public AbstractClassReader(TokenBuffer pTokenBuf, int pK_) {
        super(pTokenBuf, pK_);
        addErrorListener(this);
    }

    public AbstractClassReader(TokenStream pLexer, int pK_) {
        super(pLexer, pK_);
        addErrorListener(this);
    }



    //--------------------------------------------------------------------------
    // parser methods
    //--------------------------------------------------------------------------

    void parseExpression(String pExpression, int pStart, int pEnd) {
        try {
            SmalltalkParser parser      = ParserFactory.getParser(pExpression);
            MethodDescription methodDescription = fMethodDescriptionFactory.createMethodDescription(fClassForExpressions);
            parser.parseStatements(fClassForExpressions, methodDescription);
            if (!evaluate(methodDescription.getStatements())) {
                fClassForExpressions.addMethodDescription(methodDescription);
            }
        } catch (Exception ex) {
            notifyListeners(ex.getMessage(), pStart, pEnd);
        }
    }
    
    
    /**
     * tries to evaluate filein format expressions: subclass:, addInterface:
     * addConstructor: addMethod: addField:.
     * @param statements
     * @return true, if it could evaluate the statements.
     */
    private boolean evaluate(List<Statement> statements) {
        if (statements.size() < 3 || statements.size() > 4) {
            return false;
        }
        
        Iterator<Statement> statementIterator = statements.iterator();
        Statement           statement         = statementIterator.next();
        if (!(statement instanceof Lookup)) {
            return false;
        }
        ClassDescription classDescription  = fClassDescriptionManager.getClassDescription(((Lookup) statement).getIdentifier());                     

        statement = statementIterator.next();
        if (statement instanceof AbstractMethodInvocation
                && ((AbstractMethodInvocation) statement).getSelector().equals("class")) {
            classDescription     = classDescription.getClazz();
            statement = statementIterator.next();
        }
        
        if (!(statement instanceof AbstractMethodInvocation)) {
            return false;
        }
        AbstractMethodInvocation methodInvocation = (AbstractMethodInvocation) statement;
        String                   selector         = methodInvocation.getSelector();
        if (selector.toLowerCase().endsWith("subclassinstancevariablenamesclassvariablenamespooldictionariescategory")) {
            subclass(selector, classDescription, methodInvocation.getParams());
        } else if (selector.equals("addInterface")){
            addInterface(classDescription, methodInvocation.getParams());
        } else if (selector.equals("addConstructor")){
            addConstructor(classDescription, methodInvocation.getParams());
        } else if (selector.equals("addMethod")){
            addMethod(classDescription, methodInvocation.getParams());
        } else if (selector.equals("addField")){
            addField(classDescription, methodInvocation.getParams());
        } else {
            return false;
        }
        return true;
    }

    private void subclass(String pSelector, ClassDescription pSuperClassDescription, List<Value> pParams) {
        String className    = ((SymbolLiteral)pParams.get(0)).getSymbol();
        String instVars     = getString(pParams, 1);
        String classVars    = getString(pParams, 2);
        String poolDicts    = getString(pParams, 3);
        String category     = getString(pParams, 4);

        ClassDescription newClassDescription;
        if (pSelector.startsWith("subclass")) {
            newClassDescription = pSuperClassDescription.subclass(
                    className, instVars, classVars, poolDicts, category);
        } else if (pSelector.startsWith("variableSubclass")){
            newClassDescription = pSuperClassDescription.variableSubclass(
                    className, instVars, classVars, poolDicts, category);
        } else if (pSelector.startsWith("variableByteSubclass")){
            newClassDescription = pSuperClassDescription.variableByteSubclass(
                    className, instVars, classVars, poolDicts, category);
        } else if (pSelector.startsWith("variableCharSubclass")){
            newClassDescription = pSuperClassDescription.variableCharSubclass(
                    className, instVars, classVars, poolDicts, category);
        } else {
            throw new RuntimeException("expression=" + pSelector + " not supported");
        }
        fParsedClassDescriptions.add(newClassDescription);
        fParsedClassDescriptions.add(newClassDescription.getClazz());
    }

    public String getString(List<Value> pParams, int pIndex) {
        return ((StringLiteral)pParams.get(pIndex)).getString();
    }

    private void addInterface(ClassDescription pClassDescription, List<Value> pParams) {
        String  interfaceName = getString(pParams, 0);
        pClassDescription.addInterface(interfaceName);
    }

    private void addConstructor(ClassDescription pClassDescription, List<Value> pParams) {
        String  constructor = getString(pParams, 0);
        pClassDescription.addNativeConstructor(constructor);
    }

    private void addMethod(ClassDescription pClassDescription, List<Value> pParams) {
        String  method = getString(pParams, 0);
        pClassDescription.addNativeMethod(method);
    }

    private void addField(ClassDescription pClassDescription, List<Value> pParams) {
        String  field = getString(pParams, 0);
        pClassDescription.addNativeField(field);
    }


    void parseMethodsFor(String pMethodsFor, int pStart, int pEnd) {
        StringTokenizer tokenizer = getTokenizer(pMethodsFor);

        try {
            ClassAndToken   cat = getClassAndToken(tokenizer);
            fClassForMethods = cat.fClass;
            fParsedClassDescriptions.add(cat.fClass);
            matchKeyword("methodsFor:", cat.fToken);
            fCategoryForMethods    = getString(tokenizer);
        } catch (RuntimeException ex) {
            notifyListeners(ex.getMessage(), pStart, pEnd);
        }
    }

    private ClassAndToken getClassAndToken(StringTokenizer pTokenizer) {
        ClassAndToken   cat = new ClassAndToken();

        String          className       = nextToken(pTokenizer);
        cat.fClass = fClassDescriptionManager.getClassDescription(className);

        cat.fToken = nextToken(pTokenizer);
        if (cat.fToken.equals("class")) {
            cat.fClass  = cat.fClass.getClazz();
            cat.fToken  = nextToken(pTokenizer);
        }
        return cat;
    }

    private static class ClassAndToken {
        ClassDescription    fClass;
        String              fToken;
    }


    void parseMethod(String pMethod, int pStart, int pEnd) {
        SmalltalkParser parser      = ParserFactory.getParser(pMethod);
        parser.setCurrentClass(fClassForMethods);
        try {
            MethodDescription methodDescription = parser.method();
            methodDescription.setCategory(fCategoryForMethods);
            methodDescription.setSourceRange(pStart, pEnd);
        } catch (RecognitionException ex) {
            int position = ex.getColumn() - 1 + pStart;
            notifyListeners(ex.getMessage(), position, position);
        } catch (Exception ex) {
            notifyListeners(ex.getMessage(), pStart, pEnd);
        }
    }

    
    StringTokenizer getTokenizer(String pExpression) {
        return new StringTokenizer(pExpression, " \t\n\r\f'", true);
    }

    String getString(StringTokenizer pTokenizer) {
        String          token       = nextToken(pTokenizer);
        if (!token.startsWith("'")) {
            throw new RuntimeException("Start of string expected. Got " + token);
        }
        StringBuilder   smallString = new StringBuilder(token);
        while (smallString.length()==1 ||
               smallString.charAt(smallString.length()-1) != '\'') {
            token   = pTokenizer.nextToken();
            smallString.append(token);
        }
        smallString.deleteCharAt(0);
        smallString.setLength(smallString.length()-1);
        return smallString.toString();
    }

    private void matchKeyword(String pExpectedKeyword, String keyword) {
        if (!keyword.equals(pExpectedKeyword)) {
            throw new RuntimeException("method with " + keyword + " not supported. Expected " + pExpectedKeyword);
        }
    }

    private String nextToken(StringTokenizer pTokenizer) {
        while (true) {
            String  token = pTokenizer.nextToken().trim();
            if (token.length()>0) {
                return token;
            }
        }
    }



    //--------------------------------------------------------------------------
    // accessor method (package visible)
    //--------------------------------------------------------------------------

    /**
     * TODO this belongs into the constructor!
     */
    public void setup(ClassDescriptionManager pClassDescriptionManager, MethodDescriptionFactory pMethodDescriptionFactory) {
        fClassDescriptionManager  = pClassDescriptionManager;
        fMethodDescriptionFactory = pMethodDescriptionFactory;
    }
    
    public Set<ClassDescription> getParsedClassDescriptions() {
        return fParsedClassDescriptions;
    }
    
    public void setClassForMethods(ClassDescription pClassDescription) {
        fClassForMethods = pClassDescription;
    }
    
    ClassDescription getClassForMethods() {
        return fClassForMethods;
    }

    Object getCategoryForMethods() {
        return fCategoryForMethods;
    }
    
    public void setClassForExpressions(ClassDescription pClassDescription) {
        fClassForExpressions    = pClassDescription;
    }
    
    
    
    //--------------------------------------------------------------------------  
    // error reporting methods
    //--------------------------------------------------------------------------

    public void addErrorListener(ErrorListener pErrorListener) {
        fErrorListeners.add(pErrorListener);
    }

    protected void notifyListeners(String pMessage, int pStart, int pEnd) {
        for (ErrorListener errorListener : fErrorListeners) {
            errorListener.error(pMessage, pStart, pEnd);
        }
    }
    
    
    //--------------------------------------------------------------------------  
    // MethodDescriptionFactory methods (implementation)
    //--------------------------------------------------------------------------

    public MethodDescription createMethodDescription(ClassDescription pClassDescription) {
        return new MethodDescription("dummy", "dummy", fClassForExpressions);
    }
    
    
    
    //--------------------------------------------------------------------------  
    // ErrorListener methods (implementation)
    //--------------------------------------------------------------------------

    public void error(String pMessage, int pStart, int pEnd) {
        System.err.println(pMessage + ",start=" + pStart + ", end=" + pEnd);
    }

    
    
    //--------------------------------------------------------------------------  
    // inner class
    //--------------------------------------------------------------------------

    protected static class Content {
        //--------------------------------------------------------------------------  
        // instance variables
        //--------------------------------------------------------------------------

        private int             fStart      = Integer.MAX_VALUE;
        private StringBuilder   fContent    = new StringBuilder();

        
        
        //--------------------------------------------------------------------------  
        // accessor methods
        //--------------------------------------------------------------------------

        protected void setStart(int pPosition) {
            if (pPosition < fStart) {
                fStart = pPosition;
            }
        }
        
        protected int getStart() {
            return fStart;
        }
        
        protected int getEnd() {
            return fStart + fContent.toString().length();
        }
        
        
        protected void append(String pText) {
            fContent.append(pText);
        }
        
        protected void append(char pChar) {
            fContent.append(pChar);
        }
        
        public String toString() {
            return fContent.toString();
        }
    }
}
