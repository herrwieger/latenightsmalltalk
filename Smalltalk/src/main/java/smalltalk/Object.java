package smalltalk;

import smalltalk.shared.Unboxing;



public class Object implements Unboxing {
    public Object isKindOf(Object pClass) {
        throw new UnsupportedOperationException();
    }
    
    
    public java.lang.Object unbox() {
        return this;
    }
}
