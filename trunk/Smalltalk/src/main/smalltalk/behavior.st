Object subclass: #Behavior
	instanceVariableNames: 'name instanceSize methods superclass variables classVariables poolDictionaries category selectors'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''
"
ANSI-Methods:
classDescription:
	allSubclasses
	allSuperclasses
	name
	subclasses
	superclass
	instantiator:
	new
"
!

!Behavior methodsFor: ''!
	basicNew
		"Primitive. Answer an instance of the receiver (which is a class) with no 
		indexable variables. Fail if the class is indexable. Essential. See Object 
		documentation whatIsAPrimitive."
	!

	basicNew: sizeRequested 
		"Primitive. Answer an instance of this class with the number
		of indexable variables specified by the argument, sizeRequested.
		Fail if this class is not indexable or if the argument is not a
		positive Integer, or if there is not enough memory available. 
		Essential. See Object documentation whatIsAPrimitive."
	!
	
	display
		('Class name: ', name asString)  print.
		(superclass notNil)
			ifTrue: [ ('superclass: ', superclass ) print ].
		'Instance Variables:' print.
		variables isNil
			ifTrue: [ 'no instance variables ' print ]
			ifFalse: [ variables display ].
		'Class Variables:' print.
		classVariables isNil
			ifTrue: [ 'no class variables ' print ]
			ifFalse: [ classVariables display ].
		'Pool Dictionaries:' print.
		poolDictionaries isNil
			ifTrue: [ 'no poolDictionaries ' print ]
			ifFalse: [ poolDictionaries display ].
		('Category: ' + category) print.
		'Subclasses: ' print.
		subClasses isNil
			ifTrue: [ 'no subClasses ' print ]
			ifFalse: [ subClasses display ].
	!
	fileOut: aSym	| aMth cStr mStr aStr aFile |
		" file out one method on class.method.st "
		(aMth := self methodNamed: aSym) isNil ifTrue: [
			^ self ].
		cStr := aMth methodClass name asString.
		mStr := aMth name asString.
		aStr := cStr , '.' , mStr , '.st'.
		(aFile := File name: aStr mode: 'w') open.
		aFile putChunk: '{'.
		aFile putChunk: cStr , ' methods'.
		aFile putChunk: aMth trimmedText.
		aFile putChunk: '}'.
		aFile close!
	fileOutMethodsOn: aFile	| sorted |
		" file out all methods "
		methods isNil ifTrue: [
			methods := Dictionary new ].	"fix"
		methods isEmpty ifFalse: [
			sorted := methods sort: [ :x :y |
				x name asString < y name asString ].
			aFile putChunk: '{'.
			aFile putChunk: name asString , ' methods'.
			sorted do: [ :y |
				aFile putChunk: y trimmedText ].
			aFile putChunk: '}' ]!
	instanceSize
		^ instanceSize
	!
	logMethod: aMethod
		'{' logChunk.
		(self name asString , ' methods') logChunk.
		aMethod trimmedText logChunk.
		'}' logChunk!
	methodNamed: name
		(methods includesKey: name)
			ifTrue: [ ^ methods at: name ].
		(superclass notNil)
			ifTrue: [ ^ superclass methodNamed: name ].
		^ nil!
	methods
		^ methods!
	name
		^ name!
	name: aString
		name := aString
	!
	name: nSym instanceSize: iInt methods: mDict superclass: sClass variables: vArray classVariables: cvArray poolDictionaries: pdArray category: aCate
		name := nSym.
		instanceSize := iInt.
		methods := mDict.
		superclass := sClass.
		variables := vArray.
		classVariables := cvArray.
		poolDictionaries := pdArray.
		category := aCate
	!
	
	new
		"Answer a new initialized instance of the receiver (which is a class) with no indexable variables. Fail if the class is indexable."
		^ self basicNew initialize
	!
	
	new: sizeRequested 
		"Answer an initialized instance of this class with the number of indexable
		variables specified by the argument, sizeRequested."
	
		^ (self basicNew: sizeRequested) initialize  
	!
	
	printString
		^ name asString!
	respondsTo	| theSet |
		theSet := Dictionary new.
		self upSuperclassChain: 
			[:x | theSet addAll: x methods ].
		^ theSet!
	superclass
		^ superclass
	!
	superclass: aClass
		superclass := aClass!
	upSuperclassChain: aBlock
		aBlock value: self.
		(superclass notNil)
			ifTrue: [ superclass upSuperclassChain: aBlock ]!
	variables
		^ variables!
	classVariables
		^ classVariables
	!
	poolDictionaries
		^ poolDictionaries
	!
	category
		^ category
	!
	variables: nameArray
		variables := nameArray.
		instanceSize := superclass instanceSize + nameArray size
	!
	classVariables: nameArray
		classVariables := nameArray
	!
	poolDictionaries: poolArray
		poolDictionaries := poolArray
	!
	category: aCategory
		category := aCategory
	!
	watch: name	| m |
		m := self methodNamed: name.
		(m notNil) 
			ifTrue: [ ^ m watch: 
				[:a | ('executing ', name) print. a print] ]
			ifFalse: [ ^ 'no such method' ]!
   allInstances
      | all className next |
      self notImplementedYet.
      "todo next := <2 1>."
      all := OrderedCollection new.
      className := self name.
      [ next isNil] whileFalse: [
         (next class name == className) ifTrue: [
            (next == all) ifFalse: [ all add: next ].
            ].
         self notImplementedYet.
         "todo next := <2 2>."
         ].
      ^ all asArray
   !
   
   
   	selectors
   		"Answer a Set of all the message selectors specified in the receiver's method dictionary."
   		^ selectors
   	!
   	selectors: pSelectors
   		selectors := pSelectors
	!
   	allSelectors
   		"Answer all selectors understood by instances of the receiver"
		| coll |
		coll := OrderedCollection new.
		self withAllSuperclasses do:
			[:aClass | coll addAll: aClass selectors].
		^ coll asSet
   	!
   	allSubclasses
   		^ self notImplementedYet
   	!
   	allSuperclasses
		"Answer an OrderedCollection of the receiver's and the receiver's  
			ancestor's superclasses. The first element is the receiver's immediate  
			superclass, followed by its superclass; the last element is Object."
			| temp |
			^ superclass isNil
				ifTrue: [ OrderedCollection new]
				ifFalse: [temp := superclass allSuperclasses.
					temp addFirst: superclass.
					temp]
   	!
	withAllSuperclasses
		"Answer an OrderedCollection of the receiver and the receiver's 
		superclasses. The first element is the receiver, 
		followed by its superclass; the last element is Object."
	
		| temp |
		temp := self allSuperclasses.
		temp addFirst: self.
		^ temp
	!
!
