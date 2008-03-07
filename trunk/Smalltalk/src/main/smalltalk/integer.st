Number subclass: #Integer
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

Integer addInterface:'smalltalk.IInteger'!
Integer addField:'private int fValue;'!
Integer addConstructor:'public Integer(int pValue) {fValue = pValue;}'!
Integer addField:'private static smalltalk.Integer[] sfIntegers = new smalltalk.Integer[512];'!
Integer addMethod:'public static smalltalk.Integer valueOf(int pValue) {
	if (pValue < -255 || pValue > 255) {
		return new smalltalk.Integer(pValue);
	}
	smalltalk.Integer result = sfIntegers[pValue+255];
	if (result!!=null) {
		return result;
	}
    synchronized(sfIntegers) {
        for(int i=0; i<512; i++) {
            sfIntegers[i] = new smalltalk.Integer(i-255);
        }
    }
    return sfIntegers[pValue+255];
}'!
Integer addMethod:'public int intValue() {return fValue;}'!
Integer addMethod: '
public java.lang.Object unbox() {
    return java.lang.Integer.valueOf(intValue());
}'!
Integer addMethod:'
public java.lang.String toString() {
	return "" + fValue;
}'!
Integer addMethod:'
public int hashCode() {
    return fValue;
}'!
Integer addMethod:'
public boolean equals(java.lang.Object obj) {
    if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() !!= obj.getClass())
        return false;
    final smalltalk.Integer other = (smalltalk.Integer) obj;
    if (fValue !!= other.fValue)
        return false;
    return true;
}'!

!Integer class methodsFor: ''!

	new
		^ smalltalk error: 'cannot create integers with new'!
!


!Integer methodsFor: ''!

	* value		
		<native:
			'public smalltalk.Object $multiply(smalltalk.Object pValue) {
		        int value = ((smalltalk.Integer)pValue).intValue();
		        return smalltalk.Integer.valueOf(intValue() * value);
		    }'
		>
		| r |
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ r := self _multiply: value.
				  "primitive will return nil on overflow"
				  r notNil ifTrue: [ r ]
				  ifFalse: [ self asLongInteger * value asLongInteger ]]
			ifFalse: [super * value]
	!
	+ value
		<native:
			'public smalltalk.Object $add(smalltalk.Object pValue) {
		        int value = ((smalltalk.Integer)pValue).intValue();
		        return smalltalk.Integer.valueOf(intValue() + value);
		    }'
		>
		| r |
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ r := self _add: value.
				  "primitive will return nil on overflow"
				  r notNil ifTrue: [ r ]
				ifFalse: [ self asLongInteger + value asLongInteger ]]
			ifFalse: [super + value]
	!
	, value
		" used to make long integer constants "
		^ self * 1000 + value!
	- value
		<native:
			'public smalltalk.Object $subtract(smalltalk.Object pValue) {
		        int value = ((smalltalk.Integer)pValue).intValue();
		        return smalltalk.Integer.valueOf(intValue() - value);
		    }'
		>
		| r |
		
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ r := self _subtract: value.
				  "primitive will return nil on overflow"
				r notNil ifTrue: [ r ]
				ifFalse: [ self asLongInteger - value asLongInteger ]]
			ifFalse: [super - value]
	!
	/ value		| t b |
		value = 0 ifTrue: [ ^ smalltalk error: 'division by zero'].

		value isInteger
			ifTrue: [ b := self gcd: value .
				  t := self quo: b.
				  b := value quo: b.
				  b negative
					ifTrue: [ t := t negated.
						  b := b negated ].
				  (b = 1) ifTrue: [ ^ t ].
				  ^ Fraction top: t bottom: b ]
			ifFalse: [^ super / value]
	!
	< value
		<native:
			'public smalltalk.Object $lessThan(smalltalk.Object pValue) {
		        int value = ((smalltalk.Integer)pValue).intValue();
		        return (smalltalk.Object)de.wieger.smalltalk.universe.AbstractUniverse.getBoolean(intValue() < value);
		    }'
		>
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ self _lessThan: value]
			ifFalse: [ "todo super < value" ]!
	= value
		<native:
			'public smalltalk.Object $equals(smalltalk.Object pValue) {
		        return (smalltalk.Object)de.wieger.smalltalk.universe.AbstractUniverse.getBoolean(equals(pValue));
		    }'
		>
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ super == value ]
			ifFalse: [ "todo super = value" ]!
	> value
		<native:
			'public smalltalk.Object $greaterThan(smalltalk.Object pValue) {
		        int value = ((smalltalk.Integer)pValue).intValue();
		        return (smalltalk.Object)de.wieger.smalltalk.universe.AbstractUniverse.getBoolean(intValue() > value);
		    }'
		>
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ self _greaterThan: value ]
			ifFalse: [ "todo super > value" ]!
	allMask: value
		" see if all bits in argument are on"
		^ value = (self bitAnd: value)!
	anyMask: value
		" see if any bits in argument are on"
		^ 0 ~= (self bitAnd: value)!
	asChar
		^ self asCharacter
	!
	asCharacter
		^ Char value: self!
	asDigit
		" return as character digit "
		(self >= 0)
			ifTrue: [ (self <= 9) ifTrue: 
					[ ^ (self + $0 asInteger) asCharacter ].
				  (self < 36) ifTrue:
					[ ^ (self + $A asInteger - 10) asCharacter ] ].
		^ smalltalk error: 'illegal conversion, integer to digit'!
	asFloat
		" should be redefined by any subclasses "
		self isShortInteger ifTrue: [ ^ self _asFloat ]!
	_asFloat
		^ Float new "todo"
	!
	asFraction
		^ Fraction top: self bottom: 1!
	asInteger
		^ self
	!
	asLongInteger | newList i |
		newList := List new.
		"todo check how to handle undeclared variables." i := 0.
		i = 0 ifTrue: [ newList add: 0 ]
			ifFalse: [ i := self abs.
					[ i ~= 0 ] whileTrue:
					[ newList addLast: (i rem: 100).
					i := i quo: 100 ] ].
		^ LongInteger negative: i negative digits: newList asArray
	!
	asString
		^ self radix: 10!
	bitAnd: value
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ self _bitAnd: value ]
			ifFalse: [ smalltalk error: 
				'arguments to bit operation must be short integer']!
	_bitAnd: value
		"todo"
	!
	bitAt: value
		^ (self bitShift: 1 - value) bitAnd: 1!
	bitInvert
		"invert all bits in self"
		^ self bitXor: -1!
	bitOr: value
		^ (self bitXor: value) bitXor: (self bitAnd: value)!
	bitShift: value
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ self _bitShift: value]
			ifFalse: [ smalltalk error: 
				'argument to bit operation must be integer']!
	_bitShift: value
		"todo"
	!
	bitXor: value
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ self _bitXor: value ]
			ifFalse: [ smalltalk error: 
				'argument to bit operation must be integer']!
	_bitXor: value
		"todo"
	!
	even
		^ (self rem: 2) = 0!
	factorial
		^ (2 to: self) inject: 1 into: [:x :y | x * y ]!
	gcd: value
		(value = 0) ifTrue: [ ^ self ].
		(self negative) ifTrue: [ ^ self negated gcd: value ].
		(value negative) ifTrue: [ ^ self gcd: value negated ].
		(value > self) ifTrue: [ ^ value gcd: self ].
		^ value gcd: (self rem: value)!
	generality
		" generality value - used in mixed class arithmetic "
		^ 2!
	highBit
		 "Return the index of the highest order 1 bit of the receiver"
		 | n bit |
		 self = 0 ifTrue: [ ^0 ].
		 bit := 0.
		 self < 0
			  ifTrue: [
					"Increment the result by one if not a power of two"
					n := self negated. 
					(n bitAnd: self) = n ifFalse: [ bit := 1 ]
			  ]
			  ifFalse: [ n := self. bit := 0 ].
		 [ n > 1073741823 ] whileTrue: [ bit := bit + 30. n := n bitShift: -30 ].
		 n > 65535 ifTrue: [ bit := bit + 16. n := n bitShift: -16 ].
		 n > 256 ifTrue: [ bit := bit + 8. n := n bitShift: -8 ].
		 n > 16 ifTrue: [ bit := bit + 4. n := n bitShift: -4 ].
		 n > 3 ifTrue: [ bit := bit + 2. n := n bitShift: -2 ].
		 n > 1 ifTrue: [ bit := bit + 1. n := n bitShift: -1 ].
		 ^n + bit
	!
	isShortInteger
		^ true!
	lcm: value
		^ (self quo: (self gcd: value)) * value!
	odd
		^ (self rem: 2) ~= 0!
	printString
		^ self asString!
	quo: value
		<native:
			'public smalltalk.Object quo(smalltalk.Object pValue) {
		        int value = ((smalltalk.Integer)pValue).intValue();
		        return smalltalk.Integer.valueOf(intValue() / value);
		    }'
		>
		| r |
		^ (self isShortInteger and: [value isShortInteger])
			ifTrue: [ r := self _quo: value.
				(r isNil)
					ifTrue: [ smalltalk error:
						'quo: or rem: with argument 0']
					ifFalse: [ r ]]
			ifFalse: [ "todo ^ super quo: value" ]!
	radix: base 	| sa text |
		" return a printed representation of self in given base"
		sa := self abs.
		text := (sa \\ base) asDigit asString.
		^ (sa < base)
			ifTrue: [ (self negative)
					ifTrue: [ '-' , text ]
					ifFalse: [ text ]]
			ifFalse: [ ((self quo: base) radix: base), text ]!
	scramble
	!
		
	timesRepeat: aBlock	| i |
		" use while, which is optimized, not to:, which is not"
		i := 0.
		[ i < self ] whileTrue:
			[ aBlock value. i := i + 1]!
	truncated
		^ self!
!
