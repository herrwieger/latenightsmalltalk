package de.wieger.smalltalk.universe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.log4j.Logger;
import org.dynalang.mop.beans.BeansMetaobjectProtocol;

import smalltalk.shared.Unboxing;
import de.wieger.smalltalk.smile.BlockDescription;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.DeclaredVariable;
import de.wieger.smalltalk.smile.JavaCoder;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.smile.NumberLiteral;
import de.wieger.smalltalk.smile.ReflectionDynamicMethodInvocationCodingStrategy;
import de.wieger.smalltalk.smile.Scope;
import de.wieger.smalltalk.smile.SuperWrapperMethod;


public class JavassistUniverse extends AbstractUniverse implements Universe {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    private static final String     BRIDGE_CLASS_POSTFIX                 = "$Class";
    private static final String     COMMA                                = ", ";
    private static final String     INTERNAL_NEW_METHOD_NAME             = "$$new";



    // --------------------------------------------------------------------------
    // class variables
    //--------------------------------------------------------------------------

    static final Logger     LOG = Logger.getLogger(JavassistUniverse.class);



    //--------------------------------------------------------------------------
    // instance variables
    //--------------------------------------------------------------------------

    private CompilerUtil            fCompilerUtil                        = new CompilerUtil();
    private List<ClassDescription>  fPreviouslyCompiledClassDescriptions = new ArrayList<ClassDescription>();


    private Class                   fSmalltalkObjectClass;

    private Map<String, Object>     fJavaClassBridgeInstanceByName       = new HashMap<String, Object>();
    private Map<Class, Class>       fJavaInstanceBridgeClassByClass      = new HashMap<Class, Class>();

    private BeansMetaobjectProtocol fMetaObjectProtocol                  = new BeansMetaobjectProtocol();
    


    //--------------------------------------------------------------------------  
    // class methods
    //--------------------------------------------------------------------------

    public static JavassistUniverse getCurrentJavassistUniverse() {
        return (JavassistUniverse)AbstractUniverse.getCurrentUniverse();
    }
    
    
    
    // --------------------------------------------------------------------------
    // instance methods (API)
    //--------------------------------------------------------------------------

    
    @Override
    public void boot() {
        super.boot();
        fSmalltalkObjectClass  = getClassNamed("Object");
    }

    @Override
    protected void compileClasses(List<ClassDescription> pClassDescriptionsToCompile) {
        _compileClasses(pClassDescriptionsToCompile);
        fPreviouslyCompiledClassDescriptions.addAll(pClassDescriptionsToCompile);
    }

    public void storeClasses(String pPath) {
        storeClasses(fPreviouslyCompiledClassDescriptions, pPath);
        fPreviouslyCompiledClassDescriptions.clear();
    }


    CompilerUtil getCompilerUtil() {
        return fCompilerUtil;
    }

    public Class getClassNamed(String pSmalltalkClassname) {
        return fCompilerUtil.loadClassNamed(pSmalltalkClassname);
    }

    protected Object getJavaClassBridge(String pJavaClassname) {
        Object javaClassBridge = fJavaClassBridgeInstanceByName.get(pJavaClassname);
        if (javaClassBridge!=null) {
            return javaClassBridge;
        }
        try {
            javaClassBridge = createJavaClassBridgeClass(pJavaClassname).newInstance();
        } catch (Exception ex) {
            LOG.fatal("", ex);
            throw new RuntimeException(ex);
        }
        fJavaClassBridgeInstanceByName.put(pJavaClassname, javaClassBridge);
        return javaClassBridge;
    }

    
    public Object[] unbox(Object[] pArgs) {
        Object[] unboxedArgs = new Object[pArgs.length];
        for (int i=0; i<pArgs.length; i++) {
            unboxedArgs[i] = ((Unboxing)pArgs[i]).unbox();
        }
        return unboxedArgs;
    }
    
    
    public Object getJavaInstanceBridge(Object pObject) {
        if (pObject == null) {
            return null;
        }
        if (fSmalltalkObjectClass.isAssignableFrom(pObject.getClass())) {
            return pObject;
        }
        
        if (pObject instanceof String) {
            return newString((String)pObject);
        }
        if (pObject instanceof Boolean) {
            return ((Boolean)pObject).booleanValue() ? getTrueInstance() : getFalseInstance();
        }
        if (pObject instanceof Integer) {
            return newInteger((Integer)pObject);
        }
        
        Class bridgeInstanceClass = getJavaInstanceBridgeClass(pObject.getClass());
        try {
            return bridgeInstanceClass.getConstructor(new Class[] { pObject.getClass() }).newInstance(pObject);
        } catch (Exception ex) {
            LOG.fatal("could not instantiate", ex);
            throw new RuntimeException(ex);
        }
    }
    
    protected Class getJavaInstanceBridgeClass(Class pClazz) {
        Class javaInstanceBridgeClass = fJavaInstanceBridgeClassByClass.get(pClazz);
        if (javaInstanceBridgeClass!=null) {
            return javaInstanceBridgeClass;
        }
        javaInstanceBridgeClass = createJavaInstanceBridgeClass(pClazz);
        fJavaInstanceBridgeClassByClass.put(pClazz, javaInstanceBridgeClass);
        return javaInstanceBridgeClass;
    }
    
    public BeansMetaobjectProtocol getBeansMetaobjectProtocol() {
        return fMetaObjectProtocol;
    }
    

    
    //--------------------------------------------------------------------------  
    // compilation methods
    //--------------------------------------------------------------------------

    private void _compileClasses(List<ClassDescription> pClassDescriptions) {
        try {
            createClasses(pClassDescriptions);
            Map<ClassDescription, Map<MethodDescription, CtMethod>> methodsByDescriptionForClassDescription = new HashMap<ClassDescription, Map<MethodDescription,CtMethod>>();
            for (ClassDescription classDescription : pClassDescriptions) {
                Map<MethodDescription, CtMethod> methodsByDescription = compilePass1(classDescription);
                methodsByDescriptionForClassDescription.put(classDescription, methodsByDescription);
            }
            for (ClassDescription classDescription : pClassDescriptions) {
                compilePass2(classDescription, methodsByDescriptionForClassDescription.get(classDescription));
            }
            for (ClassDescription classDescription : pClassDescriptions) {
                if (classDescription.isConstructorClass()) {
                    addBasicNew(classDescription, fCompilerUtil.findOrSubclass(classDescription));
                }
            }
        } catch (Exception ex) {
            LOG.fatal("could not compile", ex);
            throw new RuntimeException(ex);
        }
    }

    private void storeClasses(List<ClassDescription> pClassDescriptions, String pPath) {
        try {
            for (ClassDescription classDescription : pClassDescriptions) {
                CtClass ctClass     = fCompilerUtil.findOrSubclass(classDescription);
                ctClass.writeFile(pPath);
            }
        } catch (Exception ex) {
            LOG.fatal("could not store", ex);
            throw new RuntimeException(ex);
        }
    }


    void createClasses(List<ClassDescription> pClassDescriptions) throws NotFoundException, CannotCompileException {
        for (ClassDescription classDescription : pClassDescriptions) {
            fCompilerUtil.findOrSubclass(classDescription);
        }
    }

    Map<MethodDescription, CtMethod> compilePass1(ClassDescription pClassDescription) throws Exception {
        LOG.debug("Compile pass 1. class=" + pClassDescription.getName());
        CtClass ctClass = fCompilerUtil.findOrSubclass(pClassDescription);
// TODO
//        if (shallAddTraceAdvice())  {
//            addLogger(ctClass);
//        }
        for (String fieldDeclaration : pClassDescription.getNativeFields()) {
            fCompilerUtil.addField(fieldDeclaration, ctClass);
        }
        addInstanceVariables(pClassDescription, ctClass);
        if (pClassDescription.isVariable()) {
            addVariableClassArray(pClassDescription, ctClass);
        }
        createMethodContexts(pClassDescription);
        Map<MethodDescription, CtMethod> methodsByDescription = addAbstractMethods(pClassDescription, ctClass);
        
        for (String constructorDeclaration : pClassDescription.getNativeConstructors()) {
            fCompilerUtil.addConstructor(constructorDeclaration, ctClass);
        }
        for (String methodDeclaration : pClassDescription.getNativeMethods()) {
            fCompilerUtil.addMethod(methodDeclaration, ctClass);
        }
        for (NumberLiteral numberLiteral : pClassDescription.getNumberLiterals()) {
            String numberLiteralDeclaration = JavaCodingUtil.getNumberLiteralDeclaration(numberLiteral);
            LOG.debug("declaring literal " + numberLiteralDeclaration);
            fCompilerUtil.addField(numberLiteralDeclaration, ctClass);
        }
        return methodsByDescription;
    }

    void compilePass2(ClassDescription pClassDescription, Map<MethodDescription, CtMethod> pMethodsByDescription) throws Exception {
        LOG.debug("Compile pass 1. class=" + pClassDescription.getName());
        CtClass ctClass = fCompilerUtil.findOrSubclass(pClassDescription);

        addClassClassFieldAndAccessor(pClassDescription, ctClass);
        if (pClassDescription.isVariable()) {
            addVariableClassConstructorAndAccessors(pClassDescription, ctClass);
        } else if (pClassDescription.isVariableSubclass()) {
            addVariableSubclassConstructor(pClassDescription, ctClass);
        } else {
            // do nothing
        }
        createHomeActivationExceptions(pClassDescription);
        createBlockClassesWithoutValueMethods(pClassDescription);
        addSuperWrapperMethods(pClassDescription, ctClass);
        addMethodBodies(pMethodsByDescription, ctClass);
        createBlockMethods(pClassDescription);
        fCompilerUtil.setClassToNonAbstract(ctClass);
    }

    private static final Logger LOG_CLASSCLASS = Logger.getLogger(CompilerUtil.class.getName() + ".addClassClassFieldAndAccessor");

    private void addClassClassFieldAndAccessor(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        String classClassFieldCode = JavaCodingUtil.getClassClassFieldCode(pClassDescription);
        LOG_CLASSCLASS.debug("addClassClassFieldAndAccessor class=" + pClassDescription.getName() + ", code=" + classClassFieldCode);
        CtField ctField = CtField.make(classClassFieldCode, pCtClass);
        pCtClass.addField(ctField);

        fCompilerUtil.addMethod(JavaCodingUtil.getStaticClassClassAccessorInitializerMethodCode(pClassDescription), pCtClass);
        fCompilerUtil.addMethod(JavaCodingUtil.getClassClassAccessorMethodCode(pClassDescription), pCtClass);
    }

    private void addInstanceVariables(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        for (DeclaredVariable variable : pClassDescription.getInstanceVariables()) {
            LOG.debug("addInstanceVariable=" + JavaCoder.codeValue(variable));
            if (fCompilerUtil.hasField(pCtClass, JavaCoder.codeValue(variable).toString())) {
                LOG.debug("skipping native field=" + JavaCoder.codeValue(variable));
                continue;
            }
            fCompilerUtil.addObjectField(JavaCoder.codeValue(variable).toString(), pCtClass);
        }
    }

    private void addVariableClassArray(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        if (!fCompilerUtil.hasField(pCtClass, JavaCodingUtil.ARRAY_FIELD_NAME)) {
            fCompilerUtil.addInstanceField(JavaCodingUtil.ARRAY_FIELD_NAME, JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + "[]", pCtClass);
        }
    }

    private void addVariableClassConstructorAndAccessors(ClassDescription pClassDescription, CtClass pCtClass) throws CannotCompileException {
        String variableClassConstructorCode = JavaCodingUtil.getVariableClassConstructorCode(pClassDescription);
        fCompilerUtil.addConstructor(variableClassConstructorCode, pCtClass);
        fCompilerUtil.addMethod(pCtClass, "basicAt", 1, JavaCodingUtil.BASIC_AT_METHOD_CODE);
        fCompilerUtil.addMethod(pCtClass, "basicAtPut", 2, JavaCodingUtil.BASIC_AT_PUT_METHOD_CODE);
        fCompilerUtil.addMethod(pCtClass, "basicSize", 0, JavaCodingUtil.BASIC_SIZE_METHOD_CODE);
    }

    private void addVariableSubclassConstructor(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        String variableSubclassConstructorCode = JavaCodingUtil.getVariableSubclassConstructorCode(pClassDescription);
        fCompilerUtil.addConstructor(variableSubclassConstructorCode, pCtClass);
    }

    private void addBasicNew(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        if (pClassDescription.isVariableConstructorClass()) {
            fCompilerUtil.addMethod(pCtClass, "basicNew", 1, JavaCodingUtil.getBasicNewSizeMethodCode(pClassDescription));
        } else {
            fCompilerUtil.addMethod(pCtClass, "basicNew", 0, JavaCodingUtil.getBasicNewMethodCode(pClassDescription));
        }
    }

    private void createMethodContexts(ClassDescription pClassDescription) throws Exception {
        for (MethodDescription methodDescription : pClassDescription.getNonNativeMethodDescriptions()) {
            if (!methodDescription.isWithContext()) {
                continue;
            }
            CtClass methodContextCtClass            = fCompilerUtil.subClass(methodDescription.getMethodContextName(), "java.lang.Object");
            fCompilerUtil.addDefaultConstructor(methodContextCtClass);
            for (String variableName : methodDescription.getMethodContextVariableNames()) {
                fCompilerUtil.addObjectField(variableName, methodContextCtClass);
            }
        }
    }

    private void createHomeActivationExceptions(ClassDescription pClassDescription) throws NotFoundException,
            CannotCompileException {
        for (MethodDescription methodDescription : pClassDescription.getNonNativeMethodDescriptions()) {
            if (methodDescription.hasNoBlockDescriptions()) {
                continue;
            }
            CtClass homeActivationExceptionClass = fCompilerUtil.subClass(methodDescription
                                                           .getHomeActivationExceptionName(),
                                                           "smalltalk.internal.BlockReturnException");

            CtClass       objectCtClass  = fCompilerUtil.getJavaClassNamed("java.lang.Object");
            CtConstructor ctConstructor1 = CtNewConstructor.make(new CtClass[] { objectCtClass }, null,
                                                   homeActivationExceptionClass);
            homeActivationExceptionClass.addConstructor(ctConstructor1);

            CtConstructor ctConstructor2 = CtNewConstructor.make(new CtClass[] { objectCtClass, CtClass.booleanType },
                                                   null, homeActivationExceptionClass);
            homeActivationExceptionClass.addConstructor(ctConstructor2);
        }
    }


    private void createBlockClassesWithoutValueMethods(ClassDescription pClassDescription) throws Exception {
        for (BlockDescription blockDescription : pClassDescription.getBlockDescriptions()) {
            if (blockDescription.belongsToNativeMethod()) {
                continue;
            }
            createBlockClass(blockDescription, pClassDescription);
        }
    }

    private void createBlockClass(BlockDescription blockDescription, ClassDescription pClassDescription) throws Exception {
        LOG.debug("createBlockClass " + blockDescription.getName());
        CtClass blockCtClass            = fCompilerUtil.subClass(blockDescription.getName(), BlockDescription.BLOCK_QNAME);
// TODO
//        if (shallAddTraceAdvice())  {
//            addLogger(blockCtClass);
//        }

        List<CtClass> constructorArgs = addBlockFields(blockDescription, pClassDescription.getName(), blockCtClass);
        addBlockConstructor(blockDescription, blockCtClass, constructorArgs);
        fCompilerUtil.addMethod(blockCtClass, JavaCodingUtil.BLOCK_RETURN_METHOD_NAME, 0, blockDescription.getReturnMethodCode());
        fCompilerUtil.addMethod(blockCtClass, JavaCodingUtil.BLOCK_RETURN_FROM_EXCEPTION_METHOD_NAME, 0, blockDescription.getReturnFromExceptionMethodCode());
    }

    private List<CtClass> addBlockFields(BlockDescription pBlockDescription, String pOuterClassName, CtClass pBlockCtClass) throws Exception {
        List<CtClass>   constructorArgs = new ArrayList<CtClass>();
        if (pBlockDescription.isUsingOuterClass()) {
            String qualifiedOuterClassname = JavaCodingUtil.getQualifiedClassname(pOuterClassName);
            LOG.debug("addBlockField outerclass=" + qualifiedOuterClassname);
            fCompilerUtil.addInstanceField(Scope.OUTER_CLASS.getName(), qualifiedOuterClassname, pBlockCtClass);
            constructorArgs.add(fCompilerUtil.getJavaClassNamed(qualifiedOuterClassname));
        }
        if (pBlockDescription.isUsingMethodContext()) {
            fCompilerUtil.addInstanceField(Scope.METHOD_CONTEXT.getName(), pBlockDescription.getMethodContextName(), pBlockCtClass);
            constructorArgs.add(fCompilerUtil.getJavaClassNamed(pBlockDescription.getMethodContextName()));
        }
        return constructorArgs;
    }

    private void addBlockConstructor(BlockDescription pBlockDescription, CtClass pBlockCtClass, List<CtClass> pConstructorArgs) throws Exception {
        CtConstructor   ctConstructor = new CtConstructor(toArray(pConstructorArgs), pBlockCtClass);
        String constructorBody = pBlockDescription.getConstructorBody();
        LOG.debug(constructorBody);
        ctConstructor.setBody(constructorBody);
        pBlockCtClass.addConstructor(ctConstructor);
    }

    private CtClass[] toArray(List<CtClass> constructorArgs) {
        return constructorArgs.toArray(new CtClass[constructorArgs.size()]);
    }


    private void addSuperWrapperMethods(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        for (SuperWrapperMethod superWrapperMethod : pClassDescription.getSuperWrapperMethods()) {
            LOG.debug("superwrapper method=" + superWrapperMethod.getName());
            LOG.debug("code=" + superWrapperMethod.getCode(createJavaCoder()));
            fCompilerUtil.addMethod(pCtClass, superWrapperMethod.getMethodName(), superWrapperMethod.getNumParameters(), superWrapperMethod.getCode(createJavaCoder()));
        }
    }


    private void createBlockMethods(ClassDescription pClassDescription) throws Exception {
        for (BlockDescription blockDescription : pClassDescription.getBlockDescriptions()) {
            if (blockDescription.belongsToNativeMethod()) {
                continue;
            }
            LOG.debug("block name=" + blockDescription.getName());
            LOG.debug("code=" + blockDescription.getCode(createJavaCoder()));

            CtClass     ctClass     = fCompilerUtil.getJavaClassNamed(blockDescription.getName());
            fCompilerUtil.addMethod(ctClass, blockDescription.getMethodName(), blockDescription.getNumParameters(), blockDescription.getCode(createJavaCoder()));
        }
    }

    private Map<MethodDescription, CtMethod> addAbstractMethods(ClassDescription pClassDescription, CtClass pCtClass) throws Exception {
        Map<MethodDescription, CtMethod> methodsByDescription = new HashMap<MethodDescription, CtMethod>();
        for (MethodDescription methodDescription : pClassDescription.getMethodDescriptions()) {
            if (methodDescription.isNativeToSkip()) {
                continue;
            }
            CtMethod ctMethod = fCompilerUtil.addMethod(pCtClass, methodDescription.getMethodName(), methodDescription.getNumParameters(), methodDescription.getAbstractMethodCode());
            methodsByDescription.put(methodDescription, ctMethod);
        }
        return methodsByDescription;
    }

    private void addMethodBodies(Map<MethodDescription, CtMethod> pMethodsByDescription, CtClass pCtClass) {
        for (Map.Entry<MethodDescription, CtMethod> entry : pMethodsByDescription.entrySet()) {
            MethodDescription   methodDescription   = entry.getKey();
            LOG.debug(methodDescription.toString());

            String methodCode = methodDescription.getCode(createJavaCoder());
            LOG.debug(methodDescription.toString() + " code=" + methodCode);
            try {
                fCompilerUtil.addMethod(pCtClass, methodDescription.getMethodName(), methodDescription.getNumParameters(), methodCode);
            } catch (Exception ex) {
                LOG.debug("could not compile " + methodDescription + " code=" + methodCode);
                throw new RuntimeException(ex);
            }
        }
    }


    private JavaCoder createJavaCoder() {
        return new JavaCoder(new ReflectionDynamicMethodInvocationCodingStrategy(), false, false);
    }


    
    //--------------------------------------------------------------------------
    // JavaBridge support
    //--------------------------------------------------------------------------

    Class createJavaClassBridgeClass(String pJavaClassname) {
        CtClass javaCtClass;
        try {
            javaCtClass = fCompilerUtil.getJavaClassNamed(pJavaClassname);
        } catch (NotFoundException ex) {
            throw new RuntimeException(ex);
        }
        try {
            String  bridgeClassName = JavaCodingUtil.SMALLTALK_PACKAGE_NAME + "." + pJavaClassname
                                              + BRIDGE_CLASS_POSTFIX;
            CtClass bridgeCtClass   = fCompilerUtil.subClass(bridgeClassName,
                                              JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            
            List<CtMethod> publicClassMethods = getPublicClassMethods(javaCtClass);
            addBridgeClassMethods(bridgeCtClass, publicClassMethods);
            
            List<CtConstructor> publicConstructors = getPublicConstructors(javaCtClass);
            addBridgeNewMethods(bridgeCtClass, publicConstructors);
            
            return fCompilerUtil.loadJavaClassNamed(bridgeClassName);
        } catch (Exception ex) {
            String errorMessage = "cant create class bridge for class "+ pJavaClassname;
            LOG.fatal(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    private List<CtMethod> getPublicClassMethods(CtClass javaCtClass) {
        List<CtMethod>  publicClassMethods = new ArrayList<CtMethod>();
        for (CtMethod ctMethod : javaCtClass.getMethods()) {
            if (isPublicClassMethod(ctMethod)) {
                publicClassMethods.add(ctMethod);
            }
        }
        return publicClassMethods;
    }

    private boolean isPublicClassMethod(CtMethod ctMethod) {
        return Modifier.isStatic(ctMethod.getModifiers()) && Modifier.isPublic(ctMethod.getModifiers());
    }

    private void addBridgeClassMethods(CtClass pBridgeCtClass, List<CtMethod> pPublicClassMethods) throws NotFoundException {
        for (CtMethod ctMethod : pPublicClassMethods) {
            addBridgeDelegatingMethod(pBridgeCtClass, ctMethod);
        }
        Map<MethodSelector, List<CtMethod>> methodsBySelector = getMethodsBySelector(pPublicClassMethods);
        for (MethodSelector selector : methodsBySelector.keySet()) {
            addBridgeClassMethod(pBridgeCtClass, methodsBySelector.get(selector));
        }
    }

    private void addBridgeDelegatingMethod(CtClass pBridgeCtClass, CtMethod pCtMethod) throws NotFoundException {
        StringBuilder methodCodeBuilder = new StringBuilder();
        methodCodeBuilder.append("public ");
        methodCodeBuilder.append(pCtMethod.getReturnType().getName());
        methodCodeBuilder.append(" ");
        methodCodeBuilder.append("$$" + pCtMethod.getName());
        appendMethodParameters(methodCodeBuilder, pCtMethod.getParameterTypes());
        methodCodeBuilder.append("{ return ");
        methodCodeBuilder.append(pCtMethod.getDeclaringClass().getName());
        methodCodeBuilder.append(".");
        methodCodeBuilder.append(pCtMethod.getName());
        methodCodeBuilder.append("($$);}");
        fCompilerUtil.addMethod(methodCodeBuilder.toString(), pBridgeCtClass);
    }

    private void addBridgeClassMethod(CtClass pBridgeCtClass, List<CtMethod> pMethods) throws NotFoundException {
        CtMethod firstMethod = pMethods.get(0);
        String   methodName  = firstMethod.getName();
        CtClass  returnType  = firstMethod.getReturnType();
        int      numParams   = firstMethod.getParameterTypes().length;
        addBridgeMethod(pBridgeCtClass, methodName, "$$" + methodName, returnType, numParams, "this");
    }


    private List<CtConstructor> getPublicConstructors(CtClass javaCtClass) {
        List<CtConstructor> publicConstructors = new ArrayList<CtConstructor>();
        for (CtConstructor ctConstructor : javaCtClass.getConstructors()) {
            if (Modifier.isPublic(ctConstructor.getModifiers())) {
                publicConstructors.add(ctConstructor);
            }
        }
        return publicConstructors;
    }
    
    private void addBridgeNewMethods(CtClass pBridgeCtClass, List<CtConstructor> pPublicConstructors) throws NotFoundException, CannotCompileException {
        for (CtConstructor ctConstructor : pPublicConstructors) {
            addBridgeDelegatingNewMethod(pBridgeCtClass, ctConstructor);
        }
        Map<Integer, List<CtConstructor>> constructorsByNumParams = getConstructorsByNumParams(pPublicConstructors);
        for (Integer numParameters : constructorsByNumParams.keySet()) {
            addBridgeNewMethod(pBridgeCtClass, constructorsByNumParams.get(numParameters));
        }
    }
    
    private void addBridgeDelegatingNewMethod(CtClass pBridgeCtClass, CtConstructor pCtConstructor) throws CannotCompileException, NotFoundException {
        StringBuilder methodCodeBuilder = new StringBuilder();
        
        methodCodeBuilder.append("public java.lang.Object " + INTERNAL_NEW_METHOD_NAME);
        appendMethodParameters(methodCodeBuilder, pCtConstructor.getParameterTypes());
        
        methodCodeBuilder.append("{ return new ");
        methodCodeBuilder.append(pCtConstructor.getDeclaringClass().getName());
        methodCodeBuilder.append("($$);}");
        fCompilerUtil.addMethod(methodCodeBuilder.toString(), pBridgeCtClass);
    }

    private void appendMethodParameters(StringBuilder methodCodeBuilder, CtClass[] parameterTypes) {
        methodCodeBuilder.append("(");
        int i = 1;
        for (CtClass paramCtClass : parameterTypes) {
            methodCodeBuilder.append(paramCtClass.getName());
            methodCodeBuilder.append(" p");
            methodCodeBuilder.append(i);
            methodCodeBuilder.append(COMMA);
            i++;
        }
        if (i > 1) {
            methodCodeBuilder.setLength(methodCodeBuilder.length() - COMMA.length());
        }
        methodCodeBuilder.append(")");
    }
        
    private Map<Integer, List<CtConstructor>> getConstructorsByNumParams(List<CtConstructor> pPublicConstructors)
            throws NotFoundException {
        Map<Integer, List<CtConstructor>> constructorsByNumParams = new HashMap<Integer, List<CtConstructor>>();
        for (CtConstructor ctConstructor : pPublicConstructors) {
            Integer             numParameters = Integer.valueOf(ctConstructor.getParameterTypes().length);
            List<CtConstructor> methodsList   = constructorsByNumParams.get(numParameters);
            if (methodsList == null) {
                methodsList = new ArrayList<CtConstructor>();
            }
            methodsList.add(ctConstructor);
            constructorsByNumParams.put(numParameters, methodsList);
        }
        return constructorsByNumParams;
    }
    
    private void addBridgeNewMethod(CtClass pBridgeCtClass, List<CtConstructor> pConstructors) throws NotFoundException {
        CtConstructor constructor   = pConstructors.get(0);
        CtClass       returnType    = constructor.getDeclaringClass();
        int           numParams     = constructor.getParameterTypes().length;
        String        newMethodName = JavaCodingUtil.getJavaMethodNameForSelector("new");        
        addBridgeMethod(pBridgeCtClass, newMethodName, INTERNAL_NEW_METHOD_NAME, returnType, numParams, "this");
    }
    
    
    
    Class createJavaInstanceBridgeClass(Class pClass) {
        CtClass javaCtClass;
        try {
            javaCtClass = fCompilerUtil.getJavaClassNamed(pClass.getName());
        } catch (NotFoundException ex) {
            throw new RuntimeException(ex);
        }
        try {
            String  bridgeClassName       = getJavaInstanceBridgeClassname(pClass);
            CtClass bridgeCtClass         = fCompilerUtil.subClass(bridgeClassName,
                                                    JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            
            fCompilerUtil.addInstanceField("fBridge", pClass.getName(), bridgeCtClass);
            fCompilerUtil.addConstructor("public " + bridgeCtClass.getSimpleName() + "(" + pClass.getName() + " pBridge){fBridge = pBridge;}", bridgeCtClass);
            fCompilerUtil.addMethod("public java.lang.Object unbox(){return fBridge;}", bridgeCtClass);
            
            List<CtMethod> publicInstanceMethods = getPublicInstanceMethods(javaCtClass);
            addBridgeInstanceMethods(bridgeCtClass, publicInstanceMethods);
            return fCompilerUtil.loadJavaClassNamed(bridgeClassName);
        } catch (Exception ex) {
            String errorMessage = "cant create instance bridge for class "+ pClass.getName();            
            LOG.fatal(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    private List<CtMethod> getPublicInstanceMethods(CtClass javaCtClass) {
        List<CtMethod>  publicInstanceMethods = new ArrayList<CtMethod>();
        for (CtMethod ctMethod : javaCtClass.getMethods()) {
            if (isPublicInstanceMethod(ctMethod)) {
                publicInstanceMethods.add(ctMethod);
            }
        }
        return publicInstanceMethods;
    }

    
    private void addBridgeInstanceMethods(CtClass pBridgeCtClass, List<CtMethod> pMethods)
            throws NotFoundException {
        
        Map<MethodSelector, List<CtMethod>> methodsBySelector = getMethodsBySelector(pMethods);
        for (MethodSelector selector : methodsBySelector.keySet()) {
            addBridgeInstanceMethod(pBridgeCtClass, methodsBySelector.get(selector), "fBridge");
        }
    }

    private Map<MethodSelector, List<CtMethod>> getMethodsBySelector(List<CtMethod> pMethods) throws NotFoundException {
        Map<MethodSelector, List<CtMethod>> methodsBySelector = new HashMap<MethodSelector, List<CtMethod>>();
        for (CtMethod ctMethod : pMethods) {
            MethodSelector selector    = new MethodSelector(ctMethod.getName(), ctMethod.getParameterTypes().length);
            List<CtMethod> methodsList = methodsBySelector.get(selector);
            if (methodsList == null) {
                methodsList = new ArrayList<CtMethod>();
            }
            methodsList.add(ctMethod);
            methodsBySelector.put(selector, methodsList);
        }
        return methodsBySelector;
    }
    
    static final class MethodSelector {
        private String fMethodName;
        private int    fNumArgs;
        
        public MethodSelector(String pName, int pNumArgs) {
            fMethodName = pName;
            fNumArgs    = pNumArgs;
        }
        
        @Override
        public int hashCode() {
            final int prime  = 31;
            int       result = 1;
                      result = prime * result + ((fMethodName == null) ? 0 : fMethodName.hashCode());
                      result = prime * result + fNumArgs;
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final MethodSelector other = (MethodSelector) obj;
            if (fMethodName == null) {
                if (other.fMethodName != null)
                    return false;
            } else if (!fMethodName.equals(other.fMethodName))
                return false;
            if (fNumArgs != other.fNumArgs)
                return false;
            return true;
        }        
    }

    String getJavaInstanceBridgeClassname(Class pClass) {
        return JavaCodingUtil.SMALLTALK_PACKAGE_NAME + "." + pClass.getName();
    }

    private boolean isPublicInstanceMethod(CtMethod ctMethod) {
        return !Modifier.isStatic(ctMethod.getModifiers()) && Modifier.isPublic(ctMethod.getModifiers());
    }
    
    private void addBridgeInstanceMethod(CtClass pBridgeCtClass, List<CtMethod> pMethods, String pBridgeReceiver) throws NotFoundException {
        CtMethod firstMethod = pMethods.get(0);
        String   methodName  = firstMethod.getName();
        CtClass  returnType  = firstMethod.getReturnType();
        int      numParams   = firstMethod.getParameterTypes().length;
        
        addBridgeMethod(pBridgeCtClass, methodName, methodName, returnType, numParams, pBridgeReceiver);
    }
    
    
    private void addBridgeMethod(CtClass pBridgeCtClass, String pMethodName, String pCalledMethodName, CtClass pReturnType, int pNumParams,
            String pBridgeReceiver) throws NotFoundException {
        StringBuilder methodCodeBuilder = new StringBuilder();
        methodCodeBuilder.append("{");
        if (!pReturnType.equals(CtClass.voidType)) {
            methodCodeBuilder.append("return (" + JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME + ")");
            appendGetCurrentJavassistUniverse(methodCodeBuilder);
            methodCodeBuilder.append(".getJavaInstanceBridge(");
        }
        addMetaObjectProtocolCall(pBridgeReceiver, pCalledMethodName, methodCodeBuilder);
        if (!pReturnType.equals(CtClass.voidType)) {
            methodCodeBuilder.append(");");
        } else {
            methodCodeBuilder.append(";");
            methodCodeBuilder.append("return this;");
        }
        methodCodeBuilder.append("}");
        String methodCode = methodCodeBuilder.toString();
        try {
            CtMethod ctMethod = fCompilerUtil.addMethodWithBodyCode(pBridgeCtClass, pMethodName, pNumParams, methodCode);
        } catch (CannotCompileException ex) {
            throw new RuntimeException("can not compile method=" + pMethodName + ", code=" + methodCode, ex);
        }
    }

    private void addMetaObjectProtocolCall(String pBridgeReceiver, String pMethodname, StringBuilder pMethodCodeBuilder) throws NotFoundException {
        appendGetMetaObjectProtocol(pMethodCodeBuilder);
        pMethodCodeBuilder.append(".call(");
        pMethodCodeBuilder.append(pBridgeReceiver);
        pMethodCodeBuilder.append(", \"");
        pMethodCodeBuilder.append(pMethodname);
        pMethodCodeBuilder.append("\", ");
        appendGetMetaObjectProtocol(pMethodCodeBuilder);
        pMethodCodeBuilder.append(", ");
        appendGetCurrentJavassistUniverse(pMethodCodeBuilder);
        pMethodCodeBuilder.append(".unbox($args))");
    }

    private void appendGetMetaObjectProtocol(StringBuilder pMethodCodeBuilder) {
        appendGetCurrentJavassistUniverse(pMethodCodeBuilder);
        pMethodCodeBuilder.append(".getBeansMetaobjectProtocol()");
    }


    private void appendGetCurrentJavassistUniverse(StringBuilder pMethodCodeBuilder) {
        pMethodCodeBuilder.append(JavassistUniverse.class.getName());
        pMethodCodeBuilder.append(".getCurrentJavassistUniverse()");
    }
}
