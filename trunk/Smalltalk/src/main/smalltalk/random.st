Object subclass: #Random
   instanceVariableNames: ''
   classVariableNames: ''
   poolDictionaries: ''
   category: ''!


!Random methodsFor: ''!
   
   between: low and: high
      " return random number in given range "
      ^ (self next * (high - low)) + low!
   next
      " convert rand integer into float between 0 and 1 "
      ^ (<3> rem: 1000) / 1000!
   next: value | list |
      " return a list of random numbers of given size "
      list := List new.
      value timesRepeat: [ list add: self next ].
      ^ list!
   randInteger: value
      ^ 1 + (<3> rem: value)!
   set: value
      " set seed for random number generator "
      <55 value>!
!



