/**
 * 
 */
package de.wieger.smalltalk.smile;

public abstract class AbstractValue implements Value {
    public void markReadAccess() {
        //intentionally left blank
    }

    public boolean hasReadAccess() {
        return true;
    }

    public boolean isNeverRead() {
        return false;
    }
}