Magnitude subclass: #Number
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: 'Number'
"
ANSI-Methods:
	*
	+
	- operand
	//
	<
	=
	>
	\\
	abs
	asFloat
	asFloatD
	asFloatE
	asFloatQ
	asFraction
	asInteger
	asScaledDecimal:
	ceiling
	floor
	fractionPart
	integerPart
	negated
	negative
	positive
	printString
	quo:
	raisedTo:
	raisedToInteger:
	reciprocal
	rem:
	rounded
	roundTo:
	sign
	sqrt
	squared
	strictlyPositive
	to:
	to:by:
	to:by:do:
	to:do:
	truncated
	truncateTo:
"
!


!Number methodsFor: ''!

	* value
		^ (self maxgen: value) * (value maxgen: self)!
	+ value
		^ (self maxgen: value) + (value maxgen: self)!
	- value
		^ (self maxgen: value) - (value maxgen: self)!
	/ value
		^ (self maxgen: value) / (value maxgen: self)!
	// value
		" integer division, truncate towards negative infinity"
		" see quo: "
		^ (self / value) floor!
	< value
		^ (self maxgen: value) < (value maxgen: self)!
	= value
		^ value isNumber
			ifTrue: [ (self maxgen: value) = (value maxgen: self) ]
			ifFalse: [ false ]!
	\\ value
		" remainder after integer division "
		^ self - (self // value * value)!
	% value
		^ self \\ value
	!
	asFloat
		self subclassResponsibility
	!
	abs
		^ (self < 0)
			ifTrue: [ 0 - self ]
			ifFalse: [ self ]!
	ceiling		| i |
		i := self truncated.
		^ ((self positive) and: [ self ~= i ])
			ifTrue: [ i + 1 ]
			ifFalse: [ i ]!
	copy
		^ self!
	exp
		^ self asFloat exp!
	floor		| i |
		i := self truncated.
		^ ((self negative) and: [ self ~= i ])
			ifTrue: [ i - 1 ]
			ifFalse: [ i ]!
	fractionalPart
		^ self - self truncated!
	generality
		self subclassResponsibility
	!
	isInteger
		^ self isLongInteger or: [ self isShortInteger ]!
	isNumber
		^ true!
	isShortInteger
		^ false!
	isLongInteger
		^ false!
	ln
		^ self asFloat ln!
	log: value
		^ self ln / value ln!
	maxgen: value
		(self isNumber and: [ value isNumber ])
			ifFalse: [ ^ smalltalk error: 
				'arithmetic on non-numbers' ].
		^ (self generality > value generality)
			ifTrue: [ self ]
			ifFalse: [ value coerce: self ]!
	negated
		^ 0 - self!
	negative
		^ self < 0!
	positive
		^ self >= 0!
	quo: value
		^ (self maxgen: value) quo: (value maxgen: self)!
	raisedTo: x	| y |
		x negative 
			ifTrue: [ ^ 1 / (self raisedTo: x negated) ].
		x isShortInteger 
			ifTrue: [ (x = 0) ifTrue: [ ^ 1 ].
				  y := (self raisedTo: (x quo: 2)) squared.
				  x odd ifTrue: [ y := y * self ].
				  ^ y ]
				"use logrithms to do exponeneation"
			ifFalse: [ ^ ( x * self ln ) exp ]!
	reciprocal
		^ 1 / self!
	rem: value
		^ self - ((self quo: value) * value)!
	roundTo: value
		^ (self / value ) rounded * value!
	sign
		^ (self = 0) ifTrue: [ 0 ]
			ifFalse: [ self / self abs ]!
	sqrt
		^ (self negative)
			ifTrue: [ smalltalk error: 'sqrt of negative']
			ifFalse: [ self raisedTo: 0.5 ]!
	squared
		^ self * self!
	strictlyPositive
		^ self > 0!
	to: value
		^ Interval lower: self upper: value step: 1!
	to: value by: step
		^ Interval lower: self upper: value step: step!
	truncated
		self subclassResponsibility
	!
	trucateTo: value
		^ (self / value) trucated * value!
!


