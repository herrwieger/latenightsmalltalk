Boolean subclass: #False
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

False addMethod:'public boolean booleanValue() {return false;}'!
False addMethod:'public java.lang.Object unbox() {return java.lang.Boolean.FALSE;}'!

!False methodsFor: ''!

	and: aBlock
		^ false!
	ifTrue: trueBlock ifFalse: falseBlock
		^ falseBlock value!
	not
		^ true!
	or: aBlock
		^ aBlock value!
	printString
		^ 'false'!
	xor: aBoolean
		^ aBoolean!
!


