package de.wieger.smalltalk.smile;


public class MessagesResult {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Value   fResult;
    private Value   fPreviousReceiver;


    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public MessagesResult(Value pResult) {
        fResult             = pResult;
        fPreviousReceiver   = pResult;
    }


    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public Value getPreviousReceiver() {
        return fPreviousReceiver;
    }

    public Value getResult() {
        return fResult;
    }

    /**
     * current result becomes receiver. result becomes pResult.
     * @param pResult
     */
    public void pushResult(Value pResult) {
        fPreviousReceiver   = fResult;
        fResult             = pResult;
    }
}
