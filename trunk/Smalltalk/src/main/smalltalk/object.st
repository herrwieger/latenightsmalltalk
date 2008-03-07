Object addMethod:'
private smalltalk.Object $perform(smalltalk.Object pSymbol, smalltalk.Object[] pArguments) {
	smalltalk.Symbol  methodName  = (smalltalk.Symbol)pSymbol;
	return (smalltalk.Object)smalltalk.shared.MethodInvoker.invoke(this, methodName.toString(), pArguments);
}'!

!Object methodsFor: ''
"
ANSI-Methods:
	==
	~=
	~~
	class
	copy
	doesNotUnderstand:
	error:
	hash
	identityHash
	isKindOf:
	isMemberOf:
	isNil
	notNil
	perform:
	perform:with:
	perform:with:with:
	perform:with:with:with:
	perform:withArguments:
	printOn:
	printString
	respondsTo:
	yourself

Additional methods:
	basicAt:
	basicAt:put:
	basicSize
"!
	== anObject 
		"Primitive. Answer whether the receiver and the argument are the same 
		object (have the same object pointer). Do not redefine the message == in 
		any other class. Essential. No Lookup. Do not override in any subclass. 
		See Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object $same(smalltalk.Object pAnotherObject) {
        		return (smalltalk.Object)de.wieger.smalltalk.universe.AbstractUniverse.getBoolean(this == pAnotherObject);
    		}'
		>
		self primitiveFailed
	!
	= anObject 
		"Answer whether the receiver and the argument represent the same 
		object. If = is redefined in any subclass, consider also redefining the 
		message hash."
	
		^self == anObject
	!
	~~ aValue
		^ (self == aValue) not!
	~= aValue
		^ self ~~ aValue
	!
	basicAt: index 
		"Primitive. Assumes receiver is indexable. Answer the value of an 
		indexable element in the receiver. Fail if the argument index is not an 
		Integer or is out of bounds. Essential. Do not override in a subclass. See 
		Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object basicAt(smalltalk.Object pIndex) {
        		throw new UnsupportedOperationException();
    		}'
		>
		index isInteger ifTrue: [self errorSubscriptBounds: index].
		index isNumber
			ifTrue: [^self basicAt: index asInteger]
			ifFalse: [self errorNonIntegerIndex]
	!
	basicAt: index put: value 
		"Primitive. Assumes receiver is indexable. Store the second argument 
		value in the indexable element of the receiver indicated by index. Fail 
		if the index is not an Integer or is out of bounds. Or fail if the value is 
		not of the right type for this kind of collection. Answer the value that 
		was stored. Essential. Do not override in a subclass. See Object 
		documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object basicAtPut(smalltalk.Object pIndex, smalltalk.Object pValue) {
				throw new UnsupportedOperationException();
			}'
		>
		index isInteger
			ifTrue: [(index >= 1 and: [index <= self size])
						ifTrue: [self errorImproperStore]
						ifFalse: [self errorSubscriptBounds: index]].
		index isNumber
			ifTrue: [^self basicAt: index asInteger put: value]
			ifFalse: [self errorNonIntegerIndex]
	!
	basicSize
		"Primitive. Answer the number of indexable variables in the receiver. 
		This value is the same as the largest legal subscript. Essential. Do not 
		override in any subclass. See Object documentation whatIsAPrimitive."
	
		"The number of indexable fields of fixed-length objects is 0"
		^0
	!
	
	class
		"Primitive. Answer the object which is the receiver's class. Essential. See 
		Object documentation whatIsAPrimitive."
	
		<native>
		self primitiveFailed
	!	
	copy
		^ self shallowCopy!
	doesNotUnderstand: aMessage 
		 "Handle the fact that there was an attempt to send the given message to the receiver but the receiver does not understand this message (typically sent from the machine when a message is sent to the receiver and no method is defined for that selector)."
		"Testing: (3 activeProcess)"
	
		MessageNotUnderstood new 
			message: aMessage;
			receiver: self;
			signal.
		^ aMessage sentTo: self.
	!
	error: aString 
		"Throw a generic Error exception."
	
		^Error new signal: aString
	!
	errorNotIndexable
		"Create an error notification that the receiver is not indexable."
		self error:'Instances of ', self class name, ' not indexable'
	!
	hash
		"Answer a SmallInteger whose value is related to the receiver's identity.
		May be overridden, and should be overridden in any classes that define = "
	
		^ self identityHash
	!
	identityHash
		"Answer a SmallInteger whose value is related to the receiver's identity.
		This method must not be overridden, except by SmallInteger.
		Primitive. Fails if the receiver is a SmallInteger. Essential.
		See Object documentation whatIsAPrimitive.
	
		Do not override."
	
		<native:
			'public smalltalk.Object identityHash() {
        		return smalltalk.Integer.valueOf(hashCode());
    		}'
		>
		self primitiveFailed
	!

	isKindOf: aClass
		self class upSuperclassChain:
			[:x | (x == aClass) ifTrue: [ ^ true ] ].
		^ false
	!
	isMemberOf: aClass
		^ self class == aClass!
	isNil
		^ false!
	notNil
		^ true!
	perform: aSymbol 
		"Send the unary selector, aSymbol, to the receiver.
		Fail if the number of arguments expected by the selector is not zero.
		Primitive. Optional. See Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object perform(smalltalk.Object pSymbol) {
				smalltalk.Object[] args = new smalltalk.Object[0];
        		return $perform(pSymbol, args);
    		}'
		>
		^ self perform: aSymbol withArguments: (Array new: 0)
	!
	perform: aSymbol with: anObject 
		"Send the selector, aSymbol, to the receiver with anObject as its argument.
		Fail if the number of arguments expected by the selector is not one.
		Primitive. Optional. See Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object performWith(smalltalk.Object pSymbol, smalltalk.Object pArg0) {
				smalltalk.Object[] args = new smalltalk.Object[1];
				args[0] = pArg0;
        		return $perform(pSymbol, args);
    		}'
		>
		^ self perform: aSymbol withArguments: (Array with: anObject)
	!
	perform: aSymbol with: firstObject with: secondObject 
		"Send the selector, aSymbol, to the receiver with the given arguments.
		Fail if the number of arguments expected by the selector is not two.
		Primitive. Optional. See Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object performWithWith(smalltalk.Object pSymbol, smalltalk.Object pArg0, smalltalk.Object pArg1) {
				smalltalk.Object[] args = new smalltalk.Object[2];
				args[0] = pArg0;
				args[1] = pArg1;
        		return $perform(pSymbol, args);
    		}'
		>
		^ self perform: aSymbol withArguments: (Array with: firstObject with: secondObject)
	!
	perform: aSymbol with: firstObject with: secondObject with: thirdObject 
		"Send the selector, aSymbol, to the receiver with the given arguments.
		Fail if the number of arguments expected by the selector is not three.
		Primitive. Optional. See Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object performWithWithWith(smalltalk.Object pSymbol, smalltalk.Object pArg0, smalltalk.Object pArg1, smalltalk.Object pArg2) {
				smalltalk.Object[] args = new smalltalk.Object[3];
				args[0] = pArg0;
				args[1] = pArg1;
				args[2] = pArg2;
        		return $perform(pSymbol, args);
    		}'
		>
		^ self perform: aSymbol
			withArguments: (Array with: firstObject with: secondObject with: thirdObject)
	!
	perform: selector withArguments: argArray 
		"Send the selector, aSymbol, to the receiver with arguments in argArray.
		Fail if the number of arguments expected by the selector 
		does not match the size of argArray.
		Primitive. Optional. See Object documentation whatIsAPrimitive."
	
		<native:
			'public smalltalk.Object performWithArguments(smalltalk.Object pSymbol, smalltalk.Object pArgs) {
        		throw new UnsupportedOperationException();//TODO
    		}'
		>
		^ self perform: selector withArguments: argArray inSuperclass: self class
	!
	printOn: aStream
		"Append to the argument, aStream, a sequence of characters that  
		identifies the receiver."
	
		| title |
		title := self class name.
		aStream
			nextPutAll: (title first isVowel ifTrue: ['an '] ifFalse: ['a ']);
			nextPutAll: title
	!
	printString
		"Answer a String whose characters are a description of the receiver."
	
		^ String streamContents: [:s | self printOn: s]
	!
	respondsTo: message
		self class upSuperclassChain: 
			[:c | (c methodNamed: message) notNil
					ifTrue: [ ^ true ]].
		^ false
	!
	shallowCopy	| newObj |
		newObj := self class new.
		(1 to: self basicSize) do: 
			[:i | newObj basicAt: i put: (self basicAt: i) ].
		^ newObj
	!
	subclassResponsibility
		<native:
			'public smalltalk.Object subclassResponsibility() {
        		throw new UnsupportedOperationException();
    		}'
		>
	!
	yourself
		^ self!
		
		
	addDependent: anObject
		^ anObject
	!
	changed
		self changed:self
	!
	changed: anObject
		^ anObject
	!
	removeDependent: anObject
		"Remove the given object as one of the receiver's dependents."
	
		"todo | dependents |
		dependents := self dependents reject: [:each | each == anObject].
		self myDependents: (dependents isEmpty ifFalse: [dependents])."
		^ anObject
	!
	
	
	value
		^self
	!
	initialize
		"Subclasses should redefine this method to perform initializations on instance creation"
	!
	
	notImplementedYet
		<native: 'public smalltalk.Object notImplementedYet() {
	        		throw new UnsupportedOperationException("not implemented yet");
	    		 }'
	    >
	!
!
