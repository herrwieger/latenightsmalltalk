package de.wieger.smalltalk.universe;

import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import smalltalk.shared.MethodInvoker;
import de.wieger.smalltalk.parser.ClassReader;
import de.wieger.smalltalk.parser.ClassReaderLexer;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.ClassDescription.VariabilityType;



public abstract class AbstractUniverse implements Universe {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    static final            Logger  LOG     = Logger.getLogger(AbstractUniverse.class);


    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static          AbstractUniverse sfCurrentUniverse;



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private Map<String, ClassDescription> fClassDescriptionsByName    = new HashMap<String, ClassDescription>();
    private List<ClassDescription>        fClassDescriptionsToCompile = new ArrayList<ClassDescription>();

    private Object                        fTrue;
    private Object                        fFalse;



    // --------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public AbstractUniverse() {
        setupMetaclasses();
    }



    //--------------------------------------------------------------------------
    // Universe context methods
    //--------------------------------------------------------------------------

    public static Universe getCurrentUniverse() {
        return sfCurrentUniverse;
    }

    public static void clearCurrentUniverse() {
        sfCurrentUniverse = null;
    }

    public void makeCurrentUniverse() {
        sfCurrentUniverse = this;
    }



    //--------------------------------------------------------------------------
    // instance methods
    //--------------------------------------------------------------------------

    /**
     * <table>
     * <thead>
     *      <th>Instance</th><th>Class</th><th>Metaclass</th>
     * </thead>
     * <tbody>
     *      <tr><td>Object</td><td>Class</td><td>Metaclass</td></tr>
     *      <tr><td>Behavior</td><td>Behavior</td><td>Metaclass class</td></tr>
     *      <tr><td>Class</td><td>Metaclass</td><td>Metaclass class</td></tr>
     *      <tr><td>Metaclass</td><td>Metaclass class</td><td>Metaclass class</td></tr>
     * </tbody>
     * </table>
     */
    protected void setupMetaclasses() {
        ClassDescription objectClass        = new ClassDescription(this, ClassDescription.OBJECT, null, null, VariabilityType.NONE);
        addClassDescription(objectClass);
    
        ClassDescription behaviorClass      = new ClassDescription(this, ClassDescription.BEHAVIOR, objectClass, null, VariabilityType.NONE);
        addClassDescription(behaviorClass);
    
        ClassDescription classClass         = new ClassDescription(this, "Class", behaviorClass, null, VariabilityType.NONE);
        addClassDescription(classClass);
    
        ClassDescription metaClass          = new ClassDescription(this, "Metaclass", classClass, null, VariabilityType.NONE);
        addClassDescription(metaClass);
    
        objectClass.setClazz(classClass);
    
        ClassDescription behaviorMetaclass  = new ClassDescription(this, "Behavior class", classClass, metaClass, VariabilityType.NONE);
        addClassDescription(behaviorMetaclass);
        behaviorClass.setClazz(behaviorMetaclass);
        behaviorMetaclass.setClazz(metaClass);
    
        ClassDescription classMetaclass  = new ClassDescription(this, "Class class", behaviorMetaclass, metaClass, VariabilityType.NONE);
        addClassDescription(classMetaclass);
        classClass.setClazz(classMetaclass);
        classMetaclass.setClazz(metaClass);
    
        ClassDescription metaMetaClazz      = new ClassDescription(this, "Metaclass class", classClass, null, VariabilityType.NONE);
        addClassDescription(metaMetaClazz);
        metaClass.setClazz(metaMetaClazz);
        metaMetaClazz.setClazz(metaClass);
    }
    
    
    public void boot() {
        loadSmalltalkKernel();
        compileClasses();
    }


    protected void loadSmalltalkKernel() {
        load("src/main/smalltalk/block.st");
        load("src/main/smalltalk/object.st");
        load("src/main/smalltalk/behavior.st");
        load("src/main/smalltalk/class.st");
        load("src/main/smalltalk/metaclass.st");

        load("src/main/smalltalk/boolean.st");
        load("src/main/smalltalk/true.st");
        load("src/main/smalltalk/false.st");

        load("src/main/smalltalk/magnitude.st");
        load("src/main/smalltalk/number.st");
        load("src/main/smalltalk/integer.st");
        load("src/main/smalltalk/float.st");
        load("src/main/smalltalk/char.st");

        load("src/main/smalltalk/exception.st");
        load("src/main/smalltalk/error.st");
        load("src/main/smalltalk/unhandlederror.st");

        load("src/main/smalltalk/collection.st");
        load("src/main/smalltalk/bag.st");
        load("src/main/smalltalk/interval.st");
        load("src/main/smalltalk/indexedcollection.st");

        load("src/main/smalltalk/dictionary.st");

        load("src/main/smalltalk/arrayedcollection.st");
        load("src/main/smalltalk/array.st");

        load("src/main/smalltalk/chararray.st");
        load("src/main/smalltalk/bytearray.st");

        load("src/main/smalltalk/string.st");
        load("src/main/smalltalk/symbol.st");

        load("src/main/smalltalk/orderedcollection.st");
        load("src/main/smalltalk/sortedcollection.st");

        load("src/main/smalltalk/link.st");
        load("src/main/smalltalk/list.st");
        load("src/main/smalltalk/set.st");

        load("src/main/smalltalk/transcript.st");
    }

    public void compileClasses() {
        compileClasses(fClassDescriptionsToCompile);
        fClassDescriptionsToCompile.clear();
    }


    protected abstract void compileClasses(List<ClassDescription> pClassDescriptionsToCompile);    

    

    public void load(String pFilename) {
        try {
            FileReader          fileReader  = new FileReader(pFilename);
            ClassReaderLexer    lexer       = new ClassReaderLexer(fileReader);
            ClassReader         reader      = new ClassReader(lexer);
            reader.setClassDescriptionManager(this);
            reader.fileIn();
            fileReader.close();
        } catch (Exception ex) {
            LOG.fatal("failure", ex);
            throw new RuntimeException("loading file=" + pFilename + " failed", ex);
        }
    }


    List<ClassDescription> getClassDescriptionsToCompile() {
        return fClassDescriptionsToCompile;
    }

    public void addClassDescription(ClassDescription pClassDescription) {
        fClassDescriptionsByName.put(pClassDescription.getName(), pClassDescription);
        fClassDescriptionsToCompile.add(pClassDescription);
    }

    public ClassDescription getClassDescription(String pName) {
        return fClassDescriptionsByName.get(pName);
    }

    public ClassDescription getBaseClass() {
        return getClassDescription(ClassDescription.OBJECT);
    }

    public int getNumberOfClassDescriptions() {
        return fClassDescriptionsByName.size();
    }


    
    //--------------------------------------------------------------------------
    // runtime class methods
    //--------------------------------------------------------------------------

    public static Object getBoolean(boolean pBoolean) {
        return sfCurrentUniverse.getBooleanInstance(pBoolean);
    }

    public static Object getTrue() {
        return sfCurrentUniverse.getTrueInstance();
    }

    public static Object getFalse() {
        return sfCurrentUniverse.getFalseInstance();
    }



    //--------------------------------------------------------------------------
    // runtime instance methods
    //--------------------------------------------------------------------------

    public Object lookup(Object pIdentifier) {
        String identifierAsJavaString = pIdentifier.toString();
        Object result = (Object)getBehaviorForInstancesNamed(identifierAsJavaString);
        if (result!=null) {
            return result;
        }
        return getJavaClassBridge(identifierAsJavaString);
    }

    public Object getBehaviorForInstancesNamed(String pInstanceName) {
        Class       behaviorClass       = getClassNamed(pInstanceName);
        if (behaviorClass==null) {
            return null;
        }
        return MethodInvoker.invokeStatic(behaviorClass, JavaCodingUtil.GET_CLAZZ_METHOD_NAME);
    }
    
    public Object newInstanceNamed(String pInstanceName) {
        Object behaviour = getBehaviorForInstancesNamed(pInstanceName);
        return MethodInvoker.invoke(behaviour, "$new");
    }

    protected abstract Object getJavaClassBridge(String pIdentifierAsJavaString);


    public Object getBooleanInstance(boolean pBoolean) {
        return pBoolean ? getTrueInstance() : getFalseInstance();
    }

    public synchronized Object getTrueInstance() {
        if (fTrue==null) {
            fTrue = newInstanceNamed("True");
        }
        return fTrue;
    }

    public synchronized Object getFalseInstance() {
        if (fFalse==null) {
            fFalse = newInstanceNamed("False");
        }
        return fFalse;
    }

    public Object newOrderedCollection() {
        return newInstanceNamed("OrderedCollection");
    }

    public Object newInteger(int pValue) {
        Class integerClazz = getClassNamed("Integer");
        try {
            Constructor integerConstructor = integerClazz.getConstructor(Integer.TYPE);
            return integerConstructor.newInstance(pValue);
        } catch (Exception ex) {
            throw new RuntimeException("newInteger failed", ex);
        }
    }

    public Object newSymbol(String pValue) {
        Class symbolClazz = getClassNamed("Symbol");
        try {
            Constructor symbolConstructor = symbolClazz.getConstructor(String.class);
            return symbolConstructor.newInstance(pValue);
        } catch (Exception ex) {
            throw new RuntimeException("newString failed", ex);
        }
    }

    public Object newString(String pValue) {
        Class stringClazz = getClassNamed("String");
        try {
            Constructor stringConstructor = stringClazz.getConstructor(String.class);
            return stringConstructor.newInstance(pValue);
        } catch (Exception ex) {
            throw new RuntimeException("newString failed", ex);
        }
    }

    public abstract Class getClassNamed(String pSmalltalkClassName);
}
