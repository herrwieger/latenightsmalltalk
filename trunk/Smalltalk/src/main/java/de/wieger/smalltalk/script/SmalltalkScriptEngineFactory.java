package de.wieger.smalltalk.script;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;


public class SmalltalkScriptEngineFactory implements ScriptEngineFactory {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private static final List<String> fNames        = Arrays.asList(new String[]{"Smalltalk"});
    private static final List<String> fExtensions   = Arrays.asList(new String[]{".st"});
    private static final List<String> fMimeTypes    = Arrays.asList(new String[]{
            "text/plain",
            "text/smalltalk",
            "application/smalltalk"
            });



    //--------------------------------------------------------------------------
    // ScriptEngineFactory methods (implementation)
    //--------------------------------------------------------------------------

    public String getEngineName() {
        return "Latenight Smalltalk";
    }

    public String getEngineVersion() {
        return "1.0.0.0";
    }

    public List<String> getExtensions() {
        return fExtensions;
    }

    public String getLanguageName() {
        return "Smalltalk";
    }

    public String getLanguageVersion() {
        return "1.0";
    }

    public String getMethodCallSyntax(String pReceiver, String pMethodname, String... pArgs) {
        return null;
    }

    public List<String> getMimeTypes() {
        return fMimeTypes;
    }

    public List<String> getNames() {
        return fNames;
    }

    public String getOutputStatement(String pString) {
        return "Transcript show:'" + pString + "';cr!";
    }

    public Object getParameter(String pKey) {
        if (pKey.equals(ScriptEngine.ENGINE)) {
            return getEngineName();
        }
        if (pKey.equals(ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        }
        if (pKey.equals(ScriptEngine.NAME)) {
            return getEngineName();
        }
        if (pKey.equals(ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        }
        if (pKey.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        }
        if (pKey.equals("THREADING")) {
            return null;
        }
        return null;
    }

    public String getProgram(String... pArg0) {
        return null;
    }

    public ScriptEngine getScriptEngine() {
        return new SmalltalkScriptEngine(this);
    }
}
