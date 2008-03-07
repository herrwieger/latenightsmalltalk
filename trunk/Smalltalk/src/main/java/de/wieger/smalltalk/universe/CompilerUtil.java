package de.wieger.smalltalk.universe;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.log4j.Logger;

import de.wieger.smalltalk.smile.ClassDescription;


public class CompilerUtil {
    //--------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(CompilerUtil.class);



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private ClassPool   fClassPool;
    private Loader      fClassLoader;

    private CtClass                  fSmalltalkObjectCtClass;
    private static final CtClass[][] fParameterTypes = new CtClass[100][];


    //--------------------------------------------------------------------------
    // constructors
    //--------------------------------------------------------------------------

    public CompilerUtil() {
        fClassPool      = new ClassPool();
        fClassPool.appendSystemPath();
        fClassLoader    = new SmalltalkLoader(getClass().getClassLoader(), fClassPool);
        initParameterTypes();
    }

    private void initParameterTypes() {
        try {
            fSmalltalkObjectCtClass = fClassPool.get(JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            for (int i = 0; i < fParameterTypes.length; i++) {
                fParameterTypes[i] = new CtClass[i];
                for (int j = 0; j < i; j++) {
                    fParameterTypes[i][j] = fSmalltalkObjectCtClass;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Fatal", ex);
        }
    }



    //--------------------------------------------------------------------------
    // instance methods
    //--------------------------------------------------------------------------


    /**
     * tries to find an already declared class for pClassdescription.
     * if no class exists, subclasses the superclass of pClassdescription.
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    CtClass findOrSubclass(ClassDescription pClassDescription) throws NotFoundException, CannotCompileException {
        String  qualifiedClassname      = JavaCodingUtil.getQualifiedClassname(pClassDescription);
        String  qualifiedSuperClassName = JavaCodingUtil.getQualifiedSuperClassname(pClassDescription);
        LOG.debug("class=" + qualifiedClassname + " superClass=" + qualifiedSuperClassName);

        CtClass ctClass;
        try {
            ctClass                 = fClassPool.get(qualifiedClassname);
            CtClass superCtClass    = fClassPool.get(qualifiedSuperClassName);
            if (ctClass.getSuperclass()!=superCtClass) {
                throw new RuntimeException("native java class=" + qualifiedClassname +
                        " does not extend from " + qualifiedSuperClassName + " as declared");
            }
        } catch (NotFoundException ex) {
            ctClass = null;
        }
        if (ctClass == null) {
            LOG.debug("creating new class=" + qualifiedClassname);
            ctClass = subClass(qualifiedClassname, qualifiedSuperClassName);
            addDefaultConstructor(ctClass);
            for (String interfaceClass : pClassDescription.getInterfaces()) {
                CtClass ctInterfaceClass = fClassPool.get(interfaceClass);
                ctClass.addInterface(ctInterfaceClass);
            }
        }
        return ctClass;
    }


    /**
     * @param pSmalltalkClassname
     * @return a class for the provided pSmalltalkClassname or NULL, if no class could be found.
     */
    public Class loadClassNamed(String pSmalltalkClassname) {
        String qualifiedClassname = JavaCodingUtil.getQualifiedClassname(pSmalltalkClassname);
        return loadJavaClassNamed(qualifiedClassname);
    }

    Class loadJavaClassNamed(String qualifiedClassname) {
        try {
            return fClassLoader.loadClass(qualifiedClassname);
        } catch (ClassNotFoundException e) {
            // exception intentionally ignored. we return null for this case.
            return null;
        }
    }
    
    public CtClass getJavaClassNamed(String pJavaClassname) throws NotFoundException {
        return fClassPool.get(pJavaClassname);
    }
        


    //--------------------------------------------------------------------------
    // JavaAssist convenience methods
    //--------------------------------------------------------------------------

    CtClass subClass(String pQualifiedClassname, String pQualifiedSuperClassname) throws NotFoundException {
        CtClass superClass  = fClassPool.get(pQualifiedSuperClassname);
        LOG.debug("subClass " + pQualifiedClassname + " from " + pQualifiedSuperClassname + " classPool=" + fClassPool);
        return fClassPool.makeClass(pQualifiedClassname, superClass);
    }


    public CtClass makeClass(String pBridgeClassName) {
        return fClassPool.makeClass(pBridgeClassName);
    }

    void setClassToNonAbstract(CtClass pCtClass) {
        pCtClass.setModifiers(pCtClass.getModifiers() & ~Modifier.ABSTRACT);
    }

    void addObjectField(String pVariableName, CtClass pCtClass) throws Exception {
        addInstanceField(pVariableName, JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME, pCtClass);
    }

    void addInstanceField(String pVariableName, String pFieldType, CtClass pCtClass) throws Exception {
        CtClass ctFieldType = fClassPool.get(pFieldType);
        CtField ctField     = new CtField(ctFieldType, pVariableName, pCtClass);
        ctField.setModifiers(Modifier.PUBLIC);
        pCtClass.addField(ctField);
    }

    void addField(String pFieldDeclaration, CtClass pCtClass) {
        try {
            CtField ctField = CtField.make(pFieldDeclaration, pCtClass);
            pCtClass.addField(ctField);
        } catch (CannotCompileException ex) {
            String message = "could not compile field=" + pFieldDeclaration;
            throw new RuntimeException(message, ex);
        }
    }

    boolean hasField(CtClass pCtClass, String pFieldName) {
        try {
            pCtClass.getField(pFieldName);
            return true;
        } catch (NotFoundException ex) {
            return false;
        }
    }


    void addDefaultConstructor(CtClass pCtClass) throws CannotCompileException {
        CtConstructor   ctConstructor = new CtConstructor(new CtClass[0], pCtClass);
        ctConstructor.setBody("super();");
        pCtClass.addConstructor(ctConstructor);
    }

    void addConstructor(String pConstructorCode, CtClass pCtClass) throws CannotCompileException {
        CtConstructor   ctConstructor   = CtNewConstructor.make(
                pConstructorCode, pCtClass);
        pCtClass.addConstructor(ctConstructor);
    }


    public CtMethod addMethodWithBodyCode(CtClass pCtClass, String pMethodName, int pNumParams, String pMethodBodyCode)
            throws CannotCompileException {

        CtMethod ctMethod = CtNewMethod.make(fSmalltalkObjectCtClass, pMethodName, fParameterTypes[pNumParams], null,
                                    pMethodBodyCode, pCtClass);
        pCtClass.addMethod(ctMethod);

        return ctMethod;
    }
    
    CtMethod addMethod(CtClass pCtClass, String pMethodName, int pNumParameters, String pCode) {
        LOG.debug("adding method named=" + pMethodName + " params=" + pNumParameters);
        removeExistingMethod(pCtClass, pMethodName, pNumParameters);
        return addMethod(pCode, pCtClass);
    }

    CtMethod addMethod(String pCode, CtClass pCtClass) {
        try {
            CtMethod ctMethod = CtNewMethod.make(pCode, pCtClass);
            if (!ctMethod.isEmpty() && shallAddTraceAdvice()) {
                ctMethod.insertBefore("TRACE.debug(\"" + pCode + "\");");
            }
            pCtClass.addMethod(ctMethod);
            return ctMethod;
        } catch (CannotCompileException ex) {
            String message = "could not compile method=" + pCode;
            LOG.fatal(message);
            throw new RuntimeException(message, ex);
        }
    }

    private void removeExistingMethod(CtClass pCtClass, String pMethodName, int pNumParameters) {
        try {
            CtMethod ctMethod = pCtClass.getDeclaredMethod(pMethodName, fParameterTypes[pNumParameters]);
            LOG.debug("removing already declared method named=" + pMethodName);
            pCtClass.removeMethod(ctMethod);
        } catch (NotFoundException ex) {
        }
    }


    private void addLogger(CtClass pCtClass) throws CannotCompileException {
        CtField ctField = CtField.make("private static final org.apache.log4j.Logger TRACE = org.apache.log4j.Logger.getLogger(" + pCtClass.getName() + ".class);", pCtClass);
        pCtClass.addField(ctField);
    }

    private static boolean shallAddTraceAdvice() {
        return false;
    }
}
