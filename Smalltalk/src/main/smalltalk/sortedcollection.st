OrderedCollection subclass: #SortedCollection
	instanceVariableNames: 'sortBlock sortMessage'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!SortedCollection class methodsFor: ''!

	new: anInteger
		^ (super new: anInteger) initialize
	!
	new
		"Answer an SortedCollection of default size"
		^self new: 16
	!
	with: anObject
		| newSO |
		newSO := self new.
		newSO add: anObject.
		^ newSO
	!
	withAll: aCollection
		| newSO |
		newSO := self new.
		newSO addAll: aCollection.
		^ newSO
	!
!


!SortedCollection methodsFor: ''!

	add: anObject
		self addLast: anObject.
		self reSort.
		^ anObject
	!
	addAll: aCollection
		aCollection do: [ :element |
			self addLast: element].
		self reSort.
		^aCollection
	!
	bubbleSort
		| temp |
		(self size to: 1 by: -1) do: [ :i |
			(1 to: i-1) do: [ :j |
				(sortBlock value: (self at: j) value: (self at: (j+1)) ) ifTrue: [
						temp := self at: j.
						self at: j put: (self at: j+1).
						self at: (j+1) put: temp.
					].
				].
			].
	!
	initialize
		self sortMessage: #bubbleSort.
		self sortBlock: [ :a :b | a > b ].
		self sort.
	!
	reSort
		self sort
	!
	sort
		self perform: sortMessage
	!
	sortBlock
		^ sortBlock
	!
	sortBlock: aBlock
		sortBlock := aBlock.
		self reSort
	!
	sortMessage
		^ sortMessage
	!
	sortMessage: aMessage
		sortMessage := aMessage
	!
!


