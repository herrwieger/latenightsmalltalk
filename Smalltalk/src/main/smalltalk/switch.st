Object subclass: #Switch   
   instanceVariableNames: 'const notdone'
   classVariableNames: ''
   poolDictionaries: ''
   category: ''!

!Switch methodsFor: ''!
   
   else: block
      notdone ifTrue: [ notdone <- false. block value ]!
   case: key do: block
      (notdone and: [ const = key ])
         ifTrue: [ notdone <- false. block value ]!
   key: value
      const <- value.
      notdone <- true.
	!
!  
   
   

