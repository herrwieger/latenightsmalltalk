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
import de.wieger.smalltalk.parser.ParserUtil;
import de.wieger.smalltalk.parser.SmalltalkParser;
import de.wieger.smalltalk.smile.ClassDescription;
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
        String classname = "Script" + fScriptId++;
        ClassDescription    classDescription = fJavassistUniverse.getBaseClass().subclass(classname);
        try {
            parseMethod(classDescription, SCRIPT_METHODNAME + " " + pScript);
            return compileAndRun(classDescription, SCRIPT_METHODNAME);
        } catch (Exception ex) {
            throw new ScriptException(ex);
        }
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
        SmalltalkParser parser = ParserUtil.getParser(pMethodSource);
        parser.setCurrentClass(pClassDescription);
        parser.method();
    }

    public Object compileAndRun(ClassDescription pClassDescription, String pMethodName, Object ... pArgs) throws Exception {
        fJavassistUniverse.compileClasses();
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
