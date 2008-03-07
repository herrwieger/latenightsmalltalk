package de.wieger.smalltalk.smile;


public interface StatementVisitor {
    void visitLookup(Lookup pLookup);
    void visitAssignment(Assignment pAssignment);

    void visitNilMethodInvocation(NilMethodInvocation pNilMethodInvocation);
    void visitDirectMethodInvocation(DirectMethodInvocation pDirectMethodInvocation);
    void visitDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation);

    void visitReturn(Return pReturn);
    void visitBlockReturn(BlockReturn pBlockReturn);
}
