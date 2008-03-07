header
{
package de.wieger.smalltalk.parser;

import org.apache.log4j.Logger;
import java.util.*;
import de.wieger.smalltalk.smile.*;
}
class SmalltalkLexer extends Lexer("de.wieger.antlr.AbstractPositionAwareLexer");

options {
	exportVocab		= Smalltalk;
	k				= 3;
	filter			= SEPARATORS;
}

tokens {
	NIL								= "nil";
	TRUE							= "true";
	FALSE							= "false";
	SELF							= "self";
	THIS_CONTEXT					= "thisContext";
	SUPER							= "super";

	MINUS							= "-";
	EQUALS							= "=";
	LESS							= "<";
	GREATER							= ">";
	PIPE							= "|";
	PIPES_2							= "||";

	IDENTIFIER;
	KEYWORD;
	SCOPED_IDENTIFIER;
}
// Lexical Primitives

CARET:								'^';
DOT:								'.';
SEMICOLON:							';';
DOLLAR:								'$';

PIPES_3:							"|||";

LEFT_BRACKET:						'[';
RIGHT_BRACKET:						']';
LEFT_BRACE:							'{';
RIGHT_BRACE:						'}';
LEFT_PARENTHESES:					'(';
RIGHT_PARENTHESES:					')';

HASH:								'#';


protected
DIGIT:								'0'..'9';

protected
LETTER:								('A'..'Z'|'a'..'z');

protected
BINARY_CHARACTER:					"+"  | "/" | "\\" | "*" | "~" | "<" | ">" |
									"="  | "@" | "%"  | "|" | "&" | "?" | "!" |
									",";
																						
protected
WHITESPACE_CHARACTER:				"\t" | " " | "\n" | "\r";

protected
NON_QUOTE_CHARACTER:				DIGIT | LETTER | BINARY_CHARACTER |
									WHITESPACE_CHARACTER |
									LEFT_BRACKET  | RIGHT_BRACKET | LEFT_BRACE | RIGHT_BRACE |
									LEFT_PARENTHESES | RIGHT_PARENTHESES | "_" |
									CARET  | SEMICOLON | DOLLAR | HASH | ":" | DOT | "-" |
									"`";

// Numbers
	
protected
DIGITS:								(DIGIT)+;

protected
BIG_DIGITS:							(DIGIT | LETTER)+;

protected
OPTIONAL_FRACTION_AND_EXPONENT:		((DOT DIGITS) => DOT DIGITS)? (("e" | "d" | "s") ('-')? DIGITS)?;

NUMBER: 							DIGITS ("r" ('-') BIG_DIGITS | OPTIONAL_FRACTION_AND_EXPONENT);



// Other Lexical Constructs

protected
EXTENDED_LETTER:					(LETTER | '_');

protected
PIDENTIFIER:						EXTENDED_LETTER (EXTENDED_LETTER | DIGIT)*;

protected
PKEYWORD:							PIDENTIFIER ":";

protected
PSCOPED_IDENTIFIER:					PIDENTIFIER (DOT PIDENTIFIER)+;

KW_ID_SID:							(PKEYWORD) 							=> PKEYWORD
										{$setType(KEYWORD);} |
									(PSCOPED_IDENTIFIER)  	=> PSCOPED_IDENTIFIER 
										{$setType(SCOPED_IDENTIFIER);} |
									PIDENTIFIER							{$setType(IDENTIFIER);};

BLOCK_ARGUMENT:						":" PIDENTIFIER;

ASSIGNMENT_OPERATOR:				":=" ;

/**
 * Attention: MINUS, EQUALS, LESS, GREATER, PIPE, PIPES_2 have to be handled separately
 * in lexical rule binarySelector
 */
MOST_BINARY_SELECTORS:				('-' | BINARY_CHARACTER) (BINARY_CHARACTER)?;

CHARACTER_CONSTANT:					DOLLAR (NON_QUOTE_CHARACTER | "'" | "\"" );

STRING:								"'" (NON_QUOTE_CHARACTER | "''" | "\"")* "'";

protected
COMMENT:							"\"" (NON_QUOTE_CHARACTER | "'" )* "\"";

protected
SEPARATORS:							(WHITESPACE_CHARACTER | COMMENT)+;



class SmalltalkParser extends Parser("de.wieger.smalltalk.parser.AbstractSmalltalkParser");

options {
	k					= 2;
	defaultErrorHandler	= false;
}

{
	static final Logger LOG = Logger.getLogger(SmalltalkParser.class);
}
// Other Lexical Constructs

unarySelector returns[String returnSelector=""]:
	identifier:IDENTIFIER {
		returnSelector=identifier.getText();
	};


/**
 * Attention: MINUS, EQUALS, LESS, GREATER, PIPE, PIPES_2 have to be handled separately
 */
binarySelector returns[String returnSelector=""] {
	String binary;
}:
	selector:MOST_BINARY_SELECTORS {
		returnSelector = selector.getText();
	} |
	minus:MINUS {
		returnSelector = minus.getText();
	} |
	equals:EQUALS {
		returnSelector = equals.getText();
	} |
	less:LESS {
		returnSelector = less.getText();
	} |
	greater:GREATER {
		returnSelector = greater.getText();
	} |
	pipe:PIPE {
		returnSelector = pipe.getText();
	} |
	pipes2:PIPES_2 {
		returnSelector = pipes2.getText();
	};
	
symbol returns[String returnSymbol=""]:
	ident:IDENTIFIER {
		returnSymbol = ident.getText();
	}|
	
	returnSymbol=binarySelector |
	
	(
		keyword:KEYWORD {
			returnSymbol += keyword.getText();
		}
	)+;

									
// Atomic Terms

literal returns[Literal returnLiteral=null] {
	String sign = "";
}:
	 (
	 	MINUS {
	 		sign = "-";
	 })?
	 number:NUMBER {
	 	returnLiteral = buildNumberLiteral(sign + number.getText());
	 } |
	 
	 returnLiteral=namedLiteral |

	 returnLiteral=symbolLiteral |

	 character:CHARACTER_CONSTANT {
	 	returnLiteral = buildCharLiteral(character.getText());
	 }|

	 string:STRING {
	 	returnLiteral = buildStringLiteral(string.getText());
	 } |

	 arrayLiteral |

	 byteArrayLiteral |

	 bindingLiteral
;


namedLiteral returns[Literal returnLiteral=null]:
	NIL {
		returnLiteral = NilLiteral.NIL;
	} |
	
	TRUE {
		returnLiteral = BooleanLiteral.TRUE;
	} |
	
	FALSE {
		returnLiteral = BooleanLiteral.FALSE;
	}
;

symbolLiteral returns[Literal returnLiteral=null] {
	String symbolString;
}:
	HASH
	(
		symbolString=symbol {
			returnLiteral = buildSymbolLiteral(symbolString);
		} |
		string:STRING {
			returnLiteral = buildSymbolLiteral(getContentFromStringLiteral(string.getText()));
		}
	)
;

arrayLiteral:
	HASH arrayLiteralBody
;

arrayLiteralBody:
	LEFT_PARENTHESES (literal | symbol | arrayLiteralBody | byteArrayLiteralBody)* RIGHT_PARENTHESES
;

byteArrayLiteral:
	HASH byteArrayLiteralBody
;

byteArrayLiteralBody:
	LEFT_BRACKET (NUMBER)* RIGHT_BRACKET			// "integer between 0 and 255"
;

bindingLiteral:
	HASH LEFT_BRACE ValueName RIGHT_BRACE
;

// "other than named-literal, pseudo-Value-name or 'super'"
variableName returns[Token returnToken=null]:
	id:IDENTIFIER {
		returnToken = id;
	}|
	sid: SCOPED_IDENTIFIER {
		returnToken = sid;
	}
;

// Expressions and Statements

pseudoValueName returns[Value result=null]:
	self:SELF  {
		if ( currentScopeIsMethodScope() ) {
			result = Value.SELF;
		} else {
			result = Value.OUTER_SELF;
			markNeedsOuterClass();
		}
	} |
	
	thisContext:THIS_CONTEXT {
		result = Value.THIS_CONTEXT;
	}
;

primary[SmileBuilder pSmileBuilder]
	returns[Value result=null] {
		
	Token				varNameToken;
	Literal				literal;
	BlockDescription	block;
}:
	varNameToken=variableName {
		result = getValue(varNameToken, pSmileBuilder);
	} |
	
	result = pseudoValueName |
	
	result = literal |

	block=blockConstructor {
		result = buildBlockConstructor(block);
	}|
	
	LEFT_PARENTHESES result=expression[pSmileBuilder] RIGHT_PARENTHESES
;

unaryMessage[Value pReceiver, SmileBuilder pSmileBuilder]
	returns[Value result=null] {
		
	String selector;
}:
	selector=unarySelector {
		result = buildMethodInvocation(pReceiver, selector, pSmileBuilder);
	}
;

binaryMessage[Value pReceiver, SmileBuilder pSmileBuilder] 
	returns[Value result=null] {
		
	String			selector;
	Value			param;
}:
	selector=binarySelector
	param = primary[pSmileBuilder]
	(
		param = unaryMessage[param, pSmileBuilder]
	)*
	{
		List<Value>		params		= new ArrayList<Value>();
		params.add(param);
		result = buildMethodInvocation(pReceiver, selector, params, pSmileBuilder);
	}
;

keywordMessage[Value pReceiver, SmileBuilder pSmileBuilder] 
	returns[Value result=null] {
		
	List<String> 	keywords 	= new ArrayList<String>();
	List<Value>		params		= new ArrayList<Value>();
	Value			param		= null;
}:
	(
		keyword:KEYWORD {
    		keywords.add(keyword.getText());
    	}
    	param = primary[pSmileBuilder]
    	(
    		param = unaryMessage[param, pSmileBuilder]
    	)*
    	(
    		param = binaryMessage[param, pSmileBuilder]
    	)*
    	{
    		params.add(param);
    	}
	)+ {
		result = buildMethodInvocation(
        			pReceiver,
        			getMethodNameFromKeywords(keywords),
        			params,
        			pSmileBuilder);
	}
; 

cascadedMessages[Value pReceiver, Value pResult, SmileBuilder pSmileBuilder] 
	returns[Value result=pResult]:

	(SEMICOLON
		(
			result=unaryMessage[pReceiver, pSmileBuilder] |
			result=binaryMessage[pReceiver, pSmileBuilder] | 
			result=keywordMessage[pReceiver, pSmileBuilder]
		)
	)*
;


messages[Value pReceiver, SmileBuilder pSmileBuilder]
	returns[MessagesResult messagesResult=new MessagesResult(pReceiver)] {
	Value	result;
}:
	(
		result = unaryMessage[messagesResult.getResult(), pSmileBuilder] {
			messagesResult.pushResult(result);
		}
	)+
	(
		result = binaryMessage[messagesResult.getResult(), pSmileBuilder] {
			messagesResult.pushResult(result);
		}
	)*
	(
		result = keywordMessage[messagesResult.getResult(), pSmileBuilder] {
			messagesResult.pushResult(result);
		}
	)? |
	
	(
		result = binaryMessage[messagesResult.getResult(), pSmileBuilder] {
			messagesResult.pushResult(result);
		}
	)+
	(
		result = keywordMessage[messagesResult.getResult(), pSmileBuilder] {
			messagesResult.pushResult(result);
		}
	)? |
	
	result = keywordMessage[messagesResult.getResult(), pSmileBuilder] {
		messagesResult.pushResult(result);
	}
;

restOfExpression[Value pReceiver, SmileBuilder pSmileBuilder]
	returns[Value result=pReceiver] {
		
	MessagesResult	messagesResult;
}:
	(
		messagesResult=messages[pReceiver, pSmileBuilder] {
			result = messagesResult.getResult();
		} result=cascadedMessages[messagesResult.getPreviousReceiver(), result, pSmileBuilder]
	)?
;

expression[SmileBuilder pSmileBuilder]
	returns[Value result = null] {

	Token			varNameToken;
	Value			superValue = null;
	MessagesResult	messagesResult;
}:
	(variableName ASSIGNMENT_OPERATOR ) => varNameToken=variableName ASSIGNMENT_OPERATOR result=expression[pSmileBuilder] {
		result = buildAssignment(varNameToken, result, pSmileBuilder);
	}|
	
	keyword:KEYWORD EQUALS result=expression[pSmileBuilder] {
		result = buildAssignmentFromKeyword(keyword, result, pSmileBuilder);
	} |
	
	result=primary[pSmileBuilder]
		result=restOfExpression[result, pSmileBuilder] |
	
	SUPER {
    	if ( currentScopeIsMethodScope() ) {
    		superValue = Value.SUPER;
    	} else {
    		superValue = Value.OUTER_SUPER;
    		markNeedsOuterClass();
    	}
	}
	messagesResult=messages[superValue, pSmileBuilder] {
		result = messagesResult.getResult();
	}
	result=cascadedMessages[messagesResult.getPreviousReceiver(), result, pSmileBuilder]
;

expressionList[SmileBuilder pSmileBuilder]
	returns[Value result=null]:
	
	result=expression[pSmileBuilder] (DOT result=expression[pSmileBuilder])* (DOT)?
;


declaredValueName returns[Token returnToken=null]:
	returnToken=variableName
;

temporaryList[AbstractMethodDescription pMethodDescription] {
	Token	varNameToken;
}:
	(
		varNameToken=declaredValueName {
			pMethodDescription.addLocalVariable(varNameToken.getText());
			getMethodScope().addVariableNameToken(varNameToken);
		}
	)*
;

temporaries[AbstractMethodDescription pMethodDescription]:
	PIPE temporaryList[pMethodDescription] PIPE |
	PIPES_2
;

statements[SmileBuilder pSmileBuilder, Value pResult] 
	returns[Value result=pResult]:
	
	(
		CARET result = expression[pSmileBuilder] (DOT)? {
			if (currentScopeIsMethodScope()) {
				pSmileBuilder.addReturn(result);
			} else {
				pSmileBuilder.addBlockReturn(result);
			}
		}|

		result = expression[pSmileBuilder] (
			DOT result = statements[pSmileBuilder, result]
		)?
	)?
;

blockDeclarations[BlockDescription pBlockDescription]:
	temporaries[pBlockDescription] |
	
	(
		argument:BLOCK_ARGUMENT {
			pBlockDescription.addParameter(getParameterNameFromBlockArgument(argument.getText()));
			getMethodScope().addVariableNameToken(argument);
		}
	)+
	(
		PIPE (temporaries[pBlockDescription])? |
		
		PIPES_2 temporaryList[pBlockDescription] PIPE |
		
		PIPES_3
	)
;

blockConstructor returns[BlockDescription returnBlockDescription] {
	Value result;
	returnBlockDescription 	= buildBlockDescription();
	pushBlockScope(returnBlockDescription);
}:
	LEFT_BRACKET
	(blockDeclarations[returnBlockDescription])?
	result=statements[returnBlockDescription.getSmileBuilder(), NilLiteral.NIL]
	RIGHT_BRACKET {
		// ANSI 3.4.4 Blocks: the object reference that is the value of the last statement is returned as the value of the message send that activated the block
		returnBlockDescription.addReturnIfNecessary(result);
		popBlockScope();
	}
;

pragma[MethodDescription pMethodDescription] {
	Literal	myLiteral;
}:
	LESS
	(
		identifier:IDENTIFIER {
			processPragma(identifier.getText(), pMethodDescription);
		} |
    	(
    		keyword:KEYWORD myLiteral=literal {
    			processPragma(keyword.getText(), myLiteral, pMethodDescription);
    		}
    	)+
    )
	GREATER
;

messagePattern returns[MethodDescription returnMethodDescription=null] {
	String			selector;
	Token			parameterToken;

	List<String> 	keywords 		= new ArrayList<String>();
	List<String> 	parameters		= new ArrayList<String>();
	List<Token> 	parameterTokens	= new ArrayList<Token>();
	String			binSelector;
}:
	selector=unarySelector {
		returnMethodDescription=buildUnaryMethodDescription(selector);
	}|

	binSelector=binarySelector parameterToken=declaredValueName {
		returnMethodDescription=buildBinaryMethodDescription(binSelector);
		returnMethodDescription.addParameter(parameterToken.getText());
		returnMethodDescription.addVariableNameToken(parameterToken);
	} |

	(
		keyword:KEYWORD parameterToken=declaredValueName {
			keywords.add(keyword.getText());
			parameters.add(parameterToken.getText());
			parameterTokens.add(parameterToken);
		}
	)+ {
		returnMethodDescription=buildKeywordMethodDescription(keywords);
		returnMethodDescription.addParameters(parameters);
		returnMethodDescription.addVariableNameTokens(parameterTokens);
	}
;

method returns[MethodDescription returnMethodDescription=null] {
	Value 				result;
	MethodDescription	methodDescription;
}:
	methodDescription=messagePattern {
		pushBlockScope(methodDescription);
	}
	(pragma[methodDescription])*
	(temporaries[methodDescription])?
	result = statements[methodDescription.getSmileBuilder(), NilLiteral.NIL] {
		// ANSI 3.4.2 Method definition: Otherwise the value of the method is the current binding of the reserved identifier 'self'.		
		methodDescription.addReturnIfNecessary(Value.SELF);
		popBlockScope();
		returnMethodDescription = methodDescription;		
	}
;
