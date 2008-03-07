Object subclass: #File
   instanceVariableNames: 'name number mode'
   classVariableNames: ''
   poolDictionaries: ''
   category: ''!


!File class methodsFor: ''!
   
   name: nStr mode: mStr
      ^ self new name: nStr mode: mStr!
   name: nStr open: mStr
      ^ self new name: nStr open: mStr!
   hijack: aFileNumber
      ^ self new number: aFileNumber!
!


!File methodsFor: ''!

	asString	| text line |
		text := ''.
		[ (line := self getString) notNil ]
			whileTrue: [ text := text , line ].
		^ text!
	close
		" close file, take entry out of global variable "
		number isNil ifTrue: [ ^ nil ].
		files at: number put: nil.
		<121 number>.
		number := nil.!
	delete
		('rm ', name) unixCommand!
	fileIn	| str |
		[	(str := self getChunk) notNil ] whileTrue: [
				((str at: 1) = $!!)
					ifTrue: [ self fileInSet: (str copyFrom: 2).  ]
					ifFalse: [ str execute ]
				]
			]
	!
	fileIn: name
		self name: name.
		self open: 'r'.
		self fileIn.
		self close.!
	fileInSet: methodDefLine
		| cls meta mth pos str |
		str := methodDefLine.
		pos := str indexOf: $ .
		cls := (str copyFrom: 1 to: pos - 1).
		meta :=(str copyFrom: (pos+1) to: (pos+6)) asUppercase.
		(meta = 'CLASS ') ifTrue: [ cls := cls, 'Meta' ].
		cls := cls asSymbol value.
		[ (str := self getChunk) notNil ] whileTrue: [
			(mth := Parser new parse: str in: cls) notNil ifTrue: [
				cls install: mth ] ].
	!
	flush
		" flushes file "
		number isNil ifTrue: [ ^ nil ].
		<122 number>.
		^ self
	!
	getChunk
		^ (number notNil)
			ifTrue: [<157 number>]!
	getNumber
		" get a file number - called only by open"
		(1 to: 128) do: [:i | (files at: i) isNil
			ifTrue: [ files at: i put: self. number := i. ^ nil]]!
	getString
		^ (number notNil)
			ifTrue: [<125 number>]!
	isOpen
		^ (number notNil)
	!
	mode
		^ mode
	!
	mode: m
		mode := m!
	name
		^ name!
	name: string
		name := string!
	name: nStr mode: mStr
		name := nStr.
		mode := mStr!
	name: nStr open: mStr
		name := nStr.
		mode := mStr.
		self open!
	number
		^ number
	!
	number: aFileNumber
		number := aFileNumber
	!
	open
		number notNil ifTrue: [ self close ].
		self getNumber.
		<120 number name mode> isNil
			ifTrue: [
				smalltalk error: 'open failed: ', name.
				number := nil.
				^ false
				].
		^ true!
	open: m
		self mode: m.
		self open
	!
	print: aString
		(number notNil)
			ifTrue: [<129 number aString>]
			ifFalse: [smalltalk error: 'file not open']
	!
	printNoReturn: aString
		(number notNil)
			ifTrue: [<128 number aString>]
			ifFalse: [smalltalk error: 'file not open']
	!
	putChunk: buffer
		^ (number notNil)
			ifTrue: [<158 number buffer>]
	!
	readLine
		^ self getString
	!
	readTo: end	| line result |
		result := ''.
		[(line := self readLine) notNil and: [ line ~= end]]
			whileTrue: [ result := result + line + (Char newline asString)].
		^ result!
	readUntil: conditionBlock doing: actionBlock	| line |
		[ line := self getString. line notNil]
			whileTrue: [ (conditionBlock value: line)
					ifTrue: [ ^ line ].
					actionBlock value: line ].
		^ nil!
	rewind
		" rewind file"
		number isNil ifTrue: [ ^ nil ].
		<126 0 number>.
	!
	saveImage	| saveAns |
		" subtle problem - when we read in image don't want
		  image file to be open for writing, so we remove it's
		  number from files array temporarily "
		(number notNil)
			ifTrue: [ files at: number put: nil. 
				  saveAns := <127 number>.
				  files at: number put: self]
			ifFalse: [smalltalk error: 'saveImage: file not open'].
		^saveAns!
	scratchFile
		name := 'junk.tmp'!
	write: aString
		(number notNil)
			ifTrue: [ files at: number put: aString]
			ifFalse: [smalltalk error: 'write: error']!
!


