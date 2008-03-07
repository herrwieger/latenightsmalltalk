Object subclass: #Boolean
   instanceVariableNames: ''
   classVariableNames: ''
   poolDictionaries: ''
   category: ''
"
ANSI-methods:
	&
	|
	and:
	eqv:
	ifFalse:
	ifFalse:ifTrue:
	ifTrue:
	ifTrue:ifFalse:
	not
	or:
	printString
	xor:
"
!
Boolean addMethod:'public boolean booleanValue() {throw new UnsupportedOperationException();}'!

!Boolean methodsFor: ''!
	& aBoolean 
		"Evaluating conjunction. Evaluate the argument. Then answer true if 
		both the receiver and the argument are true."
	
		^ self ifTrue:[aBoolean] ifFalse:[false]
	!
	| aBoolean 
		"Evaluating disjunction (OR). Evaluate the argument. Then answer true 
		if either the receiver or the argument is true."
	
		^ self ifTrue:[true] ifFalse:[aBoolean]
	!
   and: aBlock
      ^ self ifTrue: aBlock ifFalse: [ false ]!
   ifFalse: falseBlock
      ^ self ifTrue: [] ifFalse: falseBlock!
   ifFalse: falseBlock ifTrue: trueBlock
      ^ self ifTrue: trueBlock
         ifFalse: falseBlock!
   ifTrue: trueBlock
      ^ self ifTrue: trueBlock ifFalse: []
   !
   ifTrue: trueBlock ifFalse:falseBlock
      self subclassResponsibility
   !
   not
      ^ self
         ifTrue: [ false]
         ifFalse: [ true ]!
   or: aBlock
      ^ self ifTrue: [ true ] ifFalse: aBlock!
! 



