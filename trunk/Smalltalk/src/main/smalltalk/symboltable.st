Dictionary subclass: #SymbolTable
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!SymbolTable methodsFor: ''!

	hash: aKey
		^ 3 * ((aKey stringHash) rem: ((hashTable size) quo: 3))!
	printString
		^ self class printString , ' (...)'!
!


