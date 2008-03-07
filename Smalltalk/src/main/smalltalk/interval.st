Collection subclass: #Interval
	instanceVariableNames: 'lower upper step'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Interval class methodsFor: ''!

	lower: lValue upper: uValue step: sValue
		^ self new lower: lValue upper: uValue step: sValue!
!


!Interval methodsFor: ''!

	do: aBlock		| current |
		current := lower.
		(step > 0) 
			ifTrue: [ [ current <= upper ] whileTrue:
					[ aBlock value: current.
			  		current := current + step ] ]
			ifFalse: [ [ current >= upper ] whileTrue:
					[ aBlock value: current.
					current := current + step ] ]!
	lower: aValue
		lower := aValue!
	lower: lValue upper: uValue step: sValue
		lower := lValue.
		upper := uValue.
		step := sValue!
	reverseDo: aBlock
		(self size to: 1 by: -1) do: [ :i | aBlock value: (self at: i) ]!
	step: aValue
		step := aValue!
	upper: aValue
		upper := aValue!
!


