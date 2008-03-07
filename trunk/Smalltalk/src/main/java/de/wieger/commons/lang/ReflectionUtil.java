package de.wieger.commons.lang;


public class ReflectionUtil {

    public static String getClassNameNullSafe(Object pReceiver) {
        if (pReceiver==null) {
            return "";
        }
        return pReceiver.getClass().getName();
    }

}
