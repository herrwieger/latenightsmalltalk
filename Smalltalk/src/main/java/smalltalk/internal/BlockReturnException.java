package smalltalk.internal;



public class BlockReturnException extends RuntimeException {
    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Object  fResult;
    private boolean fIsExceptional;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public BlockReturnException(Object pResult) {
        this(pResult, false);
    }

    public BlockReturnException(Object pResult, boolean pIsExceptional) {
        fResult         = pResult;
        fIsExceptional  = pIsExceptional;
    }



    //--------------------------------------------------------------------------  
    // Throwable methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    /**
     * this improves performance significantly. we don't need the stacktrace!
     */
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    
    
    
    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public Object getResult() {
        return fResult;
    }

    public boolean isExceptional() {
        return fIsExceptional;
    }
}
