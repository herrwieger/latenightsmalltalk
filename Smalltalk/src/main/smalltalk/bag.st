Collection subclass: #Bag
	instanceVariableNames: 'elements'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Bag class methodsFor: '' !

	new
		"Answer an empty Bag."
		^self basicNew initialize
	!
!


!Bag methodsFor: '' !

	add: anObject
		"Answer anObject.  Add anObject to the elements of the receiver."
		elements
			at: anObject
			put: (self occurrencesOf: anObject) + 1.
		^anObject
	!
	add: anObject withOccurrences: anInteger
		"Answer anObject.  Add anObject to the elements
		of the receiver anInteger number of times."
		elements
			at: anObject
			put: (elements at: anObject ifAbsent: [0]) + anInteger.
		^anObject
	!
	at: anInteger
		"Answer the element of the receiver at index
		 position anInteger.  Report an error since
		 bags are not indexable."
		self errorNotIndexable
	!
	at: anInteger put: anObject
		"Replace the element of the receiver at index
		 position anInteger with anObject.  Report an
		 error, since bags are not indexable."
		self errorNotIndexable
	!
	do: aBlock
		"For each element in the receiver, evaluate
		 aBlock with that element as the argument."
		| aValue |
		elements keys do: [ :x |
			aValue := self occurrencesOf: x.
			aValue timesRepeat: [
				aBlock value: x
				]
			]
	!
	elements
		"Answer elements in the receiver."
		^elements
	!
	includes: anObject
		"Answer true if the receiver contains an
		 element equal to anObject, else answer false."
		elements at: anObject ifAbsent: [^false].
		^true
	!
	initialize
		"Private - initialize the receiver to be empty."
		elements := Dictionary new
	!
	occurrencesOf: anObject
		"Answer the number of elements of the receiver equal to anObject."
		^elements at: anObject ifAbsent: [^0]
	!
	printString
		| output kCnt |
		output := self class printString , ' ( '.
		elements keys do: [ :aKey |
			kCnt := self occurrencesOf: aKey.
			output := output + (aKey asString) + ':' + (kCnt asString) + ' '.
			].
		output := output + ')'.
		^ output
	!
	remove: anObject ifAbsent: aBlock
		"Answer anObject.  Remove one occurrence of anObject from the receiver collection.  If
		 anObject is not an element of the receiver, evaluate aBlock (with no arguments)."
		| occurrences |
		(occurrences := elements
			at: anObject
				ifAbsent: [^aBlock value]) = 1
					ifTrue: [elements removeKey: anObject]
					ifFalse: [
						elements at: anObject
						put: occurrences - 1].
		^anObject
	!
	size
		"Answer the number of elements in the receiver collection."
		| answer |
		answer := 0.
		elements associationsDo: [ :key :count |
				answer := answer + count. ].
		^answer
	!
!


