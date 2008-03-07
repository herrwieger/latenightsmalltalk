Array subclass: #QuickSortArray
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


!QuickSortArray methodsFor: ''!
quicksort: l to: r
	" sorts the array between l and r after the quicksort method 
	  by Michael Neumann 1998
	"
	| m il ir temp |
	   (r > l) ifTrue: [
	      m := self at: ((l + r) // 2).
	      il := l.
	      ir := r.
	      [
	         [(self at: il) < m] whileTrue: [il := il + 1.].
	         [(self at: ir) > m] whileTrue: [ir := ir - 1.].
	         (il < ir) ifTrue: [
	            " swap "
	            temp := self at: il.
	            self at: il put: (self at: ir).
	            self at: ir put: temp.
	         ].
	      ] doUntil: [il >= ir.].
	      self quicksort: l to: (il - 1).
	      self quicksort: (il + 1) to: r.
	   ].
	^self.
	!
!