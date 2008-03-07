package smalltalk.internal;

import java.util.List;
import java.util.Stack;

import smalltalk.IBlock;
import smalltalk.IException;
import de.wieger.smalltalk.universe.AbstractUniverse;



public class ExceptionEnvironment {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static ThreadLocal<Stack<ExceptionHandler>>  sfHandlers =
        new ThreadLocal<Stack<ExceptionHandler>>();



    //--------------------------------------------------------------------------
    // class methods
    //--------------------------------------------------------------------------

    public static void pushHandler(IBlock pBlock, smalltalk.Object pSelector, IBlock pAction) {
        Stack<ExceptionHandler> handlerList = getHandlerStack();
        handlerList.push(new ExceptionHandler(pBlock, pSelector, pAction));
    }

    public static void popHandler() {
        getHandlerStack().pop();
    }


    private static Stack<ExceptionHandler> getHandlerStack() {
        Stack<ExceptionHandler>  handlerStack = sfHandlers.get();
        if (handlerStack==null) {
            handlerStack = new Stack<ExceptionHandler>();
            sfHandlers.set(handlerStack);
        }
        return handlerStack;
    }


    public static void handle(IException pException) {
        List<ExceptionHandler> handlers = getHandlerStack();
        for (int i=handlers.size() - 1; i>=0; i--) {
            ExceptionHandler    handler = handlers.get(i);
            if (handler.handles((smalltalk.Object)pException) == AbstractUniverse.getTrue()) {
                handler.handle(pException);
                if (pException.$shouldPass()) {
                    pException.$clear();
                    continue;
                }
                if (pException.$shouldResume()) {
                    return;
                }
            }
        }
        pException.defaultAction();
    }
}
