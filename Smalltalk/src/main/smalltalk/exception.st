Object subclass: #Exception
	instanceVariableNames: 'messageText tag signalContext handlerContext outerContext'
	classVariableNames: ''
	poolDictionaries: ''
	category: ''!

Exception addInterface:'smalltalk.IException'!

Exception addField:'private boolean          fReturn;'!
Exception addField:'private boolean          fPass;'!
Exception addField:'private smalltalk.Object fResult;'!
Exception addField:'private boolean          fRetry;'!
Exception addField:'private boolean          fResume;'!

Exception addMethod:'public boolean $shouldReturn() {return fReturn;}'!
Exception addMethod:'public boolean $shouldPass() {return fPass;}'!
Exception addMethod:'public boolean $shouldRetry() {return fRetry;}'!
Exception addMethod:'public boolean $shouldResume() {return fResume;}'!
Exception addMethod:'public smalltalk.Object getResult() {return fResult;}'!
Exception addMethod:
'public void $clear() {
    fReturn = false;
    fPass   = false;
    fRetry  = false;        
    fResume = false;
    
    fResult = null;
}'!

!Exception class methodsFor: ''!
	signal
		^ self basicNew signal
	!
!

!Exception methodsFor: ''!
	defaultAction
		"see ANSI 5.5.1.1 Message: defaultAction"
		<native:
			'public smalltalk.Object defaultAction() {
		        throw new RuntimeException("defaultAction");
		    }'
		>
	!
	signal
		"see ANSI 5.5.2.1 Message: signal"
		"Ask ContextHandlers in the sender chain to handle this signal.  The default is to execute and return my defaultAction."
		<native:
			'public smalltalk.Object signal() {
		        smalltalk.internal.ExceptionEnvironment.handle(this);
		        
		        smalltalk.Object result = fResult;
		        $clear();
		        return result;
		    }'
		>
	!
	pass
		"see ANSI 5.5.4.3 Message: pass"
		<native:
			'public smalltalk.Object pass() {
		        fPass   = true;
		        return this;
		    }'
		>
	!
	resume
		"see ANSI 5.5.4.5 Message: resume"
		self resume: nil
	!
	resume: aValue
		"see ANSI 5.5.4.6 Message: resume: resumptionValue"
		<native:
			'public smalltalk.Object resume(smalltalk.Object pValue) {
		        fResume = true;
		        fResult = pValue;
		        
		        return this;
		    }'
		>
	!
	retry
		"see ANSI 5.5.4.7 Message: retry"
		<native:
			'public smalltalk.Object retry() {
		        fRetry  = true;
		        return this;
		    }'
		>
	!
	retryUsing: alternativeBlock
		"see ANSI 5.5.4.8 Message: retryUsing: alternativeBlock"
		<native:
			'public smalltalk.Object retryUsing(smalltalk.Object pBlock) {
		        smalltalk.Block block = (smalltalk.Block)pBlock;
		        $return(block.value());
		        
		        return this;
		    }'
		>
	!
	return
		"see ANSI 5.5.4.9 Message: return"
		self return: nil
	!
	return: returnValue
		"see ANSI 5.5.4.10 Message: return: returnValue"
		<native:
			'public smalltalk.Object $return(smalltalk.Object pValue) {
		        fReturn = true;
		        fResult = pValue;
		        
		        return this;
		    }'
		>
	!
!
