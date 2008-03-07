Object subclass: #Smalltalk
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Smalltalk methodsFor: ''!

	echo
		" enable - disable echo input "
		echoInput := echoInput not
	!
	debug: aString
		stderr print: aString.
	!
	error: aString
		| aProcess |
		" print a message, and remove current process "
		stderr print: aString.
		"scheduler currentProcess yourself; trace; terminate!!"
		aProcess := scheduler currentProcess yourself.
		(aProcess class = UndefinedObject) ifFalse: [
			aProcess trace; terminate ].
	!
	exit
		self shutDown.
	!
	exitSavingImage
		self exitSavingImage: 'systemImage'.
	!
	exitSavingImage: name
		self saveImage: name.
		self shutDown.
	!
	getPrompt: aString
		stdout printNoReturn: aString.
		^ stdin getString!
	inquire: aString	| response |
		response := self getPrompt: aString.
		response isNil
			ifTrue: [ ^ false ].
		^ 'Yy' includes: (response at: 1 ifAbsent: [])
	!
	perform: message withArguments: args
		^ self perform: message withArguments: args
			ifError: [ self error: 'cant perform' ]!
	perform: message withArguments: args ifError: aBlock	
			| receiver method |
		receiver := args at: 1 ifAbsent: [ ^ aBlock value ].
		method := receiver class methodNamed: message.
		^ method notNil 
			ifTrue: [ method executeWith: args ]
			ifFalse: aBlock
	!
	perform: message on: receiver withArguments: args
		| method |
		method := receiver class methodNamed: message.
		^ method notNil 
			ifTrue: [ method executeWith: args ]
			ifFalse: aBlock
	!
	print: aString
		stdout print: aString.
	!
	saveImage
		self saveImage: (self getPrompt: 'type image name: ').
		^ 'done'!
	saveImage: name	
		scheduler critical: [
			" first get rid of our own process "
			" 20050217:mft:: not sure purpose of this, so out it goes ;) "
			" scheduler removeProcess: scheduler currentProcess. "
		<153 false>. "primReclaim"
		(File name: name open: 'w')
			yourself;
			saveImage;
			close ]!
	saveMe: name
		(File name: name open: 'w')
			yourself;
			saveImage;
			close
	!
	shutDown
		files do: [ :e |
			e notNil ifTrue: [
				(#('stdin' 'stdout' 'stderr') includes: e name) ifFalse: [
					e close ] ] ].
		scheduler shutDown
	!
	watch
		^ <5>!
!


