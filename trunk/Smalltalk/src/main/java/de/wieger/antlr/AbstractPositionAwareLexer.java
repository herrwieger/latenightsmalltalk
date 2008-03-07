package de.wieger.antlr;

import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;


public abstract class AbstractPositionAwareLexer extends CharScanner {
    //--------------------------------------------------------------------------  
    // constructors
    //--------------------------------------------------------------------------

    public AbstractPositionAwareLexer() {
        super();
    }

    public AbstractPositionAwareLexer(InputBuffer pCb) {
        super(pCb);
    }

    public AbstractPositionAwareLexer(LexerSharedInputState pSharedState) {
        super(pSharedState);
    }


    
    //--------------------------------------------------------------------------  
    // Lexer methods (overridden)
    //--------------------------------------------------------------------------

    
    @Override
    public void newline() {
        // intentionally overridden and left blank. we want, that column reflects the right position
    }

    @Override
    public void tab() {
        // intentionally overridden. we want, that column reflects the right position
        setColumn(getColumn()+1);
    }
}
