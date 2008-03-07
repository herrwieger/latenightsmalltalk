package de.wieger.smalltalk.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import antlr.LLkParser;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.TokenBuffer;
import antlr.TokenStream;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.universe.ClassDescriptionManager;

/**
 * The AbstractClassReader is the base class for parsing class and method
 * definitions using Smalltalks fileout format.
 *
 * DESIGN_DECISION: I'm not using ANTLR to parse the "subclass" or "methodsFor"
 * expressions. Currently it seems quite to complicated for me. Doing it manually
 * was easier. I might use the SmalltalkLexer later on parsing these expressions.
 *
 * @author thomas
 */
abstract class AbstractClassReader extends LLkParser implements ErrorListener {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(AbstractClassReader.class);



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private ClassDescriptionManager fClassDescriptionManager;
    private Set<ClassDescription>   fParsedClassDescriptions = new HashSet<ClassDescription>();
    private ClassDescription        fCurrentClass;
    private String                  fCurrentCategory;

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
        StringTokenizer tokenizer   = getTokenizer(pExpression);

        try {
            ClassAndToken   cat = getClassAndToken(tokenizer);
            if (cat.fToken.toLowerCase().endsWith("subclass:")) {
                subclass(cat.fToken, cat.fClass, tokenizer, pExpression);
            } else if (cat.fToken.equals("addInterface:")){
                addInterface(cat.fClass, tokenizer);
            } else if (cat.fToken.equals("addConstructor:")){
                addConstructor(cat.fClass, tokenizer);
            } else if (cat.fToken.equals("addMethod:")){
                addMethod(cat.fClass, tokenizer);
            } else if (cat.fToken.equals("addField:")){
                addField(cat.fClass, tokenizer);
            } else {
                throw new RuntimeException("expression=" + pExpression + " not supported");
            }
        } catch (RuntimeException ex) {
            notifyListeners(ex.getMessage(), pStart, pEnd);
        }
    }

    private void subclass(String keyword, ClassDescription pSuperClassDescription, StringTokenizer tokenizer,
            String pExpression) {
        String className    = removeLiteralHash(nextToken(tokenizer));
        matchKeyword("instanceVariableNames:", tokenizer);
        String instVars     = getString(tokenizer);
        matchKeyword("classVariableNames:", tokenizer);
        String classVars    = getString(tokenizer);
        matchKeyword("poolDictionaries:", tokenizer);
        String poolDicts    = getString(tokenizer);
        matchKeyword("category:", tokenizer);
        String category     = getString(tokenizer);

        ClassDescription newClassDescription;
        if (keyword.equals("subclass:")) {
            newClassDescription = pSuperClassDescription.subclass(
                    className, instVars, classVars, poolDicts, category);
        } else if (keyword.equals("variableSubclass:")){
            newClassDescription = pSuperClassDescription.variableSubclass(
                    className, instVars, classVars, poolDicts, category);
        } else if (keyword.equals("variableByteSubclass:")){
            newClassDescription = pSuperClassDescription.variableByteSubclass(
                    className, instVars, classVars, poolDicts, category);
        } else if (keyword.equals("variableCharSubclass:")){
            newClassDescription = pSuperClassDescription.variableCharSubclass(
                    className, instVars, classVars, poolDicts, category);
        } else {
            throw new RuntimeException("expression=" + pExpression + " not supported");
        }
        fParsedClassDescriptions.add(newClassDescription);
        fParsedClassDescriptions.add(newClassDescription.getClazz());
    }

    private void addInterface(ClassDescription pClassDescription, StringTokenizer pTokenizer) {
        String  interfaceName = getString(pTokenizer);
        pClassDescription.addInterface(interfaceName);
    }

    private void addConstructor(ClassDescription pClassDescription, StringTokenizer pTokenizer) {
        String  constructor = getString(pTokenizer);
        pClassDescription.addNativeConstructor(constructor);
    }

    private void addMethod(ClassDescription pClassDescription, StringTokenizer pTokenizer) {
        String  method = getString(pTokenizer);
        pClassDescription.addNativeMethod(method);
    }

    private void addField(ClassDescription pClassDescription, StringTokenizer pTokenizer) {
        String  field = getString(pTokenizer);
        pClassDescription.addNativeField(field);
    }


    void parseMethodsFor(String pMethodsFor, int pStart, int pEnd) {
        StringTokenizer tokenizer = getTokenizer(pMethodsFor);

        try {
            ClassAndToken   cat = getClassAndToken(tokenizer);
            fCurrentClass = cat.fClass;
            fParsedClassDescriptions.add(cat.fClass);
            matchKeyword("methodsFor:", cat.fToken);
            fCurrentCategory    = getString(tokenizer);
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
        SmalltalkParser parser      = ParserUtil.getParser(pMethod);
        parser.setCurrentClass(fCurrentClass);
        try {
            MethodDescription methodDescription = parser.method();
            methodDescription.setCategory(fCurrentCategory);
            methodDescription.setSourceRange(pStart, pEnd);
        } catch (RecognitionException ex) {
            int position = ex.getColumn() - 1 + pStart;
            notifyListeners(ex.getMessage(), position, position);
        } catch (Exception ex) {
            notifyListeners(ex.getMessage(), pStart, pEnd);
        }
    }


    private String removeLiteralHash(String pString) {
        return pString.substring(1);
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

    private void matchKeyword(String pExpectedKeyword, StringTokenizer pTokenizer) {
        String keyword  = nextToken(pTokenizer);
        matchKeyword(pExpectedKeyword, keyword);
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
    public void setClassDescriptionManager(ClassDescriptionManager pClassDescriptionManager) {
        fClassDescriptionManager   = pClassDescriptionManager;
    }

    public Set<ClassDescription> getParsedClassDescriptions() {
        return fParsedClassDescriptions;
    }
    
    ClassDescription getCurrentClass() {
        return fCurrentClass;
    }

    Object getCurrentCategory() {
        return fCurrentCategory;
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
