Number subclass: #Float
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

Float addConstructor:' public Float(java.lang.String pRepresentation){/*TODO*/}'!
Float addMethod: '
public java.lang.Object unbox() {
    return java.lang.Float.valueOf(0.0f);
}'!

!Float class methodsFor: ''!

	new
		^ smalltalk error: 'cannot create floats with new'!
!


