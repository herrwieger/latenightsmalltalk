package de.wieger.smalltalk.eclipse.ui.editor;

import org.eclipse.jface.text.rules.IWordDetector;


public class SmalltalkWordDetector implements IWordDetector {
    public boolean isWordPart(char character) {
        return Character.isJavaIdentifierPart(character);
    }

    public boolean isWordStart(char character) {
        return Character.isJavaIdentifierPart(character) || character == '^';
    }
}
