List subclass: #Set
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Set methodsFor: ''!

	add: value
		(self includes: value)
			ifFalse: [ self addFirst: value ]!
!


