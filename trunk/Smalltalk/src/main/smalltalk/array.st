ArrayedCollection variableSubclass: #Array
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Array class methodsFor: ''!

	basicNew
		^ self basicNew: 0
	!
	new
		^ self new: 0
	!
	with: newElm
		| newArray |
		newArray := Array new: 1.
		newArray at: 1 put: newElm.
		^ newArray
	!
	with: newElm1 with: newElm2
		| newArray |
		newArray := Array with: newElm1.
		^ newArray with: newElm2
	!
!
