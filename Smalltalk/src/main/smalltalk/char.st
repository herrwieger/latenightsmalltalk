Magnitude subclass: #Char
	instanceVariableNames: 'value'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

Char addConstructor:'public Char(char pValue) {value(smalltalk.Integer.valueOf(pValue));}'!
Char addMethod: '
public java.lang.Object unbox() {
    return java.lang.Character.valueOf((char)((smalltalk.Integer)value).intValue());
}'!

!Char class methodsFor: ''!

	cr
		"return return character"
		^ self value: 13
	!
	newline
		"return newline character"
		^ self value: 10
	!
	nl
		^ self newline
	!
	tab
		^ self value: 9
	!
	value: aValue
		^ self new value: aValue
	!
!


!Char methodsFor: ''!

	< aValue
		" can only compare characters to characters "
		^ aValue isChar
			ifTrue: [ value < aValue asInteger ]
			ifFalse: [ smalltalk error: 'char compared to nonchar']!
	== aValue
		^ aValue isChar
			ifTrue: [ value = aValue asInteger ]
			ifFalse: [ false ]!
	, aChar
		^ self asString, aChar asString
	!
	asInteger
		^ value!
	asString
		" make ourselves into a string "
		^ ' ' copy yourself; at: 1 put: self; yourself!
	digitValue
		" return an integer representing our value "
		self isDigit ifTrue: [ ^ value - $0 asInteger ].
		self isUppercase ifTrue: [ ^ value - $A asInteger + 10 ].
		^ smalltalk error: 'illegal conversion, char to digit'!
	downTo: aValue
		^ self to: aValue by: -1
	!
	isAlphaNumeric
		^ (self isAlphabetic) or: [ self isDigit ]!
	isAlphabetic
		^ (self isLowercase) or: [ self isUppercase ]!
	isBlank
		^ value = $  asInteger " blank char "!
	isChar
		^ true!
	isDigit
		^ value between: $0 asInteger and: $9 asInteger!
	isXDigit
		"FIX - there must be a better way to do this"
		^ (value between: $0 asInteger and: $9 asInteger) or: [
			((value asChar asUppercase asInteger) between: $A asInteger and: $F asInteger)
		]
	!
	isLowercase
		^ value between: $a asInteger and: $z asInteger!
	isUppercase
		^ value between: $A asInteger and: $Z asInteger!
	asLowercase
		self isUppercase
			ifTrue: [ ^ Char value: ((value - 65) + 97)]!
	printString
		^ '$', self asString!
	reverseCase
		self isAlphabetic
			ifTrue: [
				self isUppercase
					ifTrue: [ ^ Char value: ((value - 65) + 97)]
					ifFalse: [ ^ Char value: ((value -97) + 65)].
				]
			ifFalse: [ ^ self ].
	!
	to: aValue
		^ Interval lower: self asInteger upper: aValue asInteger step: 1!
	to: aValue by: step
		^ Interval lower: self asInteger upper: aValue asInteger step: step!
	asUppercase
		self isLowercase
			ifTrue: [ ^ Char value: ((value -97) + 65)]!
	value: aValue
		" private - used for initialization "
		value := aValue!
!


