package de.wieger.smalltalk.eclipse.ui.editor;

import java.util.List;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;

import de.wieger.smalltalk.eclipse.ui.SmalltalkPreferenceConstants;


public class SmalltalkStringScanner extends AbstractScriptScanner {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------
    
    private static final String[] TOKEN_PROPERTIES = {
        SmalltalkPreferenceConstants.SMALLTALK_STRING
    };
    
    

    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkStringScanner(IColorManager pManager, IPreferenceStore pStore) {
        super(pManager, pStore);
        initialize();
    }

    
    
    //--------------------------------------------------------------------------  
    // instance methods
    //--------------------------------------------------------------------------

    protected List createRules() {
        IToken string = getToken(SmalltalkPreferenceConstants.SMALLTALK_STRING);
        setDefaultReturnToken(string);
        
        return null;
    }

    protected String[] getTokenProperties() {
        return TOKEN_PROPERTIES;
    }
}
