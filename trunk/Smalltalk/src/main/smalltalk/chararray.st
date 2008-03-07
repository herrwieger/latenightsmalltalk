ArrayedCollection variableCharSubclass: #CharArray
	instanceVariableNames: ''
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!


CharArray addConstructor: '
public CharArray(java.lang.String pString) {
        $array = new smalltalk.Object[pString.length()];
        char[]  characters  = pString.toCharArray();
        for (int i=0; i<characters.length; i++) {
            $array[i] =  smalltalk.Integer.valueOf(characters[i]);
        }
}'!
CharArray addMethod: '
public int hashCode() {
    return toString().hashCode();
}'!
CharArray addMethod: '
public boolean equals(java.lang.Object pOtherObject) {
    if (this == pOtherObject)
        return true;
    if (pOtherObject == null)
        return false;
    if (getClass() !!= pOtherObject.getClass())
        return false;
    return toString().equals(pOtherObject.toString());
}'!
CharArray addMethod: '
public java.lang.String toString() {
    StringBuilder   builder = new StringBuilder();
    for (int i=0; i<$array.length; i++) {
        if ($array[i]==null) {
            continue;
        }            
        smalltalk.Integer    anInteger = (smalltalk.Integer)$array[i];
        builder.append((char)anInteger.intValue());
    }
    return builder.toString();
}'!
CharArray addMethod: '
public java.lang.Object unbox() {
    return toString();
}'!

!CharArray class methodsFor: ''!
	new: size
		^ self basicNew: size!
!

!CharArray methodsFor: ''!

	asCharArray
		^ self
	!
	asString
		self notImplementedYet
	!
	logChunk
		self notImplementedYet
	!
	hash
		<native:
			'public smalltalk.Object hash() {
		        return smalltalk.Integer.valueOf(hashCode());
		    }'
		>
	!
	size: value
		self notImplementedYet
	!
!


