Object subclass: #Stream
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!
Stream subclass: #PositionableStream
	instanceVariableNames: 'collection position limit'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!
PositionableStream subclass: #ReadStream
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!
PositionableStream subclass: #ReadWriteStream
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!
PositionableStream subclass: #WriteStream
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Stream class methodsFor: '' !

	new
		^self shouldNotImplement
	!
!

!Stream methodsFor: '' !

	close
		^self
	!
	contents
		| savedPosition contents |
		savedPosition := self position.
		self position: 0.
		contents := self upToEnd.
		self position: savedPosition.
		^contents
	!
	cr
		self nextPut: Character cr
	!
	crtab
		self cr; tab
	!
	crtab: tabCount
		self cr; tab: tabCount
	!
	do: aBlock
		[self atEnd] whileFalse: [aBlock value: self next]
	!
	flush
		^self
	!
	isEmpty
		^self position = 0
	!
	next: anInteger
		| result |
		result := self contentsSpecies new: anInteger.
		(1 to: anInteger) do: [:index | result at: index put: self next].
		^result
	!
	next: anInteger into: aCollection startingAt: startIndex
		(startIndex to: startIndex + anInteger - 1) do: [:index |
			aCollection at: index put: self next
			].
		^aCollection
	!
	next: anInteger put: anObject
		anInteger timesRepeat: [self nextPut: anObject]
	!
	next: anInteger putAll: aCollection startingAt: startIndex 
		(startIndex to: startIndex + anInteger - 1) do: [:index |
			self nextPut: (aCollection at: index)
		].
		^aCollection
	!
	nextAt: anInteger
		self skip: anInteger - 1.
		^self next
	!
	nextAvailable: anInteger
		| result stream |
		stream := WriteStream on: (result := self contentsSpecies new: anInteger).
		anInteger timesRepeat: [
			self atEnd ifTrue: [^stream contents].
			stream nextPut: self next
			].
		^result
	!
	nextInto: aCollection
		^self next: aCollection size into: aCollection startingAt: 1
	!
	nextMatchFor: anObject
		^anObject = self next
	!
	nextPutAll: aCollection
		aCollection do: [:each | self nextPut: each].
		^aCollection
	!
	peek
		| result |
		self isPositionable ifFalse: [self error: 'not Positionable'].
		self atEnd ifFalse: [
			result := self next.
			self skip: -1
			].
		^result
	!
	peekAt: anInteger
		| skipCount result |
		self isPositionable ifFalse: [self shouldNotImplement].
		skipCount := 0.
		[self atEnd not && [skipCount < anInteger]] whileTrue: [
			result := self next.
			skipCount := skipCount + 1
			].
		skipCount = anInteger ifFalse: [result := nil].
		self skip: skipCount negated.
		^result
	!
	peekFor: anObject
		self atEnd ifTrue: [^false].
		^self peek = anObject
			ifTrue: [self next. true]
			ifFalse: [false]
	!
	print: anObject 
		anObject printOn: self
	!
	reset
		self position: 0
	!
	skip: anInteger
		If anInteger is negative then skip backwards."
		self isPositionable
			ifTrue: [self position: self position + anInteger]
			ifFalse: [anInteger < 0
				ifTrue: [^self shouldNotImplement]
				ifFalse: [anInteger timesRepeat: [self next]]
				]
	!
	skipAny: aCollection
		| tally |
		tally := 0.
		[self atEnd not && [aCollection includes: self peek]] whileTrue: [
			self next.
			tally := tally + 1
			].
		^tally
	!
	skipTo: anObject
		[self atEnd] whileFalse: [self next = anObject ifTrue: [^true]].
		^false
	!
	space
		self nextPut: Character space
	!
	store: anObject 
		anObject storeOn: self
	!
	tab
		self nextPut: Character tab
	!
	tab: tabCount
		tabCount timesRepeat: [ self tab]
	!
	upTo: anObject
		| stream object |
		stream := WriteStream on: (self contentsSpecies new: 128).
		[self atEnd or: [(object := self next) = anObject]] whileFalse: [stream nextPut: object].
		^stream contents
	!
	upToAny: aCollection
		| stream object |
		stream := WriteStream on: (self contentsSpecies new: 128).
		[self atEnd or: [aCollection includes: (object := self next)]] whileFalse: [stream nextPut: object].
		^stream contents
	!
	upToEnd
		| stream |
		stream := WriteStream on: (self contentsSpecies new: 128).
		[self atEnd] whileFalse: [stream nextPut: self next].
		^stream contents
	!
!

!PositionableStream class methodsFor: '' !

	on: aCollection 
		^self basicNew initialize: aCollection
	!
	on: aCollection from: firstIndex to: lastIndex
		^self on: (aCollection copyFrom: firstIndex to: lastIndex)
	!
	with: aCollection 
		^(self basicNew initialize: aCollection) setToEnd
	!
!

!PositionableStream methodsFor: '' !

	atEnd
		^position >= limit
	!
	collection
		^collection
	!
	contents
		^collection copyFrom: 1 to: limit
	!
	contentsSpecies
		^collection class
	!
	copyFrom: firstIndex to: lastIndex
		^collection copyFrom: firstIndex to: lastIndex
	!
	initialize: aCollection
		collection := aCollection.
		position := 0.
		limit := aCollection size
	!
	isEmpty
		^limit = 0
	isPositionable
		^true
	!
	isReadable
		^true
	!
	isWritable
		^true
	!
	next
		position >= limit ifTrue: [^self error: 'end Of Stream'].
		^collection at: (position := position + 1)
	!
	nextPut: anObject
		(position := position + 1) > limit ifTrue: [
			(limit := position) > collection size ifTrue: [
				collection := collection growTo: position
				]
			].
		^collection at: position put: anObject
	!
	peek
		position >= limit ifTrue: [^self error: 'end Of Stream'].
		^collection at: position
	!
	peekFor: anObject
		position > limit ifTrue: [ ^false ].
		^self next = anObject
			ifTrue: [ true ]
			ifFalse: [ self skip: -1. false ]
	!
	position
		^position
	!
	position: anInteger
		(anInteger between: 0 and: limit)
			ifTrue: [position := anInteger]
			ifFalse: [self error: 'invalid Position']
	!
	reset
		self position: 0
	!
	setToEnd
		position := limit
	!
	size
		^limit
	!
	skip: anInteger
		self position: ((self position + anInteger max: 0) min: limit)
	!
	truncate
		limit := position
	!
!

!ReadStream methodsFor: '' !

	isReadable
		^true
	!
	isWritable
		^false
	!
!

!WriteStream class methodsFor: '' !

	on: aCollection 
		^(super on: aCollection) truncate
	!
!

!WriteStream methodsFor: '' !

	isReadable
		^false
	!
!

