package de.wieger.smalltalk.universe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.log4j.Logger;

import de.wieger.smalltalk.smile.BlockDescription;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.DynamicMethodInvocation;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.smile.Statement;


public class JavasourceUniverse extends AbstractUniverse {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    private static final String DYNAMIC_INVOCATION_BASECLASS = "DynamicInvocationBaseclass";

    private static final String SOURCE_PATH = "C:/Work/Home/SmalltalkTransformations/src/main/java-gen/";

    
    
    //--------------------------------------------------------------------------  
    // class variables
    //--------------------------------------------------------------------------

    private static final Logger LOG = Logger.getLogger(JavasourceUniverse.class);



    //--------------------------------------------------------------------------
    // AbstractUniverse methods (implementation)
    //--------------------------------------------------------------------------

    @Override
    public void compileClasses(List<ClassDescription> pClassDescriptionsToCompile) {
        createDynamicInvocationBaseclass(pClassDescriptionsToCompile);
        for (ClassDescription classDescription : pClassDescriptionsToCompile) {
            createJavaClass(classDescription);
        }
    }

    private void createDynamicInvocationBaseclass(List<ClassDescription> pClassDescriptionsToCompile) {
        Set<DirectMethod>  directMethods  = new HashSet<DirectMethod>();
        
        for (ClassDescription classDescription : pClassDescriptionsToCompile) {
            for (MethodDescription methodDescription : classDescription.getMethodDescriptions()) {
                findInvocations(directMethods, methodDescription.getStatements());
                for (BlockDescription blockDescription : methodDescription.getBlockDescriptions()) {
                    findInvocations(directMethods, blockDescription.getStatements());
                }
            }
        }
        LOG.debug("directMethods=" + directMethods);
        PrintWriter printWriter = getJavaFilePrintWriter(DYNAMIC_INVOCATION_BASECLASS);
        try {
            StringTemplate stringTemplate = JavaCodingUtil.getTemplate("dynamicInvocationBaseclass");
            stringTemplate.setAttribute("pPackageName",                 JavaCodingUtil.SMALLTALK_PACKAGE_NAME);
            stringTemplate.setAttribute("pSimpleClassname",             JavaCodingUtil.getSimpleClassname(DYNAMIC_INVOCATION_BASECLASS));
            stringTemplate.setAttribute("pDirectMethods",               directMethods);
            stringTemplate.setAttribute("pSmalltalkObjectClassQName",   JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            stringTemplate.setAttribute("pUniverseClassName",           AbstractUniverse.class.getName());
            printWriter.write(stringTemplate.toString());
        } finally {
            printWriter.close();
        }
    }

    private void findInvocations(Set<DirectMethod> directMethods, List<Statement> pStatements) {
        for (Statement statement : pStatements) {
            if (!(statement instanceof DynamicMethodInvocation)) {
                continue;
            }
            DynamicMethodInvocation invocation = (DynamicMethodInvocation)statement;
            DirectMethod            directMethod = new DirectMethod(invocation.getSelector(), invocation.getNumParams());
            if(directMethods.contains(directMethod)) {
                continue;
            }
            directMethods.add(directMethod);
        }
    }

    private void createJavaClass(ClassDescription pClassDescription) {
        PrintWriter printWriter = getJavaFilePrintWriter(pClassDescription.getName());
        try {
            StringTemplate stringTemplate = JavaCodingUtil.getTemplate("javaClass");
            stringTemplate.setAttribute("pClassDescription",            pClassDescription);
            stringTemplate.setAttribute("pPackageName",                 JavaCodingUtil.getPackagename(pClassDescription));
            stringTemplate.setAttribute("pSimpleClassname",             JavaCodingUtil.getSimpleClassname(pClassDescription));
            stringTemplate.setAttribute("pClassQName",                  JavaCodingUtil.getQualifiedClassname(pClassDescription));
            
            stringTemplate.setAttribute("pSuperClassQName",
                    getQualifiedSuperClassQName(pClassDescription));
            
            stringTemplate.setAttribute("pClassClassQName",
                    JavaCodingUtil.getQualifiedClassname(pClassDescription.getClazz()));
            if (pClassDescription.getInstanceClass()!=null) {
                stringTemplate.setAttribute("pInstanceClassQName",      JavaCodingUtil.getQualifiedClassname(pClassDescription.getInstanceClass()));
            }
            stringTemplate.setAttribute("pGetClazzMethodName",          JavaCodingUtil.GET_CLAZZ_METHOD_NAME);
            stringTemplate.setAttribute("pClassMethodName",             JavaCodingUtil.getJavaMethodNameForSelector("class"));
            stringTemplate.setAttribute("pSmalltalkObjectClassQName",   JavaCodingUtil.SMALLTALK_OBJECT_CLASS_QNAME);
            stringTemplate.setAttribute("pUniverseClassName",           AbstractUniverse.class.getName());

            printWriter.write(stringTemplate.toString());
        } finally {
            printWriter.close();
        }
    }


    private String getQualifiedSuperClassQName(ClassDescription pClassDescription) {
        ClassDescription superClass = pClassDescription.getSuperClass();
        if(superClass==null) {
            return  JavaCodingUtil.getQualifiedClassname(DYNAMIC_INVOCATION_BASECLASS);
        }
        return JavaCodingUtil.getQualifiedClassname(superClass);
    }
    

    private PrintWriter getJavaFilePrintWriter(String pSmalltalkClassname) {
        String classname    = JavaCodingUtil.getQualifiedClassname(pSmalltalkClassname);
        String filePath     = SOURCE_PATH + classname.replace('.', '/') + ".java";
        String pathToFile   = filePath.substring(0, filePath.lastIndexOf("/"));
        new File(pathToFile).mkdirs();
        try {
            return new PrintWriter(new FileWriter(filePath));
        } catch (IOException ex) {
            throw new RuntimeException("error while creating class " + pSmalltalkClassname, ex);
        }
    }



    //--------------------------------------------------------------------------
    // runtime methods
    //--------------------------------------------------------------------------


    public Class getClassNamed(String pSmalltalkClassName) {
        String classname    = JavaCodingUtil.getQualifiedClassname(pSmalltalkClassName);
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("class not found", ex);
        }
    }

    protected Object getJavaClassBridge(String pIdentifierAsJavaString) {
        throw new UnsupportedOperationException("currently not implemented");
    }
}
