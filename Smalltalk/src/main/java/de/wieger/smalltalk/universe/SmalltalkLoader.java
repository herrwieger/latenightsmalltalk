package de.wieger.smalltalk.universe;

import javassist.ClassPool;
import javassist.Loader;

import org.apache.log4j.Logger;

/**
 * SmalltalkLoader sorgt dafür, dass nur die Klassen aus dem Paket "smalltalk."
 * aus dem javasssist ClassPool geladen werden. Alle anderen Klassen werden
 * normal geladen.
 */
public class SmalltalkLoader extends Loader {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(SmalltalkLoader.class);



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public SmalltalkLoader(ClassLoader pParentClassLoader, ClassPool pClassPool) {
        super(pParentClassLoader, pClassPool);
    }



    //--------------------------------------------------------------------------
    // Loader methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    protected Class loadClassByDelegation(String pName) throws ClassNotFoundException {
        if (pName.startsWith(JavaCodingUtil.SMALLTALK_PACKAGE_NAME) && !pName.startsWith("smalltalk.shared.")) {
            return null;
        }
        return delegateToParent(pName);
    }
}
