package de.wieger.smalltalk.smile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import de.wieger.smalltalk.universe.ClassDescriptionManager;



public class ClassDescription {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    public static final    String  OBJECT       = "Object";
    public static final    String  BEHAVIOR     = "Behavior";



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private ClassDescriptionManager              fClassDescriptionManager;
    private String                               fName;
    private ClassDescription                     fSuperClass;
    private ClassDescription                     fClazz;
    private ClassDescription                     fInstanceClass;
    private VariabilityType                      fVariabilityType;
    private boolean                              fIsSealed;

    private Map<String, DeclaredVariable>        fInstanceVariablesByName = new HashMap<String, DeclaredVariable>();
    private Set<String>                          fPoolDictionaries        = new HashSet<String>();

    private List<MethodDescription>              fMethodDescriptions      = new ArrayList<MethodDescription>();
    private List<BlockDescription>               fBlockDescriptions       = new ArrayList<BlockDescription>();
    private List<SuperWrapperMethod>             fSuperWrapperMethods     = new ArrayList<SuperWrapperMethod>();

    private List<String>                         fNativeConstructors      = new ArrayList<String>();
    private List<String>                         fNativeFields            = new ArrayList<String>();
    private List<String>                         fNativeMethods           = new ArrayList<String>();
    private List<String>                         fInterfaces              = new ArrayList<String>();
    private Map<String, NumberLiteral>           fNumberLiteralsByString  = new HashMap<String, NumberLiteral>();



    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public ClassDescription(
            ClassDescriptionManager pClassDescriptionManager,
            String                  pName,
            ClassDescription        pSuperClass,
            ClassDescription        pClazz,
            VariabilityType         pVariabilityType) {

        fClassDescriptionManager    = pClassDescriptionManager;
        fName                       = pName;
        fSuperClass                 = pSuperClass;
        fClazz                      = pClazz;
        fVariabilityType            = pVariabilityType;
    }




    //--------------------------------------------------------------------------
    // instance methods
    //--------------------------------------------------------------------------

    public ClassDescription subclass(
            String  pName,
            String  pInstanceVariableNames,
            String  pClassVariableNames,
            String  pPoolDictionaries,
            String  pCategory
            ) {
        return subclass(pName, pInstanceVariableNames,
                pClassVariableNames, pPoolDictionaries, pCategory, VariabilityType.NONE);
    }

    public ClassDescription variableSubclass(
            String  pName,
            String  pInstanceVariableNames,
            String  pClassVariableNames,
            String  pPoolDictionaries,
            String  pCategory
            ) {
        return subclass(pName, pInstanceVariableNames,
                pClassVariableNames, pPoolDictionaries, pCategory, VariabilityType.OBJECT);
    }

    public ClassDescription variableByteSubclass(
            String  pName,
            String  pInstanceVariableNames,
            String  pClassVariableNames,
            String  pPoolDictionaries,
            String  pCategory
            ) {
        return subclass(pName, pInstanceVariableNames,
                pClassVariableNames, pPoolDictionaries, pCategory, VariabilityType.BYTE);
    }

    public ClassDescription variableCharSubclass(
        String  pName,
        String  pInstanceVariableNames,
        String  pClassVariableNames,
        String  pPoolDictionaries,
        String  pCategory
        ) {
        return subclass(pName, pInstanceVariableNames,
                pClassVariableNames, pPoolDictionaries, pCategory, VariabilityType.CHAR);
    }


    public ClassDescription subclass(String pName) {
        return subclass(
                pName, "", "", "", "", VariabilityType.NONE);
    }

    /**
     * Gets or creates ClassDescriptions for a class named pName and its metaclass.
     * Afterwards seals the class and its metaclass, that it can't be declared again.
     */
    private ClassDescription subclass(
            String          pName,
            String          pInstanceVariableNames,
            String          pClassVariableNames,
            String          pPoolDictionaries,
            String          pCategory,
            VariabilityType pVariabilityType
            ) {

        String              metaClassName   = pName + " class";
        ClassDescription    newMetaclass    = fClassDescriptionManager.getClassDescription(metaClassName);
        if (newMetaclass == null ) {
            newMetaclass = new ClassDescription(fClassDescriptionManager, metaClassName,
                    getClazz(), getClazz().getClazz(), VariabilityType.NONE);

            fClassDescriptionManager.addClassDescription(newMetaclass);
        }
        newMetaclass.seal();

        ClassDescription    newClass        = fClassDescriptionManager.getClassDescription(pName);
        if (newClass == null) {
            newClass = new ClassDescription(fClassDescriptionManager, pName, this, newMetaclass, pVariabilityType);
            fClassDescriptionManager.addClassDescription(newClass);
        }
        newClass.seal();

        newMetaclass.setInstanceClass(newClass);
        newMetaclass.addInstanceVariableNames(asList(pClassVariableNames));
        newClass.addInstanceVariableNames(asList(pInstanceVariableNames));
        newClass.addPoolDictionaries(asList(pPoolDictionaries));

        return newClass;
    }

    public static List<String> asList(String pNames) {
        String  trimmedNames    = pNames.trim();
        if (trimmedNames.length()==0) {
            return Collections.emptyList();
        }
        List<String> names = Arrays.asList(trimmedNames.split("\\s+"));
        return names;
    }


    /**
     * Marks this class as sealed. Invoking this method twice raises a
     * RuntimeException.
     */
    private void seal() {
        if (fIsSealed) {
            throw new RuntimeException("FATAL: trying to declare class=" + getName() + " twice");
        }
        fIsSealed = true;
    }

    private void addInstanceVariableNames(Collection<String> pInstanceVariableNames) {
        for (String varName : pInstanceVariableNames) {
            addInstanceVariable(varName);
        }
    }

    private void addInstanceVariable(String pVarName) {
        fInstanceVariablesByName.put(pVarName, new DeclaredVariable(pVarName));
    }

    void addPoolDictionaries(Collection<String> pPoolDictionaries) {
        fPoolDictionaries.addAll(pPoolDictionaries);
    }





    //--------------------------------------------------------------------------
    // accessor methods
    //--------------------------------------------------------------------------

    public String getName() {
        return fName;
    }

    public ClassDescription getSuperClass() {
        return fSuperClass;
    }

    /**
     * returns true, if this class is the behavior class or one of its subclasses.
     * @return
     */
    public boolean isBehaviour() {
        if (fName.equals(BEHAVIOR)) {
            return true;
        }
        if (getSuperClass() == null ) {
            return false;
        }
        return getSuperClass().isBehaviour();
    }

    public ClassDescription getClazz() {
        return fClazz;
    }

    public void setClazz(ClassDescription pClassDescription) {
        fClazz  = pClassDescription;
    }


    public ClassDescription getInstanceClass() {
        return fInstanceClass;
    }

    public boolean isConstructorClass() {
        return fInstanceClass!=null;
    }

    public void setInstanceClass(ClassDescription pClassDescription) {
        fInstanceClass = pClassDescription;
    }

    public boolean isVariable() {
        return fVariabilityType != VariabilityType.NONE;
    }

    /**
     * returns true, if one of this classes superclasses is variable.
     * @return
     */
    public boolean isVariableSubclass() {
        if (fSuperClass==null) {
            return false;
        }
        if (fSuperClass.isVariable()) {
            return true;
        }
        return fSuperClass.isVariableSubclass();
    }

    /**
     * returns true, if this class has an instance class and that class is variable or a variable subclass.
     * @return
     */
    public boolean isVariableConstructorClass() {
        if (fInstanceClass == null) {
            return false;
        }
        return fInstanceClass.isVariable() || fInstanceClass.isVariableSubclass();
    }

    /**
     * returns the instance variable which has been declared in this class
     * or in one of this classes superclasses.
     */
    public DeclaredVariable getInstanceVariable(String pVarName) {
        DeclaredVariable instanceVariable = fInstanceVariablesByName.get(pVarName);
        if (instanceVariable!=null) {
            return instanceVariable;
        }
        if (getSuperClass()!=null) {
            return getSuperClass().getInstanceVariable(pVarName);
        }
        return null;
    }

    public Collection<DeclaredVariable> getInstanceVariables() {
        return fInstanceVariablesByName.values();
    }


    public void addMethodDescription(MethodDescription pMethodDescription) {
        fMethodDescriptions.add(pMethodDescription);
    }

    public List<MethodDescription> getMethodDescriptions() {
        return fMethodDescriptions;
    }

    @SuppressWarnings("unchecked")
    public Collection<MethodDescription> getNonNativeMethodDescriptions() {
        Collection<MethodDescription> select = CollectionUtils.select(fMethodDescriptions, new Predicate() {
                    public boolean evaluate(Object pObject) {
                        return ((MethodDescription)pObject).isNotNative();
                    }
                });
        return select;
    }

    @SuppressWarnings("unchecked")
    public Collection<MethodDescription> getMethodsToCode() {
        Collection<MethodDescription> select = CollectionUtils.select(fMethodDescriptions, new Predicate() {
                    public boolean evaluate(Object pObject) {
                        return ((MethodDescription)pObject).isNotNativeToSkip();
                    }
                });
        return select;
    }


    public void addBlockDescription(BlockDescription pBlockDescription) {
        fBlockDescriptions.add(pBlockDescription);
    }

    public List<BlockDescription> getBlockDescriptions() {
        return fBlockDescriptions;
    }

    public void addSuperWrapperMethod(String pSelector, int pNumArgs) {
        fSuperWrapperMethods.add(new SuperWrapperMethod(pSelector, pNumArgs));
    }

    public List<SuperWrapperMethod> getSuperWrapperMethods() {
        return fSuperWrapperMethods;
    }

    public void addInterface(String pInterfaceName) {
        fInterfaces.add(pInterfaceName);
    }
    public List<String> getInterfaces() {
        return fInterfaces;
    }

    public void addNativeConstructor(String pNativeConstructor) {
        fNativeConstructors.add(pNativeConstructor);
    }

    public List<String> getNativeConstructors() {
        return fNativeConstructors;
    }


    public void addNativeField(String pField) {
        fNativeFields.add(pField);
    }

    public List<String> getNativeFields() {
        return fNativeFields;
    }

    public void addNativeMethod(String pNativeMethod) {
        fNativeMethods.add(pNativeMethod);
    }

    public List<String> getNativeMethods() {
        return fNativeMethods;
    }
    
    
    public void addNumberLiteral(String pValue, NumberLiteral pNumberLiteral) {
        fNumberLiteralsByString.put(pValue, pNumberLiteral);
    }
    
    public NumberLiteral getNumberLiteral(String pValue) {
        return fNumberLiteralsByString.get(pValue);
    }
        
    public Collection<NumberLiteral> getNumberLiterals() {
        return fNumberLiteralsByString.values();
    }



    //--------------------------------------------------------------------------
    // Object methods (overridden)
    //--------------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getName() + "[" + fName + "]";
    }


    //--------------------------------------------------------------------------
    // enum
    //--------------------------------------------------------------------------

    public enum VariabilityType {
        NONE,
        BYTE,
        CHAR,
        OBJECT
    }
}
