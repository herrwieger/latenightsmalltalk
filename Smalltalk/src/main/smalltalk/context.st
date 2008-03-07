Object subclass: #Context
	instanceVariableNames: 'linkLocation method arguments temporaries'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Context class methodsFor: ''!

	method: aMeth arguments: aVec temporaries: tVec
		^ self new method: aMeth arguments: aVec temporaries: tVec!
!


!Context methodsFor: ''!

	arguments: a
		arguments := a!
	at: key put: value
		temporaries at: key put: value!
	blockReturn
		<18 self>
			ifFalse: [ ^ smalltalk error: 
				'incorrect context for block return']!
	copy
		^ super copy temporaries: temporaries copy!
	method: m
		method := m!
	method: aMeth arguments: aVec temporaries: tVec
		method := aMeth.
		arguments := aVec.
		temporaries := tVec!
	returnToBlock: bytePtr
		" change the location we will return to, to execute a block"
		<28 self bytePtr>!
	temporaries: t
		temporaries := t!
!


