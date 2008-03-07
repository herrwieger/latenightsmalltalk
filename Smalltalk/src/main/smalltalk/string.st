CharArray subclass: #String
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

String addConstructor:'public String(java.lang.String pString) {super(pString);}'!

!String class methodsFor: ''!
	new: size
		^ self basicNew: size
	!
	new: size withAll: aChar
		| newString |
		newString := self new: size.
		(1 to: size) do: [ :i | newString := newString, aChar ].
		^ newString
	!
!


!String methodsFor: ''!

	+ value
		^ self , value!
	, value
		(value isMemberOf: String)
			ifTrue: [ self notImplementedYet ]
			ifFalse: [ ^ self , value asString ]!
	< value
		(value isKindOf: String)
			ifTrue: [ ^ super < value ]
			ifFalse: [ ^ false ]!
	= value
		(value isKindOf: String)
			ifTrue: [ ^ super = value ]
			ifFalse: [ ^ false ]!
	asByteArray	| newArray i |
		newArray := ByteArray new: self size.
		i := 0.
		self do: [:x | i := i + 1. newArray at: i put: x asInteger].
		^ newArray!
	asInteger
		^ self inject: 0 into: [:x :y | x * 10 + y digitValue ]!
	asLowercase
		| newStr |
		newStr := self.
		(1 to: self size) do: [ :i |
			newStr at: i put: (self at: i) asLowercase ].
		^newStr
	!
	asUppercase
		| newStr |
		newStr := self.
		(1 to: self size) do: [ :i |
			newStr at: i put: (self at: i) asUppercase ].
		^newStr
	!
	asString
		^ self!
	asSymbol
		self notImplementedYet
	!
	basicAt: index
		^  (super basicAt: index) asCharacter!
	basicAt: index put: aValue
		(aValue isMemberOf: Char)
			ifTrue: [ super basicAt: index put: aValue asInteger ]
			ifFalse: [ smalltalk error:
				'cannot put non Char into string' ]
	!
	chomp |dlm|
		dlm := 10 asCharacter asString.
		^ self trimRight: dlm
	!
	chomp: delim
		^ self trimRight: delim
	!
	contains: aString
		| sl last |
		sl := aString size.
		last := self size - sl + 1.
		( 1 to: last) do: [ :i |
			( (self copyFrom: i to: sl) = aString) ifTrue: [ ^true ].
			].
		^ false
	!
	containsChars: chars
		| tmp |
		tmp := chars asString.
		( 1 to: self size) do: [ :i |
			( (tmp indexOf: (self at: i)) > 0) ifTrue: [ ^true ].
			].
		^ false
	!
	copy
		" catenation makes copy automatically "
		^ '',self!
	copyFrom: position1
		^ self copyFrom: position1 to: self size
	!
	edit	| file text |
		(file := File new)
			yourself;
			scratchFile;
			open: 'w';
			print: self;
			close.
		(editor, ' ', file name) unixCommand.
		file open: 'r'.
		text := file asString.
		file yourself; close; delete.
		^ text!
	execute	| meth |
		" execute self as body of a method "
		meth := Parser new parse: 'DoIt ' , self in: UndefinedObject.
		^ meth notNil ifTrue: [
			meth executeWith: (Array new: 1) ]	"nil"
		ifFalse: [
			nil ]!
	from: aPosition
		^ self copyFrom: aPosition to: self size.
	!
	hash
		^ super hash
	!
	indexOf: aChar
		^ self indexOf:aChar startingAt: 1
	!
	indexOf: aChar startingAt: start
		(start to: self size) do: [ :i |
			((self at: i) == aChar ) ifTrue: [ ^i ]
			].
		^ 0
	!
	isLowercase
		(1 to: self size) do: [ :i |
			( (self at: i) isLowercase) ifFalse: [ ^ false ].
			].
		^true
	!
	isUppercase
		(1 to: self size) do: [ :i |
			( (self at: i) isUppercase) ifFalse: [ ^ false ].
			].
		^true
	!
	print
		stdout print: self!
	printString
		^ '''' , self, ''''!
	reMatch: pattern
		" returns true if self matches the regex pattern, false otherwise "
		self notImplementedYet
	!
	reMatches: pattern
		" returns an Array of matches of self against regex pattern "
		self notImplementedYet
	!
	replace: oldStr with: repStr
		^ self replace: oldStr with: repStr occurrence: 1
	!
	replaceAll: oldStr with: repStr
		^ self replace: oldStr with: repStr occurrence: nil
	!
	replace: oldStr with: repStr occurrence: occur
		| idx me finds newStr oSize rSize sSize |
		idx := 1.
		finds := 0.
		newStr := ''.
		oSize := oldStr size.
		rSize := repStr size.
		sSize := self size.
		[ idx <= sSize ] whileTrue: [
			((self copyFrom: idx to: (idx+oSize-1)) = oldStr)
				ifTrue: [
					finds := finds + 1.
					( (finds = occur) or: [ occur isNil ] )
						ifTrue: [ newStr := newStr + repStr. idx := idx + oSize. ]
						ifFalse: [ newStr := newStr + (self at: idx). idx := idx + 1 ].
					]
				ifFalse: [ newStr := newStr + (self at: idx). idx := idx + 1 ].
			].
		^ newStr
	!
	reverseCase
		| newStr |
		newStr := self.
		(1 to: self size) do: [ :i |
			newStr at: i put: (self at: i) reverseCase ].
		^newStr
	!
	size
		^ self basicSize
	!
	split: aCharSet
		" return an OrderedCollection of self split on any char in aCharSet "
		^ self split: aCharSet expectedSize: 16.
	!
	split: aCharSet expectedSize: anInteger
		| aCollection aPart aChar |
		aPart := ''.
		aCollection := OrderedCollection new: anInteger.
		(1 to: self size) do: [ :i |
			aChar := self at: i.
			((aCharSet indexOf: aChar) == 0)
				ifTrue: [ aPart := aPart + aChar ]
				ifFalse: [ aCollection add: aPart. aPart := '' ].
			].
		aCollection add: aPart.
		^ aCollection
	!
	trim
		^ (self trimLeft: ' ') trimRight: ' '.
	!
	trimmed	| dlm ans |
		dlm := 10 asCharacter.
		(ans := self) isEmpty ifTrue: [
			^ans ].
		[ (ans at: 1) == dlm ] whileTrue: [
			ans := ans copyFrom: 2 to: ans size.
			ans isEmpty ifTrue: [
				^ ans ] ].
		[ (ans at: ans size) == dlm ] whileTrue: [
			ans := ans copyFrom: 1 to: ans size - 1.
			ans isEmpty ifTrue: [
				^ ans ] ].
		^ans!
	trimLeft: delim
		| ans |
		(ans := self) isEmpty ifTrue: [ ^ans ].
		[ (ans copyFrom: 1 to: delim size) = delim ] whileTrue: [ ans := ans copyFrom: delim size+1 to: ans size ].
		^ ans
	!
	trimRight: delim
		| ans |
		(ans := self) isEmpty ifTrue: [ ^ans ].
		[ (ans copyFrom: (ans size) - delim size + 1) = delim ] whileTrue: [ ans := ans copyFrom: 1 to: ans size - delim size ].
		^ ans
	!
	trimRedundant: delim
		| ans tmp sub1 sub2 idx max |
		(ans := self) isEmpty ifTrue: [ ^ans ].
		ans := (ans trimLeft: delim) trimRight: delim.
		tmp := ans copyFrom: 1 to: delim size.
		idx := delim size + 1. max := ans size - delim size + 1.

		[idx <= max] whileTrue: [
			sub1 := ans copyFrom: idx to: idx + delim size - 1.
			sub2 := ans copyFrom: idx - delim size to: idx - 1.
			( (sub1 = delim) and: [ sub2 = delim ] )
				ifFalse: [ tmp := tmp + (ans at: idx). idx := idx + 1 ]
				ifTrue:  [ idx := idx + delim size].
			].
			tmp := tmp + (ans copyFrom: idx).

		^ tmp
	!
	upTo: aPosition
		^ self copyFrom: 1 to: aPosition.
	!
	value
		" evaluate self as an expression "
		^ ( '^ [ ', self, ' ] value' ) execute
	!
	words: aBlock	| text index list |
		list := List new.
		text := self.
		[ text := text copyFrom: 
			(text indexOf: aBlock ifAbsent: [ text size + 1])
				to: text size.
		  text size > 0 ] whileTrue:
			[ index := text 
				indexOf: [:x | (aBlock value: x) not ]
				ifAbsent: [ text size + 1].
			  list addLast: (text copyFrom: 1 to: index - 1).
			  text := text copyFrom: index to: text size ].
		^ list asArray
	!
	findSubstring: key in: body startingAt: start matchTable: matchTable
		"Answer the index in the string body at which the substring key first occurs, at or beyond start.  The match is determined using matchTable, which can be used to effect, eg, case-insensitive matches.  If no match is found, zero will be returned."
		| index c1 c2 |
		matchTable isNil ifTrue: [
			key size = 0 ifTrue: [^ 0].
			start to: body size - key size + 1 do:
				[:startIndex |
				index := 1.
					[(body at: startIndex+index-1)
						= (key at: index)]
						whileTrue:
						[index = key size ifTrue: [^ startIndex].
						index := index+1]].
			^ 0
		].
	
		key size = 0 ifTrue: [^ 0].
		start to: body size - key size + 1 do:
			[:startIndex |
			index := 1.
			[c1 := body at: startIndex+index-1.
			c2 := key at: index.
			((c1 leadingChar = 0) ifTrue: [(matchTable at: c1 asciiValue + 1)]
							ifFalse: [c1 asciiValue + 1])
				= ((c2 leadingChar = 0) ifTrue: [(matchTable at: c2 asciiValue + 1)]
									ifFalse: [c2 asciiValue + 1])]
				whileTrue:
					[index = key size ifTrue: [^ startIndex].
					index := index+1]].
		^ 0
	!
	findString: subString
		"Answer the index of subString within the receiver, starting at start. If 
		the receiver does not contain subString, answer 0."
		^self findString: subString startingAt: 1.
	!
	findString: subString startingAt: start 
		"Answer the index of subString within the receiver, starting at start. If 
		the receiver does not contain subString, answer 0."
	
		^ self findSubstring: subString in: self startingAt: start matchTable: nil "todo CaseSensitiveOrder"
	!	
	findString: key startingAt: start caseSensitive: caseSensitive
		"Answer the index in this String at which the substring key first occurs, at or beyond start.  The match can be case-sensitive or not.  If no match is found, zero will be returned."
	
		caseSensitive
		ifTrue: [^ self findSubstring: key in: self startingAt: start matchTable: nil "todo CaseSensitiveOrder"]
		ifFalse: [^ self findSubstring: key in: self startingAt: start matchTable: nil "todo CaseInsensitiveOrder"]	
	!
	match: text
		"Answer whether text matches the pattern in this string.
		Matching ignores upper/lower case differences.
		Where this string contains #, text may contain any character.
		Where this string contains *, text may contain any sequence of characters."
	
		^ self startingAt: 1 match: text startingAt: 1
	"
		'*'			match: 'zort' true
		'*baz'		match: 'mobaz' true
		'*baz'		match: 'mobazo' false
		'*baz*'		match: 'mobazo' true
		'*baz*'		match: 'mozo' false
		'foo*'		match: 'foozo' true
		'foo*'		match: 'bozo' false
		'foo*baz'	match: 'foo23baz' true
		'foo*baz'	match: 'foobaz' true
		'foo*baz'	match: 'foo23bazo' false
		'foo'		match: 'Foo' true
		'foo*baz*zort' match: 'foobazort' false
		'foo*baz*zort' match: 'foobazzort' false
		'*foo#zort'	match: 'afoo3zortthenfoo3zort' true
		'*foo*zort'	match: 'afoodezortorfoo3zort' true
	"	
	!
	startingAt: keyStart match: text startingAt: textStart
		"Answer whether text matches the pattern in this string.
		Matching ignores upper/lower case differences.
		Where this string contains #, text may contain any character.
		Where this string contains *, text may contain any sequence of characters."
		| anyMatch matchStart matchEnd i matchStr j ii jj |
		i := keyStart.
		j := textStart.
	
		"Check for any #'s"
		[i > self size ifTrue: [^ j > text size "Empty key matches only empty string"].
		(self at: i) = $#] whileTrue:
			["# consumes one char of key and one char of text"
			j > text size ifTrue: [^ false "no more text"].
			i := i+1.  j := j+1].
	
		"Then check for *"
		(self at: i) = $*
			ifTrue: [i = self size ifTrue:
						[^ true "Terminal * matches all"].
					"* means next match string can occur anywhere"
					anyMatch := true.
					matchStart := i + 1]
			ifFalse: ["Otherwise match string must occur immediately"
					anyMatch := false.
					matchStart := i].
	
		"Now determine the match string"
		matchEnd := self size.
		(ii := self indexOf: $* startingAt: matchStart) > 0 ifTrue:
			[ii = 1 ifTrue: [self error: '** not valid -- use * instead'].
			matchEnd := ii-1].
		(ii := self indexOf: $# startingAt: matchStart) > 0 ifTrue:
			[ii = 1 ifTrue: [self error: '*# not valid -- use #* instead'].
			matchEnd := matchEnd min: ii-1].
		matchStr := self copyFrom: matchStart to: matchEnd.
	
		"Now look for the match string"
		[jj := text findString: matchStr startingAt: j caseSensitive: false.
		anyMatch ifTrue: [jj > 0] ifFalse: [jj = j]]
			whileTrue:
			["Found matchStr at jj.  See if the rest matches..."
			(self startingAt: matchEnd+1 match: text startingAt: jj + matchStr size) ifTrue:
				[^ true "the rest matches -- success"].
			"The rest did not match."
			anyMatch ifFalse: [^ false].
			"Preceded by * -- try for a later match"
			j := j+1].
		^ false "Failed to find the match string"
	!
!
