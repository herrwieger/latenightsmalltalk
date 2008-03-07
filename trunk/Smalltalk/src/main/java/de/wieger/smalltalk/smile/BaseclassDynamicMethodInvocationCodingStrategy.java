package de.wieger.smalltalk.smile;



public class BaseclassDynamicMethodInvocationCodingStrategy implements DynamicMethodInvocationCodingStrategy {
    public void codeDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation, JavaCoder pCoder,
            StringBuilder pCode) {
        pCoder.appendResultTempVarAssignment(pDynamicMethodInvocation);
        pDynamicMethodInvocation.getReceiver().accept(pCoder);
        pCode.append(".");
        pCode.append(pDynamicMethodInvocation.getMethodName());
        pCode.append("(");
        for (Value param : pDynamicMethodInvocation.getParams()) {
            param.accept(pCoder);
            pCode.append(",");
        }
        if (pDynamicMethodInvocation.getNumParams() > 0) {
            pCode.setLength(pCode.length() - 1);
        }
        pCode.append(");");
    }
}
