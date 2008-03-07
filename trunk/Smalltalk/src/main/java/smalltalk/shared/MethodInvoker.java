package smalltalk.shared;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.wieger.commons.lang.ReflectionUtil;


public class MethodInvoker {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    private static Class[][] PARAMETER_TYPES;


    
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger    LOG             = Logger.getLogger(MethodInvoker.class);
    private static final Object[]  NO_PARAMS       = new Object[0];



    //--------------------------------------------------------------------------
    // class methods
    //--------------------------------------------------------------------------

    public static void initParameterTypes(Class pSmalltalkObjectClass) {
        PARAMETER_TYPES = new Class[100][];
        for (int i=0; i<PARAMETER_TYPES.length; i++) {
            PARAMETER_TYPES[i] = new Class[i];
            for (int j=0; j<i; j++) {
                PARAMETER_TYPES[i][j] = pSmalltalkObjectClass;
            }
        }
    }

    public static Object invoke(Object pReceiver, String pMethodName) {
        return invoke(pReceiver, pMethodName, NO_PARAMS);
    }

    public static Object invoke(Object pReceiver, String pMethodName, Object[] pArgs) {
        LOG.debug(invocationInfo(pReceiver, pMethodName) + " args=" + Arrays.asList(pArgs));

        assert pReceiver!=null: "receiver for " + pMethodName + " must not be null";

        try {
            Method method = pReceiver.getClass().getMethod(pMethodName, PARAMETER_TYPES[pArgs.length]);
            Object result = method.invoke(pReceiver, pArgs);
            LOG.debug(invocationInfo(pReceiver, pMethodName) + " result=" + result);
            return result;
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException)ex.getCause();
            }
            throw new InvocationFailed(pReceiver, pMethodName, pArgs, ex);
        } catch (Exception ex) {
            LOG.error("does not understand");
            throw new DoesNotUnderstand(pReceiver, pMethodName, pArgs, ex);
        }
    }

    public static Object invokeStatic(Class pReceiver, String pMethodName, Object... pArgs) {
        LOG.debug(invocationInfo(pReceiver, pMethodName) + " args=" + Arrays.asList(pArgs));

        assert pReceiver!=null: "receiver for " + pMethodName + " must not be null";

        try {
            Method method = pReceiver.getMethod(pMethodName, PARAMETER_TYPES[pArgs.length]);
            Object result = method.invoke(pReceiver, pArgs);
            LOG.debug(invocationInfo(pReceiver, pMethodName) + " result=" + result);
            return result;
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException)ex.getCause();
            }
            throw new InvocationFailed(pReceiver, pMethodName, pArgs, ex);
        } catch (Exception ex) {
            LOG.error("does not understand");
            throw new DoesNotUnderstand(pReceiver, pMethodName, pArgs, ex);
        }
    }

    private static String invocationInfo(Object pReceiver, String pMethodName) {
        return "invoke on=@" + ReflectionUtil.getClassNameNullSafe(pReceiver) + ":" + pReceiver + " method=" + pMethodName;
    }
}
