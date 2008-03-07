group javasource;


dynamicInvocationBaseclass(pPackageName, pSimpleClassname, pDirectMethods, pSmalltalkObjectClassQName, pUniverseClassName) ::= <<
package <pPackageName>;

public abstract class <pSimpleClassname> {
	<pDirectMethods : {<it.methodDeclaration> {
		return doesNotUnderstand((<pSmalltalkObjectClassQName>)<pUniverseClassName>.getCurrentUniverse().newString("<it.selector>"));
	}
	}>
	
	public abstract <pSmalltalkObjectClassQName> doesNotUnderstand(<pSmalltalkObjectClassQName> pSelector);
}
>>

javaClass(
	pClassDescription,
	pPackageName, 
	pSimpleClassname, 
	pClassQName, 
	pSuperClassQName, 
	pClassClassQName,
	pInstanceClassQName, 
	pGetClazzMethodName, 
	pClassMethodName, 
	pSmalltalkObjectClassQName,
	pUniverseClassName
) ::= <<
package <pPackageName>;

public class <pSimpleClassname> extends <pSuperClassQName> <optionalImplements(pClassDescription)> {
	<classClassFieldCode(pClassClassQName=pClassClassQName)>
	
	<staticClassClassAccessorInitializerMethodCode(pGetClazzMethodName=pGetClazzMethodName, pClassDescription=pClassDescription, pClassClassQName=pClassClassQName, pSuperClassQName=pSuperClassQName, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName, pUniverseClassName=pUniverseClassName)>
	
	<classClassAccessorMethodCode(pClassMethodName=pClassMethodName, pGetClazzMethodName=pGetClazzMethodName, pClassQName=pClassQName, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
	
	
	<pClassDescription.instanceVariables : {<pSmalltalkObjectClassQName> <it.name>;<\n>} >
	<pClassDescription.nativeFields>


	public <pSimpleClassname>(){super();}

	<pClassDescription.nativeConstructors>
	
    <if(pClassDescription.variable)>
        <pSmalltalkObjectClassQName>[] $array;
        
        <variableClassConstructorCode(pSimpleClassname=pSimpleClassname, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
        
        public <pSmalltalkObjectClassQName> basicSize(){return smalltalk.Integer.valueOf($array.length);}
        
        public <pSmalltalkObjectClassQName> basicAtPut(<pSmalltalkObjectClassQName> pIndex, <pSmalltalkObjectClassQName> pValue){
        	$array[((smalltalk.Integer)pIndex).intValue()-1]=pValue;
           	return pValue;
        }
        
        public <pSmalltalkObjectClassQName> basicAt(<pSmalltalkObjectClassQName> pIndex) {
        	return $array[((smalltalk.Integer)pIndex).intValue()-1];
       	}
    <endif>
    <if(pClassDescription.variableSubclass)>
        <variableSubclassConstructorCode(pSimpleClassname=pSimpleClassname, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
    <endif>
    <if(pClassDescription.constructorClass)>
        <if(pClassDescription.variableConstructorClass)>
        	<basicNewSizeMethodCode(pInstanceClassQName=pInstanceClassQName, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
		<else>
			<basicNewMethodCode(pInstanceClassQName=pInstanceClassQName, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
		<endif>            
    <endif>

	<pClassDescription.numberLiterals : numberLiteralDeclaration(pNumberLiteral=it, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
	<pClassDescription.nativeMethods>
	<pClassDescription.methodsToCode: method(pMethodDescription=it, pSmalltalkObjectClassQName=pSmalltalkObjectClassQName)>
	<pClassDescription.superWrapperMethods: {<it.codeWithSimpleNames><\n>}>
}
>>

optionalImplements(pClassDescription) ::= <<
<if(pClassDescription.interfaces.empty)>
<else>
implements <pClassDescription.interfaces; separator="," : {<it>}>
<endif>
>>

 

classClassFieldCode(pClassClassQName) ::= <<
private static <pClassClassQName> sfClass;
>>



staticClassClassAccessorInitializerMethodCode(pGetClazzMethodName, pClassDescription, pClassClassQName, pSuperClassQName, pSmalltalkObjectClassQName, pUniverseClassName) ::= <<
public static <pSmalltalkObjectClassQName> <pGetClazzMethodName>() {
	if (sfClass == null) {
		sfClass = new <pClassClassQName>();
        <pSmalltalkObjectClassQName> className = (<pSmalltalkObjectClassQName>)<pUniverseClassName>.getCurrentUniverse().newString("<pClassDescription.clazz.name>");
        sfClass.name(className);

        smalltalk.Collection selectors = (smalltalk.Collection)<pUniverseClassName>.getCurrentUniverse().newOrderedCollection();
        <pClassDescription.methodDescriptions : addSelector(pSelector = it.selector, pSmalltalkObjectClassQName = pSmalltalkObjectClassQName)>
        sfClass.selectors((<pSmalltalkObjectClassQName>)selectors);
        
        <if(pClassDescription.superClass)>
        sfClass.superclass(<pSuperClassQName>.getClazz());
    	<endif>
	}
	return sfClass;
}
>>


addSelector(pSelector, pSmalltalkObjectClassQName) ::= <<
selectors.add((<pSmalltalkObjectClassQName>)<pUniverseClassName>.getCurrentUniverse().newSymbol("<pSelector>"));

>>



classClassAccessorMethodCode(pClassMethodName, pGetClazzMethodName, pClassQName, pSmalltalkObjectClassQName) ::= <<
public <pSmalltalkObjectClassQName> <pClassMethodName>() {
	return <pClassQName>.<pGetClazzMethodName>();
}
>>


    
variableClassConstructorCode(pSimpleClassname, pSmalltalkObjectClassQName) ::= <<
public <pSimpleClassname>(<pSmalltalkObjectClassQName> pSize){
	$array = new <pSmalltalkObjectClassQName>[((smalltalk.Integer)pSize).intValue()];
}
>>


variableSubclassConstructorCode(pSimpleClassname, pSmalltalkObjectClassQName) ::= <<
public <pSimpleClassname>(<pSmalltalkObjectClassQName> pSize) {
	super(pSize);
}
>>


basicNewMethodCode(pInstanceClassQName, pSmalltalkObjectClassQName) ::= <<
public <pSmalltalkObjectClassQName> basicNew() {
	return new <pInstanceClassQName>();
}
>>


basicNewSizeMethodCode(pInstanceClassQName, pSmalltalkObjectClassQName) ::= <<
public <pSmalltalkObjectClassQName> basicNew(<pSmalltalkObjectClassQName> pSize) {
	return new <pInstanceClassQName>(pSize);
}
>>


numberLiteralDeclaration(pNumberLiteral, pSmalltalkObjectClassQName) ::= <<
	public static final <pSmalltalkObjectClassQName> <pNumberLiteral.value> = <pNumberLiteral.declarationCode>;
>>

method(pMethodDescription, pSmalltalkObjectClassQName) ::= <<
<if(pMethodDescription.notNative)>
	<if(pMethodDescription.withContext)>
		private static class <pMethodDescription.methodContextSimpleName> {
			<pMethodDescription.methodContextVariableNames : {private <pSmalltalkObjectClassQName> <it>;<\n>}>
		}
	<endif>
    <if(pMethodDescription.withBlocks)>
        private static class <pMethodDescription.homeActivationExceptionSimpleName> extends smalltalk.internal.BlockReturnException {
	    	private <pMethodDescription.homeActivationExceptionSimpleName>(<pSmalltalkObjectClassQName> pResult) {
	    		super(pResult);
	    	}
	    	private <pMethodDescription.homeActivationExceptionSimpleName>(<pSmalltalkObjectClassQName> pResult, boolean pIsExceptional) {
	    		super(pResult, pIsExceptional);
	    	}
	    }
		<pMethodDescription.blockDescriptions : block(it)>
    <endif>
<endif>
<pMethodDescription.codeWithSimpleNames>

>>


block(pBlockDescription) ::= <<
public static class <pBlockDescription.simpleName> extends smalltalk.Block {
	<pBlockDescription.instanceVariablesCode>
	<pBlockDescription.constructorCode>
	
	<pBlockDescription.returnMethodCodeWithSimpleNames>
	<pBlockDescription.returnFromExceptionMethodCodeWithSimpleNames>
	<pBlockDescription.codeWithSimpleNames>
}
>>


