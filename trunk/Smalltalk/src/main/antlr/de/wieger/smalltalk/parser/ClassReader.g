header
{
package de.wieger.smalltalk.parser;

import org.apache.log4j.Logger;
import java.util.*;
import de.wieger.smalltalk.smile.*;
}
class ClassReaderLexer extends Lexer("de.wieger.antlr.AbstractPositionAwareLexer");

options {
	k				= 3;
    charVocabulary	= '\u0000'..'\uFFFE';
	filter			= false;
}

WHITE:			' ' | '\n' | '\r' | '\t';

BANG: 			"!" (WHITE)*;

OTHER:			~('!' | ' ' | '\n' | '\r' | '\t');

BANGBANG:		"!!";
END_METHODS:	'!' WHITE '!' (WHITE)*;


class ClassReader extends Parser("de.wieger.smalltalk.parser.AbstractClassReader");

options {
	k					= 1;
	defaultErrorHandler	= false;
}

{
	static final Logger LOG = Logger.getLogger(SmalltalkParser.class);
}

fileIn:
	(methodsList | executableExpression)*
; exception catch[RecognitionException ex] {
	notifyListeners(ex.getMessage(), ex.getColumn()-1, ex.getColumn()-1);
}

methodsList {
	Content	hdr;
}:
	BANG
	hdr=content {
		parseMethodsFor(hdr.toString(), hdr.getStart(), hdr.getEnd());
	} BANG
	methods
;

methods {
	Content 		method;
	StringBuilder 	comment;
}:
	(content BANG) =>
		(
			method=content {
				parseMethod(method.toString(), method.getStart(), method.getEnd());
			}
			BANG
		)
		methods |
	
	(content END_METHODS) =>
		(
			method=content {
				parseMethod(method.toString(), method.getStart(), method.getEnd());
			}
			END_METHODS
		)
;

executableExpression {
	Content expr;
}:
	expr=content {
		parseExpression(expr.toString(), expr.getStart(), expr.getEnd());
	}
	BANG;


content returns[Content returnContent=new Content()]:
	(
		other:OTHER {
			returnContent.setStart(other.getColumn()-1);
			returnContent.append(other.getText());
		} |
		white:WHITE {
			returnContent.setStart(white.getColumn()-1);
			returnContent.append(white.getText());
		} |
		bangbang:BANGBANG {
			returnContent.setStart(bangbang.getColumn()-1);
			returnContent.append('!');
		}
	)+
;
