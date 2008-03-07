package de.wieger.smalltalk.script;

import static org.testng.Assert.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.testng.annotations.Test;


public class TestScriptEngine {
    //--------------------------------------------------------------------------  
    // test methods
    //--------------------------------------------------------------------------

    @Test
    public void testGetScriptEngineByName() {
        ScriptEngine engine = getEngine();
        assertNotNull(engine);
    }

    @Test
    public void testEval() throws ScriptException {
        ScriptEngine engine = getEngine();
        engine.eval("Transcript show:'HelloWorld';cr");
    }

    @Test
    public void testEngineGetReservedKeys() {
        ScriptEngine        engine  = getEngine();
        ScriptEngineFactory factory = engine.getFactory();
        assertEquals(factory.getEngineName(), engine.get(ScriptEngine.ENGINE));
        assertEquals(factory.getEngineVersion(), engine.get(ScriptEngine.ENGINE_VERSION));
        assertEquals(factory.getEngineName(), engine.get(ScriptEngine.NAME));
        assertEquals(factory.getLanguageName(), engine.get(ScriptEngine.LANGUAGE));
        assertEquals(factory.getLanguageVersion(), engine.get(ScriptEngine.LANGUAGE_VERSION));
    }

    @Test
    public void testGetOutputStatement() throws ScriptException {
        ScriptEngine engine = getEngine();
        engine.eval(engine.getFactory().getOutputStatement("Hallo Welt"));
    }

    
    
    //--------------------------------------------------------------------------  
    // util methods
    //--------------------------------------------------------------------------

    private ScriptEngine getEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("Smalltalk");
        return engine;
    }
}
