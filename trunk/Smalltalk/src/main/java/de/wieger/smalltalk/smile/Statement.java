package de.wieger.smalltalk.smile;


public interface Statement {
    void accept(StatementVisitor pVisitor);
}
