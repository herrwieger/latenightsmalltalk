Object subclass: #JavaIntegration
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''
!

	
!JavaIntegration methodsFor: 'testing'!
	helloWorld
		(javax.swing.JFrame new:('Hello World')) show.
	! !
