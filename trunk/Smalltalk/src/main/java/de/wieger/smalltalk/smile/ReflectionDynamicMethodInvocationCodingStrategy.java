package de.wieger.smalltalk.smile;

import smalltalk.shared.MethodInvoker;
import de.wieger.smalltalk.universe.JavaCodingUtil;


public class ReflectionDynamicMethodInvocationCodingStrategy implements DynamicMethodInvocationCodingStrategy {
    //--------------------------------------------------------------------------  
    // constants
    //--------------------------------------------------------------------------

    private static final String PARAMS = "params";
    
    
    
    //--------------------------------------------------------------------------  
    // class variables
    //--------------------------------------------------------------------------

    private int sfId;
    
    

    //--------------------------------------------------------------------------  
    // DynamicMethodInvocationCodingStrategy methods (implementation)
    //--------------------------------------------------------------------------

    public void codeDynamicMethodInvocation(DynamicMethodInvocation pDynamicMethodInvocation, JavaCoder pCoder, StringBuilder pCode) {
        int paramId = sfId++;
        if (pDynamicMethodInvocation.getNumParams() > 0) {
            pCode.append(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + "[] " + PARAMS);
            pCode.append(paramId);
            pCode.append(" = new " + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + "[");
            pCode.append(pDynamicMethodInvocation.getNumParams());
            pCode.append("];");
            int i=0;
            for (Value param : pDynamicMethodInvocation.getParams()) {
                pCode.append(PARAMS);
                pCode.append(paramId);
                pCode.append("[");
                pCode.append(i++);
                pCode.append("] = ");
                param.accept(pCoder);
                pCode.append(";");
            }
        }
        pCoder.appendResultTempVarAssignment(pDynamicMethodInvocation);
        pCode.append("(" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ")");
        pCode.append(MethodInvoker.class.getName());
        pCode.append(".invoke(");
        pDynamicMethodInvocation.getReceiver().accept(pCoder);
        pCode.append(", ");
        pCode.append("\"");
        pCode.append(pDynamicMethodInvocation.getMethodName());
        pCode.append("\"");
        if (pDynamicMethodInvocation.getNumParams() > 0) {
            pCode.append(", " + PARAMS);
            pCode.append(paramId);
        }
        pCode.append(");\n");
    }
}
