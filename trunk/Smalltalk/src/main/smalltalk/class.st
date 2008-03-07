Behavior subclass: #Class
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Class methodsFor: ''!

	fileOut		| f |
		" file out whole class on class.st "
		(f := File name: (name asString,'.st') mode: 'w') open.
		self fileOutOn: f.
		f close!
	fileOutClassOn: aFile	| dlm pad buf buf2 |
		dlm := 10 asCharacter asString.
		pad := 9 asCharacter asString.
		buf :=	superClass isNil ifTrue: [
				'nil' ]
			ifFalse: [
				superClass name asString ].
		buf := buf , dlm , pad.
		buf := buf , 'subclass: ' , self name printString.
		buf := buf , dlm , pad.
		buf2 := ''.
		variables notNil ifTrue: [
			variables inject: '' into: [ :p :v |
				buf2 := buf2 , p , v.
				' ' ] ].
		buf := buf , 'instanceVariableNames: ' , buf2 printString.
		classVariables notNil ifTrue: [
			classVariables inject: '' into: [ :p :v |
				buf2 := buf2 , p , v.
				' ' ] ].
		buf := buf , 'classVariableNames: ' , buf2 printString.
		aFile putChunk: buf!
	fileOutOn: aFile
		" file out class description "
		self fileOutClassOn: aFile.
		self class fileOutMethodsOn: aFile.
		self fileOutMethodsOn: aFile!
	hierarchy
		^ self hierarchy: 0!
	hierarchy: n	| result |
		result := ''.
		(1 to: n) do: [ :i| result := result + ' '].
		result := result + name.
		self subClasses do: [ :c|
			result := result + (Char newline asString) + (c hierarchy: n+1) ].
		^ result!
	methodsFor: aCategory
		Transcript show: ('in `' + (self name) + '` methodsFor');cr.
		^ self
	!
	subClasses
		^ classes inject: List new
			into: [:x :y | (y superClass == self)
						ifTrue: [ x add: y]. x ]!
	subclass: aSymbol instanceVariableNames: aString
		^ self subclass: aSymbol instanceVariableNames: aString classVariableNames: '' poolDictionaries: '' category: ''
	!
	subclass: aSymbol instanceVariableNames: aString classVariableNames: aCString
		^ self subclass: aSymbol instanceVariableNames: aString classVariableNames: aCString poolDictionaries: '' category: ''
	!
	subclass: aSymbol instanceVariableNames: aString classVariableNames: aCString poolDictionaries: aPool
		^ self subclass: aSymbol instanceVariableNames: aString classVariableNames: aCString poolDictionaries: aPool category: ''
	!
	subclass: aSymbol instanceVariableNames: aString classVariableNames: aCString poolDictionaries: aPool category: aCate
		| newMeta varArray cvarArray newClass |
		(aCString size > 0)
			ifTrue: [ cvarArray := aCString words: [ :x | x isAlphaNumeric ] ].
		newMeta := Metaclass
			metaName: (aSymbol asString , 'Meta') asSymbol
			instanceSize: self class instanceSize + cvarArray basicSize
			methods: Dictionary new
			superClass: self class
			variables: (Array primOrefs: 0)
			classVariables: (Array primOrefs: 0)
			poolDictionaries: aPool
			category: aCate.
			"variables: (Array primOrefs: 0)."
		(aString size > 0)
			ifTrue: [ varArray := aString words: [ :x | x isAlphaNumeric ] ].
		newClass := newMeta
			instName: aSymbol
			instanceSize: self instanceSize + varArray basicSize + cvarArray basicSize
			methods: Dictionary new
			superClass: self
			variables: varArray
			classVariables: cvarArray
			poolDictionaries: nil "(Array primOrefs: 0)"
			category: aCate.
		newMeta name assign: newMeta.
		aSymbol assign: newClass.
		classes at: aSymbol put: newClass.
		^ newClass!
!
