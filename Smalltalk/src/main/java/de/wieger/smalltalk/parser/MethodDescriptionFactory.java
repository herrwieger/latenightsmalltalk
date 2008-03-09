package de.wieger.smalltalk.parser;

import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.MethodDescription;


public interface MethodDescriptionFactory {
    MethodDescription createMethodDescription(ClassDescription pClassDescription);
}
