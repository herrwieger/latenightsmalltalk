Object subclass: #Block
	instanceVariableNames: 'argCount'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

Block addInterface:'smalltalk.IBlock'!
Block addConstructor:'public Block(smalltalk.Object pArgCount) {argCount = pArgCount;}'!
Block addMethod:'
public smalltalk.Object $return(smalltalk.Object pResult) {
    throw new UnsupportedOperationException();
}'!
Block addMethod:'    
public smalltalk.Object $returnFromException(smalltalk.Object pResult) {
    throw new UnsupportedOperationException();
}'!

!Block methodsFor: ''!
	argCount
		^ argCount
	!
	checkArgumentCount: count
		^ (argCount = count)
			ifTrue: [ true ]
			ifFalse: [
				smalltalk error: 'wrong number of arguments passed to block'.
				false ]
	!
	repeat
		[ true ] whileTrue: [ self value ]
	!
	value
		self subclassResponsibility
	!
	value: x
		self subclassResponsibility
	!
	value: x value: y
		self subclassResponsibility
	!
	value: x value: y value: z
		self subclassResponsibility
	!
	valueWithArguments: pArgs
		self notImplementedYet
	!
	
	
	whileFalse: aBlock
		[ self value not ] whileTrue: aBlock!
	whileTrue
		self whileTrue: []!
	whileTrue: aBlock
		( self value ) ifTrue:
			[ aBlock value. 
				self whileTrue: aBlock ]!

				
	ensure: terminationBlock
		"see ANSI 5.4.3.1 Message: ensure:terminationBlock"
		<native:
			'public smalltalk.Object ensure(smalltalk.Object pTerminationBlock) {
		        try {
		            return value();
		        }
		        finally {
		            ((smalltalk.Block)pTerminationBlock).value();
		        }
		    }'
		>
	!
	ifCurtailed: terminationBlock
		"see ANSI 5.4.3.2 Message: ifCurtailed:terminationBlock"
		<native:
			'public smalltalk.Object ifCurtailed(smalltalk.Object pTerminationBlock) {
		        try {
		            return value();
		        }
		        catch (smalltalk.internal.BlockReturnException bEx) {
		            if (bEx.isExceptional()) {
		                ((smalltalk.Block)pTerminationBlock).value();
		            }
		            throw bEx;
		        }
		    }'
		>
	!
	on: selector do: action
		"see ANSI 5.4.3.3 Message: on:selector do:action"
		<native:
			'public smalltalk.Object onDo(smalltalk.Object pSelector, smalltalk.Object pAction) {        
		        smalltalk.internal.ExceptionEnvironment.pushHandler(this, pSelector, (smalltalk.Block)pAction);
		        try {
		            return value();
		        }
		        finally {
		            smalltalk.internal.ExceptionEnvironment.popHandler();
		        }
		    }'
		>
	!
!
