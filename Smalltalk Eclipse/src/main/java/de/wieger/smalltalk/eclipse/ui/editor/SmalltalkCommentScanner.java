package de.wieger.smalltalk.eclipse.ui.editor;

import java.util.List;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;

import de.wieger.smalltalk.eclipse.ui.SmalltalkPreferenceConstants;


public class SmalltalkCommentScanner extends AbstractScriptScanner {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------
    
    private static final String[] TOKEN_PROPERTIES = {
        SmalltalkPreferenceConstants.SMALLTALK_COMMENT
    };
    
    

    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkCommentScanner(IColorManager pManager, IPreferenceStore pStore) {
        super(pManager, pStore);
        initialize();
    }

    
    //--------------------------------------------------------------------------  
    // AbstractScriptScanner methods (implementation)
    //--------------------------------------------------------------------------

    protected List createRules() {
        IToken comment = getToken(SmalltalkPreferenceConstants.SMALLTALK_COMMENT);
        setDefaultReturnToken(comment);
        
        return null;
    }

    protected String[] getTokenProperties() {
        return TOKEN_PROPERTIES;
    }
}
