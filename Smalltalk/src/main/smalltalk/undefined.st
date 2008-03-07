Object subclass: #UndefinedObject
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!UndefinedObject methodsFor: ''!

	initBot	| aBlock saveFile saveAns |
		" initialize the initial object image "
		aBlock := [ files do: [:f | f notNil ifTrue: [ f open ]].
					echoInput := false.
					scheduler run.
					scheduler := Scheduler new.
					systemProcess := aBlock newProcess ].
		scheduler := Scheduler new.
		systemProcess := aBlock newProcess.
		<153 true>. "primReclaim"
		saveFile := File
			name: 'systemImage'
			open: 'w'.
		saveAns := saveFile saveImage.
		saveFile close.
		stdout
			yourself;
			printNoReturn: 'saveImage: ';
			print: saveAns printString.
	!
	initMid
		" initialize the initial object image "
		| metaclasses key |
		metaclasses := Dictionary new.
		symbols binaryDo: [ :x :y |
			(y class == Metaclass) ifTrue: [
				key := (x asString copyFrom: 1 to: x basicSize - 5) asSymbol.
				metaclasses at: key put: y ] ].
		classes := Dictionary new.
		classvariables := Dictionary new.
		symbols binaryDo: [ :x :y |
			((metaclasses at: x ifAbsent: [nil]) == y class) ifTrue: [
				classes at: x put: y ] ]!
	initTop
		" initialize the initial object image "
		files := Array new: 128.
		(stdin := File name: 'stdin' mode: 'r') open.
		(stdout := File name: 'stdout' mode: 'w') open.
		(stderr := File name: 'stderr' mode: 'w') open.
		editor := 'vi'!
	initialize
		" initialize the initial object image "
		smalltalk := Smalltalk new.
		self initTop.
		self initMid.
		self initBot.
	!
	restart
		| aBlock args |
		" initialize the initial object image "
		"smalltalk := Smalltalk new."
		self initTop.
		self initMid.
		ARGC := nil. ARGV := nil.
		aBlock := [
			echoInput := false.
			scheduler run.
			scheduler := Scheduler new.
			systemProcess := aBlock newProcess ].
		scheduler := Scheduler new.
		systemProcess := aBlock newProcess.
	!
	runScript: aScript option: aOption
		| aBlock |
		" initialize the initial object image "
		smalltalk := Smalltalk new.
		self initTop.
		self initMid.
		aBlock := [
			echoInput := false.
			scheduler runScript: aScript option: aOption.
			scheduler := Scheduler new.
			systemProcess := aBlock newProcess ].
		scheduler := Scheduler new.
		systemProcess := aBlock newProcess.
	!
	isNil
		^ true!
	notNil
		^ false!
	printString
		^ 'nil'!
	ARGV: anArray
		ARGC := anArray size.
		ARGV := anArray
	!
!


