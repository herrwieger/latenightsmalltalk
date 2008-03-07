package de.wieger.smalltalk.universe;

import de.wieger.smalltalk.smile.ClassDescription;



public interface Universe extends ClassDescriptionManager {
    void makeCurrentUniverse();

    // parsing methods

    void boot();

    void compileClasses();

    void load(String pFilename);

    int getNumberOfClassDescriptions();

    ClassDescription getBaseClass();



    // runtime methods

    Object lookup(Object pIdentifier);

    Object getBehaviorForInstancesNamed(String pInstanceName);

    Class getClassNamed(String pClassName);

    Object getTrueInstance();

    Object getFalseInstance();

    Object newOrderedCollection();

    Object newInteger(int pValue);

    Object newString(String pValue);

    Object newSymbol(String pValue);
}
