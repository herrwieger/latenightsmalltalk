Object subclass: #Scheduler
	instanceVariableNames: 'notdone processList currentProcess'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Scheduler class methodsFor: ''!

	new
		^ self basicNew
			notdone: true
			processList: Set new
			currentProcess: nil!
!


!Scheduler methodsFor: ''!

	addProcess: aProcess
		" add a process to the process list "
		processList add: aProcess!
	critical: aBlock
		"set time slice counter high to insure bytecodes are
		executed before continuing "
		<53 10000>.
		aBlock value.
		"then yield processor "
		<53 0>.!
	currentProcess
		" return the currently executing process "
		^ currentProcess!
	initialize	| string |
		Date initialize.
		string := smalltalk getPrompt: '> '.
		string isNil ifTrue: [
			'''EOF''' logChunk.
			notdone := false ]
		ifFalse: [
			(string size > 0) ifTrue: [
				string logChunk.
				echoInput ifTrue: [ string print ].
				[ string value print ] fork ] ]!
	notdone: nBool processList: pSet currentProcess: cProc
		notdone := nBool.
		processList := pSet.
		currentProcess := cProc!
	removeProcess: aProcess
		" remove a given process from the process list "
		processList remove: aProcess.!
	run
		" run as long as process list is non empty "
		(ARGV isNil) ifTrue: [
			Date initialize.
			nil ARGV: <42 0>.
			self runScripts: <42 1>.
			].
		[ notdone ] whileTrue: [
			processList size = 0 ifTrue: [ self initialize ].
			processList do: [ :x |
				currentProcess := x.
				x execute
				]
			]
	!
	runScripts: theActions
		(1 to: theActions size) do: [ :i |
			| action |
			action := theActions at: i.
			self runScript: (action at: 2) option: (action at: 1).
			].
		(theActions size > 0) ifTrue: [ self shutDown ].
	!
	runScript: aScript option: aOption
		| string |
		(aOption ~= 'd')
			ifTrue: [
				string := 'File new fileIn: ''' + aScript + ''''.
				string logChunk.
				string value.
				]
			ifFalse: [
				aScript value.
				].
	!
	shutDown
		notdone := false
	!
	yield
		" set time slice counter to zero, thereby
		yielding to next process "
		<53 0>!
!


