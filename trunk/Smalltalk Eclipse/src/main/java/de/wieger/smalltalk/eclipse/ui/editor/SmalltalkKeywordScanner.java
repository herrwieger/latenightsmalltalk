package de.wieger.smalltalk.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.DLTKColorConstants;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import de.wieger.smalltalk.eclipse.ui.SmalltalkPreferenceConstants;


public class SmalltalkKeywordScanner extends AbstractScriptScanner {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------
    
    private static final String[] TOKEN_PROPERTIES = {
        DLTKColorConstants.DLTK_DEFAULT,
        SmalltalkPreferenceConstants.SMALLTALK_KEYWORD,
    };
    
    

    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkKeywordScanner(IColorManager pManager, IPreferenceStore pStore) {
        super(pManager, pStore);
        initialize();
    }

    
    
    //--------------------------------------------------------------------------  
    // AbstractScriptScanner methods (implementation)
    //--------------------------------------------------------------------------

    @Override
    protected List createRules() {
        IToken defaultToken     = getToken(DLTKColorConstants.DLTK_DEFAULT);
        setDefaultReturnToken(defaultToken);
        
        IToken keyword = getToken(SmalltalkPreferenceConstants.SMALLTALK_KEYWORD);
        
        List<IRule> rules = new ArrayList<IRule>();

        rules.add(new WhitespaceRule(new SmalltalkWhitespaceDetector()));
        WordRule wordRule = new WordRule(new SmalltalkWordDetector(), defaultToken);
        wordRule.addWord("nil", keyword);
        wordRule.addWord("true", keyword);
        wordRule.addWord("false", keyword);
        wordRule.addWord("self", keyword);
        wordRule.addWord("super", keyword);
        wordRule.addWord("thisContext", keyword);
        wordRule.addWord("^", keyword);
        rules.add(wordRule);

        return rules;
    }

    @Override
    protected String[] getTokenProperties() {
        return TOKEN_PROPERTIES;
    }



    //--------------------------------------------------------------------------  
    // accessor methods
    //--------------------------------------------------------------------------

    IDocument getDocument() {
        return fDocument;
    }
}
