Object subclass: #Semaphore
	instanceVariableNames: 'count processList'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Semaphore class methodsFor: ''!

	new
		^ self basicNew
			count: 0
			processList: List new!
!


!Semaphore methodsFor: ''!

	count: cInt processList: pList
		count := cInt.
		processList := pList!
	critical: aBlock
		self wait.
		aBlock value.
		self signal!
	set: aNumber
		count := aNumber!
	signal
		(processList size = 0)
			ifTrue: [ count := count + 1]
			ifFalse: [ scheduler critical:
				[ processList first resume.
					processList removeFirst ]]!
	wait		| process |
		(count = 0)
			ifTrue: [ scheduler critical:
					[ process := scheduler currentProcess.
					  processList add: process.
					  scheduler removeProcess: process].
				  scheduler yield ]
			ifFalse: [ count := count - 1]!
!


