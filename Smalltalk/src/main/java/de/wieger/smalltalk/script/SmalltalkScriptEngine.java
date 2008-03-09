package de.wieger.smalltalk.script;

import java.io.IOException;
import java.io.Reader;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;

import smalltalk.shared.MethodInvoker;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.wieger.smalltalk.parser.ClassReader;
import de.wieger.smalltalk.parser.MethodDescriptionFactory;
import de.wieger.smalltalk.parser.ParserFactory;
import de.wieger.smalltalk.parser.SmalltalkParser;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.universe.JavassistUniverse;


public class SmalltalkScriptEngine extends AbstractScriptEngine {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    private static final String SCRIPT_METHODNAME = "script";



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private ScriptEngineFactory fScriptEngineFactory;
    private JavassistUniverse   fJavassistUniverse  = new JavassistUniverse();
    private int                 fScriptId;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkScriptEngine(ScriptEngineFactory pScriptEngineFactory) {
        fScriptEngineFactory = pScriptEngineFactory;
        fJavassistUniverse.boot();
    }



    //--------------------------------------------------------------------------
    // AbstractScriptEngine methods (implementation)
    //--------------------------------------------------------------------------

    public Bindings createBindings() {
        return new SimpleBindings();
    }

    public Object eval(String pScript, ScriptContext pScriptContext) throws ScriptException {
        fJavassistUniverse.makeCurrentUniverse();
        try {
            String           classname        = "Script" + fScriptId++;
            ClassDescription classDescription = fJavassistUniverse.getBaseClass().subclass(classname);            
            
            parseScript(pScript, classDescription);
            compile();
            
            Object result = null;
            for (MethodDescription methodDescription : classDescription.getMethodDescriptions()) {
                result = run(classDescription, methodDescription.getName());
            }                
            return result;
        } catch (Exception ex) {
            throw new ScriptException(ex);
        }
    }



    private String scriptMethodName(int i) {
        return SCRIPT_METHODNAME + i;
    }

    private void parseScript(String pScript, ClassDescription pClassDescription) throws RecognitionException, TokenStreamException {
        ClassReader classReader = ParserFactory.getClassReader(pScript, fJavassistUniverse, new MethodDescriptionFactory() {
            int fMethodIndex=0;
            
            @Override
            public MethodDescription createMethodDescription(ClassDescription pClassDescription) {
                String methodName = SCRIPT_METHODNAME + fMethodIndex++;
                return new MethodDescription(methodName, methodName, pClassDescription);
            }
        });
        classReader.setClassForExpressions(pClassDescription);
        classReader.fileIn();
    }



    public Object eval(Reader pReader, ScriptContext pScriptContext) throws ScriptException {
        try {
            return eval(IOUtils.toString(pReader), pScriptContext);
        } catch (IOException ex) {
            throw new ScriptException(ex);
        }
    }

    public ScriptEngineFactory getFactory() {
        return fScriptEngineFactory;
    }

    @Override
    public Object get(String pKey) {
        Object value = super.get(pKey);
        if (value!=null) {
            return value;
        }
        return fScriptEngineFactory.getParameter(pKey);
    } 
    

    
    //--------------------------------------------------------------------------
    // SmalltalkScriptEngine methods
    //--------------------------------------------------------------------------

    public ClassDescription parseMethods(String pClassName, String ... pMethodSources) throws RecognitionException, TokenStreamException {
        ClassDescription  classDescription = fJavassistUniverse.getBaseClass().subclass(pClassName);
        for (String methodSource : pMethodSources) {
            parseMethod(classDescription, methodSource);
        }
        return classDescription;
    }
    
    public void parseMethod(ClassDescription pClassDescription, String pMethodSource) throws RecognitionException, TokenStreamException {
        SmalltalkParser parser = ParserFactory.getParser(pMethodSource);
        parser.setCurrentClass(pClassDescription);
        parser.method();
    }

    
    public Object compileAndRun(ClassDescription pClassDescription, String pMethodName, Object ... pArgs) throws Exception {
        compile();
        return run(pClassDescription, pMethodName, pArgs);
    }



    private void compile() {
        fJavassistUniverse.compileClasses();
    }

    private Object run(ClassDescription pClassDescription, String pMethodName, Object... pArgs)
            throws InstantiationException, IllegalAccessException {
        String  className   = pClassDescription.getName();
        Class   clazz       = fJavassistUniverse.getClassNamed(className);

        return run(clazz, pMethodName, pArgs);
    }

    public Object run(Class pClazz, String pMethodName, Object... pArgs) throws InstantiationException,
            IllegalAccessException {
        fJavassistUniverse.makeCurrentUniverse();
        Object      instance    = pClazz.newInstance();
        MethodInvoker.initParameterTypes(fJavassistUniverse.getClassNamed("Object"));
        return MethodInvoker.invoke(instance, pMethodName, pArgs);
    }
    
    
    public Object newInteger(int pInteger) {
        return fJavassistUniverse.newInteger(pInteger);
    }

    public Object getFalse() {
        return fJavassistUniverse.getFalseInstance();
    }

    public Object getTrue() {
        return fJavassistUniverse.getTrueInstance();
    }

    public Object newSymbol(String pString) {
        return fJavassistUniverse.newSymbol(pString);
    }
    
    public Object newString(String pString) {
        return fJavassistUniverse.newString(pString);
    }

    public ClassDescription getBaseClass() {
        return fJavassistUniverse.getBaseClass();
    }

    public void load(String pFilename) {
        fJavassistUniverse.load(pFilename);
    }

    public void compileClasses() {
        fJavassistUniverse.compileClasses();
    }

    public Class getClassNamed(String pSmalltalkClassname) {
        return fJavassistUniverse.getClassNamed(pSmalltalkClassname);
    }
}
