package de.wieger.smalltalk.universe;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.NumberLiteral;


public class JavaCodingUtil {
    //--------------------------------------------------------------------------
    // constants
    //--------------------------------------------------------------------------

    public static Map<String, String> JAVA_METHOD_NAME_FOR_SELECTOR = new HashMap<String, String>();
    static {
        // binary selectors
        JAVA_METHOD_NAME_FOR_SELECTOR.put("+",      "$add");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("-",      "$subtract");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("*",      "$multiply");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("/",      "$divide");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("//",     "$integerDivide");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("\\\\",   "$integerRemainder");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("**",     "$pow");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("%",      "$percent");

        JAVA_METHOD_NAME_FOR_SELECTOR.put("=",      "$equals");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("~=",     "$notEquals");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("==",     "$same");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("~~",     "$notSame");
        JAVA_METHOD_NAME_FOR_SELECTOR.put(">",      "$greaterThan");
        JAVA_METHOD_NAME_FOR_SELECTOR.put(">=",     "$greaterThanOrEquals");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("<",      "$lessThan");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("<=",     "$lessThanOrEquals");

        JAVA_METHOD_NAME_FOR_SELECTOR.put("&",      "$binaryAnd");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("|",      "$binaryOr");

        JAVA_METHOD_NAME_FOR_SELECTOR.put(",",      "$append");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("@",      "$createPoint");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("->",     "$createAssociation");

        // unary selectors - reserved java keywords
        JAVA_METHOD_NAME_FOR_SELECTOR.put("class",  "$class");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("new",    "$new");

        JAVA_METHOD_NAME_FOR_SELECTOR.put("while",  "$while");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("do",     "$do");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("for",    "$for");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("if",     "$if");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("else",   "$else");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("switch", "$switch");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("case",   "$case");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("return", "$return");
        JAVA_METHOD_NAME_FOR_SELECTOR.put("assert", "$assert");

        // special java method names
        JAVA_METHOD_NAME_FOR_SELECTOR.put("wait",    "$wait");
    }


    public static final String SMALLTALK_PACKAGE_NAME   = "smalltalk";
    public static final String SMALLTALK_OBJECT_CLASS_QNAME   = getQualifiedClassname("Object");

    static final String GET_CLAZZ_METHOD_NAME          = "getClazz";
    static final String ARRAY_FIELD_NAME               = "$array";
    static final String BASIC_SIZE_METHOD_CODE         =
        "public " + SMALLTALK_OBJECT_CLASS_QNAME + " basicSize(){" + "return smalltalk.Integer.valueOf($array.length);" + "}";
    static final String BASIC_AT_PUT_METHOD_CODE       =
        "public " + SMALLTALK_OBJECT_CLASS_QNAME + " basicAtPut("
            + SMALLTALK_OBJECT_CLASS_QNAME + " pIndex, " + SMALLTALK_OBJECT_CLASS_QNAME + " pValue){"
            + "$array[((smalltalk.Integer)pIndex).intValue()-1]=pValue;"
            + "return pValue;" + "}";
    static final String BASIC_AT_METHOD_CODE           =
        "public " + SMALLTALK_OBJECT_CLASS_QNAME + " basicAt("
            + SMALLTALK_OBJECT_CLASS_QNAME + " pIndex){" + "return "
            + "$array[((smalltalk.Integer)pIndex).intValue()-1];" + "}";

    static final String BLOCK_RETURN_FROM_EXCEPTION_METHOD_NAME = "$returnFromException";
    static final String BLOCK_RETURN_METHOD_NAME                = "$return";




    //--------------------------------------------------------------------------
    // class methods
    //--------------------------------------------------------------------------

    public static String getJavaMethodNameForSelector(String pSelector) {
        String methodName = JAVA_METHOD_NAME_FOR_SELECTOR.get(pSelector);
        if (methodName!=null) {
            return methodName;
        }
        return pSelector;
    }


    public static String getPackagename(ClassDescription pClassDescription) {
        return SMALLTALK_PACKAGE_NAME;
    }

    public static String getQualifiedClassname(ClassDescription pClassDescription) {
        return getQualifiedClassname(pClassDescription.getName());
    }

    public static String getQualifiedClassname(String pSmalltalkClassname) {
        return SMALLTALK_PACKAGE_NAME + "." + getSimpleClassname(pSmalltalkClassname);
    }

    public static String getSimpleClassname(ClassDescription pClassDescription) {
        return getSimpleClassname(pClassDescription.getName());
    }

    public static String getSimpleClassname(String pSmalltalkClassname) {
        return pSmalltalkClassname.replace(' ', '$');
    }

    static String getQualifiedSuperClassname(ClassDescription pClassDescription) {
        ClassDescription superClass = pClassDescription.getSuperClass();
        if (superClass==null) {
            return "java.lang.Object";
        }
        return getQualifiedClassname(superClass);
    }


    static String getClassClassFieldCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("classClassFieldCode");

        stringTemplate.setAttribute("pClassClassQName", getQualifiedClassname(pClassDescription.getClazz()));

        return stringTemplate.toString();
    }

    public static String getStaticClassClassAccessorInitializerMethodCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("staticClassClassAccessorInitializerMethodCode");

        stringTemplate.setAttribute("pGetClazzMethodName",  GET_CLAZZ_METHOD_NAME);
        stringTemplate.setAttribute("pClassDescription",    pClassDescription);
        stringTemplate.setAttribute("pClassClassQName",     getQualifiedClassname(pClassDescription.getClazz()));
        ClassDescription superclass = pClassDescription.getSuperClass();
        if (superclass != null) {
            stringTemplate.setAttribute("pSuperClassQName", getQualifiedClassname(superclass));
        }
        stringTemplate.setAttribute("pSmalltalkObjectClassQName",   SMALLTALK_OBJECT_CLASS_QNAME);
        stringTemplate.setAttribute("pUniverseClassName",   AbstractUniverse.class.getName());

        return stringTemplate.toString();
    }

    public static String getClassClassAccessorMethodCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("classClassAccessorMethodCode");

        stringTemplate.setAttribute("pClassMethodName",     getJavaMethodNameForSelector("class"));
        stringTemplate.setAttribute("pGetClazzMethodName",  GET_CLAZZ_METHOD_NAME);
        stringTemplate.setAttribute("pClassQName",          getQualifiedClassname(pClassDescription));
        stringTemplate.setAttribute("pSmalltalkObjectClassQName",   SMALLTALK_OBJECT_CLASS_QNAME);

        return stringTemplate.toString();
    }

    static String getVariableClassConstructorCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("variableClassConstructorCode");

        stringTemplate.setAttribute("pSimpleClassname", getSimpleClassname(pClassDescription));
        stringTemplate.setAttribute("pSmalltalkObjectClassQName",   SMALLTALK_OBJECT_CLASS_QNAME);

        return stringTemplate.toString();
    }

    static String getVariableSubclassConstructorCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("variableSubclassConstructorCode");

        stringTemplate.setAttribute("pSimpleClassname", getSimpleClassname(pClassDescription));
        stringTemplate.setAttribute("pSmalltalkObjectClassQName",   SMALLTALK_OBJECT_CLASS_QNAME);

        return stringTemplate.toString();
    }

    static String getBasicNewMethodCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("basicNewMethodCode");

        stringTemplate.setAttribute("pInstanceClassQName", getQualifiedClassname(pClassDescription.getInstanceClass()));
        stringTemplate.setAttribute("pSmalltalkObjectClassQName",   SMALLTALK_OBJECT_CLASS_QNAME);

        return stringTemplate.toString();
    }

    static String getBasicNewSizeMethodCode(ClassDescription pClassDescription) {
        StringTemplate stringTemplate = getTemplate("basicNewSizeMethodCode");

        stringTemplate.setAttribute("pInstanceClassQName", getQualifiedClassname(pClassDescription.getInstanceClass()));
        stringTemplate.setAttribute("pSmalltalkObjectClassQName",   SMALLTALK_OBJECT_CLASS_QNAME);

        return stringTemplate.toString();
    }


    public static String getNumberLiteralDeclaration(NumberLiteral pNumberLiteral) {
        StringTemplate stringTemplate = getTemplate("numberLiteralDeclaration");

        stringTemplate.setAttribute("pNumberLiteral", pNumberLiteral);
        stringTemplate.setAttribute("pSmalltalkObjectClassQName", SMALLTALK_OBJECT_CLASS_QNAME);

        return stringTemplate.toString();
    }
    
    
    public static StringTemplate getTemplate(String pTemplateName) {
        StringTemplateGroup group           = new StringTemplateGroup(new InputStreamReader(JavaCodingUtil.class.getResourceAsStream("/stringtemplates/javasource.st")));
        StringTemplate      stringTemplate  = group.getInstanceOf(pTemplateName);
        return stringTemplate;
    }
}
