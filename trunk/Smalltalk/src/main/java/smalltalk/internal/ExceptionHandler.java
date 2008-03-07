package smalltalk.internal;

import smalltalk.IBlock;
import smalltalk.IException;
import smalltalk.IInteger;



public class ExceptionHandler {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private IBlock              fBlock;
    private smalltalk.Object    fSelector;
    private IBlock              fAction;



    // --------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public ExceptionHandler(IBlock pBlock, smalltalk.Object pSelector, IBlock pAction) {
        fBlock      = pBlock;
        fSelector   = pSelector;
        fAction     = pAction;
    }



    //--------------------------------------------------------------------------
    // instance methods
    //--------------------------------------------------------------------------

    public smalltalk.Object handles(smalltalk.Object pException) {
        return pException.isKindOf(fSelector);
    }

    public void handle(IException pException) {
        int argCount = ((IInteger)fAction.argCount()).intValue();
        if (argCount != 1) {
            fAction.value();
            return;
        }
        fAction.value((smalltalk.Object)pException);
        if (pException.$shouldReturn()) {
            smalltalk.Object result = pException.getResult();
            pException.$clear();
            fBlock.$returnFromException(result);
        }
        if (pException.$shouldRetry()) {
            pException.$clear();
            fBlock.$returnFromException(fBlock.value());
        }
    }
}
