String subclass: #Symbol
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

Symbol addConstructor:'public Symbol(java.lang.String pSymbol) {super(pSymbol);}'!

!Symbol methodsFor: ''!

 	apply: args
		^ self apply: args ifError: [ 'does not apply' ]
	!
 	apply: args ifError: aBlock
		^ smalltalk perform: self withArguments: args ifError: aBlock
	!
	asString
		^ self
	!
	asSymbol
		^ self
	!
	assign: value
		self notImplementedYet
	!
	copy
		^ self!
	printString
		^ '#' , self asString!
	respondsTo
		^ classes inject: Set new
			into: [:x :y | ((y methodNamed: self) notNil)
						ifTrue: [ x add: y]. x]!
	stringHash
		self notImplementedYet
	!
	value
		self notImplementedYet
	!
!


