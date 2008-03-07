package de.wieger.smalltalk.smile;


public interface DynamicMethodInvocationCodingStrategy {
    void codeDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation, JavaCoder pCoder,
            StringBuilder pCode);
}
