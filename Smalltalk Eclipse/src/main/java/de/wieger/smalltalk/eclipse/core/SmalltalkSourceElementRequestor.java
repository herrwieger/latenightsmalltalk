package de.wieger.smalltalk.eclipse.core;

import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;


public class SmalltalkSourceElementRequestor extends SourceElementRequestVisitor {
    public SmalltalkSourceElementRequestor(ISourceElementRequestor pRequestor) {
        super(pRequestor);
    }
}
