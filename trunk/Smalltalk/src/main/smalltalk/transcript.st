Object subclass: #Transcript
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Transcript class methodsFor: ''!
	cr
		<native:
			'public smalltalk.Object cr() {
		        System.out.println();
		        
		        return this;
		    }'
		>
		stdout print: ''
	!
	logClose
		"todo"
	!
	logName
		"todo"
	!
	logIsOpen
		"todo"
	!
	logOpen: name
		"todo"
	!
	nl
		stdout print: ''
	!
	show: aString
		<native:
			'public smalltalk.Object show(smalltalk.Object pObject) {
		        System.out.print(pObject);
		        
		        return this;
		    }'
		>
		stdout printNoReturn: aString.
	!
	showCr: aString
		stdout print: aString.
	!
!
