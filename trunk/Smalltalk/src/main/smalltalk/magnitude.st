Object subclass: #Magnitude
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''

"
ANSI-Methods:
	<
	<=
	>
	>=
	between:and:
	max:
	min:
"
!


!Magnitude methodsFor: ''!
	< value
		^ (self <= value) and: [ self ~= value ]!
	<= value
		^ (self < value) or: [ self = value ]!
	= value
		^ (self == value)!
	> value
		^ (value < self)!
	>= value
		^ value <= self!
	between: low and: high
		^ (low <= self) and: [ self <= high ]!
	max: value
		^ (self < value)
			ifTrue: [ value ]
			ifFalse: [ self ]!
	min: value
		^ (self < value)
			ifTrue: [ self ]
			ifFalse: [ value ]!
	~= value
		^ (self = value) not!

	to: stop do: aBlock 
		self to: stop by: 1 do: aBlock
	!
	to: stop by: step do: aBlock 
		| nextValue |
		nextValue := self.
		[nextValue <= stop]
			whileTrue: 
				[aBlock value: nextValue.
				nextValue := nextValue + step]
	!
!
