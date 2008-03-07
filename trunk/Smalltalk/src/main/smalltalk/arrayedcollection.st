IndexedCollection subclass: #ArrayedCollection
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!ArrayedCollection methodsFor: ''!

	< coll
		(coll isKindOf: ArrayedCollection)
			ifTrue: [
				self with: coll 
					   do: [:x :y | (x = y) ifFalse: [ ^ x < y ]].
				  ^ self size < coll size ]
			ifFalse: [ ^ super < coll ]
	!
	= coll
		(coll isKindOf: ArrayedCollection)
			ifTrue: [ (self size = coll size)
					ifFalse: [ ^ false ].
				  self with: coll
					do: [:x :y | (x = y) 
						ifFalse: [ ^ false ] ]. 
				 ^ true ]
			ifFalse: [ ^ super = coll ]
	!
	+ aValue	| size1 size2 newValue |
		"catenate two arrays together "
		size1 := self size.
		size2 := aValue size.
		newValue := self class new: (size1 + size2).
		(1 to: size1) do: [ :i | newValue at: i put: (self at: i) ].
		(1 to: size2) do: [ :i | newValue at: (size1 + i) put: (aValue at: i) ].
		^ newValue
	!
	add: aValue
		^ self with: aValue
	!
	asString
		^ (self inject: '#( ' into: [:x :y | x := x, y, ' ' ]), ')'
	!
	atAllPut: anObject
		(1 to: self size) do: [ :i | self at: i put: anObject ]
	!
	at: index ifAbsent: exceptionBlock
		(self includesKey: index)
			ifTrue: [ ^ self basicAt: index ]
			ifFalse: [ ^ exceptionBlock value ]
	!
	at: index put: value
		^ self basicAt: index put: value.
	!
	binaryDo: aBlock
		(1 to: self size) do:
			[:i | aBlock value: i value: (self at: i) ]
	!
	collect: aBlock		| s newArray |
		s := self size.
		newArray := Array new: s.
		(1 to: s) do: [:i | newArray at: i put: 
			(aBlock value: (self at: i))].
		^ newArray
	!
	contains: aValue
		(1 to: self size) do:
			[:i | ((self at: i) = aValue) ifTrue: [^true] ].
		^false
	!
	copy
		^ self asArray
	!
	copyFrom: low to: high	| newArray newlow newhigh |
		newlow := low max: 1.
		newhigh := high min: self size.
		(high < 0) ifTrue: [ newhigh := self size + high ].
		newArray := self class new: (0 max: newhigh - newlow + 1).
		(newlow to: newhigh)
			do: [:i |  newArray at: ((i - newlow) + 1)
					put: (self at: i) ].
		^ newArray
	!
	deepCopy
		^ self deepCopyFrom: 1 to: self size
	!
	deepCopyFrom: low to: high	| newArray newlow newhigh |
		newlow := low max: 1.
		newhigh := high min: self size.
		newArray := self class new: (0 max: newhigh - newlow + 1).
		(newlow to: newhigh)
			do: [:i |  newArray at: ((i - newlow) + 1)
					put: (self at: i) copy ].
		^ newArray
	!
	do: aBlock
		(1 to: self size) do:
			[:i | aBlock value: (self at: i) ]
	!
	exchange: a and: b	| temp |
		temp := self at: a.
		self at: a put: (self at: b).
		self at: b put: temp
	!
	from: low to: high	| start ssize newArray |
		start := low max: 0.
		ssize := (high min: self size) + 1 - start max: 0.
		newArray := self class new: size.
		(1 to: ssize) do: [ :i |
			newArray at: i put: (self at: start).
			start := start + 1].
		^ newArray
	!
	includesKey: index
		^ index between: 1 and: self size
	!
	replaceFrom: startIndex to: stopIndex with: anArray startingAt: repStart
		| count repSize repIndex |
		count := stopIndex - startIndex + 1.
		repSize := anArray size - repStart + 1.
		(count > repSize) ifTrue: [ smalltalk error: 'illegal index' ].
		repIndex := repStart.
		(startIndex to: stopIndex) do: [ :index |
			self at: index put: (anArray at: repIndex).
			repIndex := repIndex + 1.
			].
	!
	reverseDo: aBlock
		(self size to: 1 by: -1) do:
			[:i | aBlock value: (self at: i) ]
	!
	select: aCond	| newList |
		newList := List new.
		self do: [:i | (aCond value: i) ifTrue: [newList addLast: i]].
		^ newList asArray
	!
	shallowCopy
		^ self copyFrom: 1 to: self size
	!
	size
		^ self basicSize
	!
	with: newElement
		| s newArray |
		s := self size.
		newArray := Array new: (s + 1).
		(1 to: s) do: [:i | newArray at: i put: (self at: i) ].
		newArray at: s+1 put: newElement.
		^ newArray
	!
	with: coll do: aBlock
		(1 to: (self size min: coll size))
			do: [:i | aBlock value: (self at: i) 
					value: (coll at: i) ]
	!
	with: coll ifAbsent: z do: aBlock	| xsize ysize |
		xsize := self size.
		ysize := coll size.
		(1 to: (xsize max: ysize))
			do: [:i | aBlock value:
			  (i <= xsize ifTrue: [ self at: i ] ifFalse: [ z ])
			  value:
			  (i <= ysize ifTrue: [ coll at: i ] ifFalse: [ z ])]
	!
!


