IndexedCollection subclass: #OrderedCollection
	instanceVariableNames: 'array firstIndex lastIndex'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!OrderedCollection class methodsFor: ''!

	new: anInteger
		^ super new initializeWith: anInteger
	!
	new
		^self new: 16
	!
!


!OrderedCollection methodsFor: ''!

	add: anObject
		^self addLast: anObject
	!
	add: anObject after: oldObject 
		| index |
		index := self find: oldObject.
		self insert: anObject before: index + 1.
		^anObject
	!
	add: anObject afterIndex: index 
		self insert: anObject before: firstIndex + index.
		^ anObject
	!
	add: anObject before: oldObject 
		| index |
		index := self find: oldObject.
		self insert: anObject before: index.
		^anObject
	!
	add: anObject beforeIndex: index 
		self add: anObject afterIndex: index - 1.
		^ anObject
	!
	addAllLast: aCollection 
		aCollection do: [:each | self addLast: each].
		^aCollection
	!
	addFirst: anObject 
		(firstIndex = 1) ifTrue: [self growFront].
		firstIndex := firstIndex - 1.
		array at: firstIndex put: anObject.
		^ anObject
	!
	addLast: anObject 
		lastIndex = array size ifTrue: [self growBack].
		lastIndex := lastIndex + 1.
		array at: lastIndex put: anObject.
		^ anObject
	!
	at: index 
		(index < 1 or: [index + firstIndex - 1 > lastIndex])
			ifTrue: [self error: 'invalid index']
			ifFalse: [^ array at: index + firstIndex - 1]
	!
	at: index ifAbsentPut: block
		| elem |
		index <= self size ifTrue: [
			^ (elem := self at: index)
				ifNotNil: [elem]
				ifNil: [self at: index put: block value]
		].
		[self size < index] whileTrue: [self add: nil].
		^ self at: index put: block value
	!
	at: index put: anObject 
		| idx |
		idx := index + firstIndex - 1.
		(idx >= firstIndex and: [ idx <= lastIndex ])
			ifTrue: [^(array at: idx + firstIndex - 1 put: anObject)]
			ifFalse: [self error: 'No Such Element']
	!
	collect: aBlock 
		| newCollection |
		newCollection := self class new: self size.
		(firstIndex to: lastIndex) do: [:index |
			newCollection addLast: (aBlock value: (array at: index))
			].
		^ newCollection
	!
	do: aBlock 
		(firstIndex to: lastIndex) do: [ :index |
			aBlock value: (array at: index).
			]
	!
	find: anObject
		( firstIndex to: lastIndex) do: [ :index |
			((self at: index) = anObject) ifTrue: [ ^ index ]
			].
		^ smalltalk error: 'Not Found'
	!
	grow
		| newArray |
		newArray := Array new: self size + 2.
		newArray replaceFrom: 1 to: array size with: array startingAt: 1.
		array := newArray
	!
	growBack
		" *refactor* "
		| newLast delta |
		newLast := self size.
		array size - self size = 0 ifTrue: [self grow].
		(delta := firstIndex - 1) = 0 ifTrue: [^ self].
		(1 to: newLast) do: [ :index |
			array at: index put: (array at: index + delta).
			array at: index + delta put: nil
			].
		firstIndex := 1.
		lastIndex := newLast
	!
	growFront
		" *refactor* "
		| delta index |
		delta := array size - self size.
		(delta = 0) ifTrue:  [
			self grow.
			delta := array size - self size
			].
		lastIndex = array size ifTrue: [^ self].
		index := array size.
		[index > delta] whileTrue: [
			array at: index put: (array at: index - delta + firstIndex - 1).
			array at: index - delta + firstIndex - 1 put: nil.
			index := index - 1
			].
		firstIndex := delta + 1.
		lastIndex := array size
	!
	includes: anObject
		| idx |
		( firstIndex to: lastIndex) do: [ :i |
			(( self at: i ) = anObject) ifTrue: [ ^ true ]
			].
		^false
	!
	initializeWith: anInteger
		array := Array new: anInteger.
		self reset
	!
	insert: anObject before: spot
		| delta spotIndex|
		spotIndex := spot.
		delta := spotIndex - firstIndex.
		(firstIndex = 1) ifTrue: [
			self growFront.
			spotIndex := firstIndex + delta
			].
		firstIndex := firstIndex - 1.
		array
			replaceFrom: firstIndex
			to: spotIndex - 2
			with: array
			startingAt: firstIndex + 1.
		array at: spotIndex - 1 put: anObject.
		^ anObject
	!
	remove: anObject ifAbsent: absentBlock
		| index |
		(firstIndex to: lastIndex) do: [ :index |
			(anObject = (self at: index))
				ifTrue: [
					self removeAt: index.
					^ anObject
					]
			].
		^ absentBlock value
	!
	removeFirst
		| firstObject |
		self emptyCheck.
		firstObject := self at: firstIndex.
		self at: firstIndex put: nil.
		firstIndex := firstIndex + 1.
		^ firstObject
	!
	removeAt: index
		| removed |
		removed := self at: index.
		array 
			replaceFrom: index 
			to: lastIndex - 1 
			with: array 
			startingAt: index+1.
		self at: lastIndex put: nil.
		lastIndex := lastIndex - 1.
		^ removed
	!
	removeLast
		| lastObject |
		self emptyCheck.
		lastObject := array at: lastIndex.
		array at: lastIndex put: nil.
		lastIndex := lastIndex - 1.
		^ lastObject
	!
	reset
		firstIndex := 1.
		lastIndex := 0.
	!
	select: aBlock 
		| newCollection element |
		newCollection := self class new.
		(firstIndex to: lastIndex) do: [:index |
			(aBlock value: (element := array at: index)) ifTrue: [
				newCollection addLast: element
				]
			].
		^ newCollection
	!
	size
		^lastIndex - firstIndex + 1
	!
!


