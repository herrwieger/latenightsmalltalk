package de.wieger.smalltalk.smile;


public enum Scope {
    SELF(""),
    OUTER_CLASS("$outerClass"),
    METHOD_CONTEXT("$methodContext");



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private String fName;
    private String fAccessPrefix;



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    Scope(String pName) {
        fName           = pName;
        fAccessPrefix   = pName.length() == 0 ? "" : fName + ".";
    }



    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public String getName() {
        return fName;
    }

    public String getAccessPrefix() {
        return fAccessPrefix;
    }
}
