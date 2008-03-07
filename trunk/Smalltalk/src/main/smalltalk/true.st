Boolean subclass: #True
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

True addMethod:'public boolean booleanValue() {return true;}'!
True addMethod:'public java.lang.Object unbox() {return java.lang.Boolean.TRUE;}'!

!True methodsFor: ''!

	ifTrue: trueBlock ifFalse: falseBlock
		^ trueBlock value!
	not
		^ false!
	printString
		^ 'true'!
	xor: aBoolean
		^ aBoolean not!
!


