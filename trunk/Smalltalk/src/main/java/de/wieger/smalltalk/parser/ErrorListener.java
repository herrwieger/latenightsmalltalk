package de.wieger.smalltalk.parser;


public interface ErrorListener {
    void error(String pMessage, int pStart, int pEnd);
}
