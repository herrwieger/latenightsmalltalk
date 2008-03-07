Behavior subclass: #Metaclass
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

!Metaclass methodsFor: ''!

	instName: nSym instanceSize: iInt methods: mDict superClass: sClass variables: vArray classVariables: cvArray poolDictionaries: pdArray category: aCategory
		^ self basicNew
			name: nSym
			instanceSize: iInt
			methods: mDict
			superClass: sClass
			variables: vArray
			classVariables: cvArray
			poolDictionaries: pdArray
			category: aCategory.
	!
	methodsFor: aCategory
		Transcript show: ('in `' + (self name) + '` methodsFor');cr.
		^ self
	!
	subClasses
		^ classes inject: List new
			into: [:x :y | (y class superClass == self)
						ifTrue: [ x add: y class ]. x ]!
!
