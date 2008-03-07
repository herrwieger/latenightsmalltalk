package de.wieger.smalltalk.eclipse.core;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class SmalltalkCodeRule implements IPredicateRule {
    //--------------------------------------------------------------------------  
    // instance variables
    //--------------------------------------------------------------------------

    private IToken fSuccessToken;

    
    
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkCodeRule(IToken pCode) {
        fSuccessToken = pCode;
    }
    

    
    //--------------------------------------------------------------------------  
    // IPredicateRule methods (implementation)
    //--------------------------------------------------------------------------

    public IToken evaluate(ICharacterScanner pScanner) {
        return evaluate(pScanner, false);
    }
    
    public IToken evaluate(ICharacterScanner pScanner, boolean pResume) {
        int ch;
        boolean matched = false;
        while ((ch = pScanner.read()) != ICharacterScanner.EOF) {
            if (ch=='"' || ch=='\'') {
                pScanner.unread();
                return matched ? fSuccessToken : Token.UNDEFINED;
            }
            matched = true;
        }
        return Token.UNDEFINED;
    }

    public IToken getSuccessToken() {
        return fSuccessToken;
    }
}
