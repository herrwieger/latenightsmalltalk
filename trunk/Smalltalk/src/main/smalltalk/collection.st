Magnitude subclass: #Collection
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

!Collection methodsFor: ''!

	< coll
		(coll respondsTo: #includes:)
			ifFalse: [ ^ smalltalk error:
				  'collection compared to non collection'].
		self do: [:x | ((self occurrencesOf: x) < 
			(coll occurrencesOf: x))ifFalse: [ ^ false ]].
		coll do: [:x | (self includes: x) ifFalse: [ ^ true ]].
		^ false!
	= coll
		self do: [:x | (self occurrencesOf: x) = 
				(coll occurrencesOf: x) ifFalse: [ ^ false ] ].
		^ true
	!
	add: aValue
		self subclassResponsibility
	!
	asArray		| newArray i |
		newArray := Array new: self size.
		i := 0.
		self do: [:x | i := i + 1. newArray at: i put: x].
		^ newArray!
	asByteArray	| newArray i |
		newArray := ByteArray new size: self size.
		i := 0.
		self do: [:x | i := i + 1. newArray at: i put: x].
		^ newArray!
	asList
		^ List new addAll: self!
	asSet
		^ Set new addAll: self!
	asString
		^ self asByteArray asString!
	at: pIndex
		self subclassResponsibility
	!
   binaryDo: aBlock 
      (1 to: self size) do:
         [:i | aBlock value: i value: (self at: i) ]!
	collect: transformBlock
		^ self inject: List new into: [:list :element|
			list add: (transformBlock value: element)]!
	display
		| out |
		"self do: [:x | x print ]!!"
		out := self inject: '' into: [:x :y | x := x, y, ' ' ].
		out print.
	!
	do: pAction
		self subclassResponsibility
	!
	find: testBlock ifAbsent: exceptionBlock
		self do: [:element | (testBlock value: element) ifTrue: [ ^element]].
		^ exceptionBlock value!
	includes: value
		self do: [:x | (x = value) ifTrue: [ ^ true ] ].
		^ false!
	includesTest: aBlock
		self do: [:element | (aBlock value: element) ifTrue: [ ^ true ] ].
		^ false!
	inject: thisValue into: binaryBlock     | last |
		last := thisValue.
		self do: [:x | last := binaryBlock value: last value: x].
		^ last!
	isEmpty 
		^ self size == 0!
	emptyCheck
		self isEmpty ifTrue: [ self error: 'Empty Collection' ].
	!
	occurrencesOf: anObject
		^ self inject: 0
				into: [:x :y | (y = anObject) 
					 ifTrue: [x + 1]
					 ifFalse: [x] ]!
	printString
		^ ( self inject: self class printString , ' ('
			 into: [:x :y | x , ' ' , y printString]), ' )'!
	reject: testBlock
		^ self select: [ :x | (testBlock value: x) not]!
	select: testBlock
		^ self inject: List new into: [:list :element |
			(testBlock value: element) ifTrue: [ list add: element]. list]!
	size
		^ self inject: 0 into: [:x :y | x + 1]!
	sort
		^ self sort: [:x :y | x < y ]!
	sort: aBlock
		^ self inject: List new
			into: [:x :y | x add: y ordered: aBlock. x]!
!


