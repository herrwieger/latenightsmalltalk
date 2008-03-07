TestCase subclass: #ExampleTest
	instanceVariableNames: 'fArray'
	classVariableNames: ''
	poolDictionaries: ''
	category: 'SUnit-Tests'
!

	
!ExampleTest methodsFor: 'testing'!
	testArray
		fArray := Array new:1.
		fArray at:1 put:42.
		self assert:((fArray at:1) = 42).
		self assert:((fArray size) = 1).
	! !
	