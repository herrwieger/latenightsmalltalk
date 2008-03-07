package de.wieger.smalltalk.smile;


public interface ValueVisitor {

    void visitSelf(AbstractValue pAbstractValue);

    void visitOuterSelf(AbstractValue pAbstractValue);

    void visitSuper(AbstractValue pAbstractValue);

    void visitOuterSuper(AbstractValue pAbstractValue);

    void visitThisContext(AbstractValue pAbstractValue);

    void visitBlockConstructor(BlockConstructor pBlockConstructor);

    void visitBooleanLiteral(BooleanLiteral pBooleanLiteral);

    void visitCharLiteral(CharLiteral pCharLiteral);

    void visitDeclaredVariable(DeclaredVariable pDeclaredVariable);

    void visitFloatLiteral(FloatLiteral pFloatLiteral);

    void visitIntegerLiteral(IntegerLiteral pIntegerLiteral);

    void visitNilLiteral(NilLiteral pNilLiteral);

    void visitScopedVariable(ScopedVariable pScopedVariable);

    void visitStringLiteral(StringLiteral pStringLiteral);

    void visitSymbolLiteral(SymbolLiteral pSymbolLiteral);

    void visitTemporaryVariable(TemporaryVariable pTemporaryVariable);

}
