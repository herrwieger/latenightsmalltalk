Object subclass: #Fibonacci
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!Fibonacci class methodsFor: ''!
	fibonacci: value
		(value < 2) ifTrue:[^ value].
		^ (self fibonacci: value - 1) + (self fibonacci: value -2) 
	!
	fibonacciTest
		"n=#{i} => #{fib(i)}"
		0 to: 35 do: [ :i | Transcript show: 'n='; show:i; show:'=>'; show:(self fibonacci: i);cr]
	!
!
