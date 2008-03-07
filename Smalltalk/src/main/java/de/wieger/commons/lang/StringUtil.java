package de.wieger.commons.lang;


public class StringUtil {
    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    private StringUtil() {
        // disable instantiation
    }



    //--------------------------------------------------------------------------
    // class methods
    //--------------------------------------------------------------------------

    public static void removeLastChar(StringBuilder pStringBuilder) {
        assert pStringBuilder.length() > 0;

        pStringBuilder.setLength(pStringBuilder.length() -1);
    }

    public static void firstCharToLower(StringBuilder pBuilder) {
        assert pBuilder.length() > 0;

        pBuilder.setCharAt(0, Character.toLowerCase(pBuilder.charAt(0)));
    }

    public static String firstCharToUpper(String pString) {
        StringBuilder   builder = new StringBuilder(pString);
        firstCharToUpper(builder);
        return builder.toString();
    }

    public static void firstCharToUpper(StringBuilder pBuilder) {
        assert pBuilder.length() > 0;

        pBuilder.setCharAt(0, Character.toUpperCase(pBuilder.charAt(0)));
    }

}
