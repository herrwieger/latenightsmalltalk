package de.wieger.smalltalk.universe;

import de.wieger.smalltalk.smile.ClassDescription;


public interface ClassDescriptionManager {
    ClassDescription getClassDescription(String pName);
    void addClassDescription(ClassDescription pNewClassDescription);
}
