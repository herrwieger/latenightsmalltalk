package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import de.wieger.smalltalk.smile.ClassDescription;
import de.wieger.smalltalk.smile.MethodDescription;
import de.wieger.smalltalk.smile.SmileBuilder;
import de.wieger.smalltalk.universe.JavassistUniverse;
import de.wieger.smalltalk.universe.Universe;


public class TestParser {
	//--------------------------------------------------------------------------
	// constants
	//--------------------------------------------------------------------------

    private static final String[] BINARY_SELECTORS = {
        "-",
        "=",
        "<",
        ">",
        "|",
        "||",
        "<=",
        ">=",
        "<>",
        "==",
    };

    private static final String[] VARIABLE_NAMES	= {
		"a",
		"a.b",
		"a.b.c.d",
		"snapples"
	};

	private static final String[] SYMBOLS	= {
		"a",
		"+",
		"hadde:",
		"hadde:wadde:dudde:da:"
	};


	private static final String[] BLOCK_CONSTRUCTORS	= {
		"[:eachChar|Transcript show: eachChar]",

	};

	private static final String[] STATEMENTS	= {
		"Transcript show: 'Hello World!'.",

		"'Hello World' do: [ :eachChar | "
		+ "Transcript show: eachChar ; cr . "
		+ "]. knuff",
	};


	private static final String[] METHODS	= {
        "at: index <primitive:60>",

		"postBuildWith: aBuilder " +
		"	| listController |" +
		"	super postBuildWith: aBuilder." +
		"	listController := (self builder componentAt: #petsLB) widget controller." +
		"	listController keyboardHook: self myKeyboardHookBlock." +
		"	listController dispatcher doubleClick: [self blockDoubleClick].",

		"	deleteSelection" +
		"	| index |" +
		"	index := self pets selectionIndex." +
		"	index > 0 ifTrue: [self pets list removeAtIndex: index]",

		"expandSelection" +
		"	self pets selectionIndex > 0" +
		"		ifTrue: [Dialog warn: 'Details of selection']",

		"myKeyboardHookBlock" +
		"	^" +
		"	[:event :controller | " +
		"	| selector |" +
		"	selector := keyboardSelectors at: event keyValue ifAbsent: [nil]." +
		"	selector notNil ifTrue: [self perform: selector]." +
		"	event]",
	};


	//--------------------------------------------------------------------------
	// class variables
	//--------------------------------------------------------------------------

	private static final Logger LOG = Logger.getLogger(TestParser.class);



	//--------------------------------------------------------------------------
	// test methods
	//--------------------------------------------------------------------------

    @Test
    public void testParseBinarySelector() {
        parserTest(BINARY_SELECTORS, new ParserTest(){
            public void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException {
                pParser.binarySelector();
            }
        });
    }

    @Test
    public void testBinarySelectorsAndBlanks() throws Exception {
        SmalltalkParser parser = setupParser("- < > = | ||");
        assertEquals(parser.binarySelector(), "-");
        assertEquals(parser.binarySelector(), "<");
        assertEquals(parser.binarySelector(), ">");
        assertEquals(parser.binarySelector(), "=");
        assertEquals(parser.binarySelector(), "|");
        assertEquals(parser.binarySelector(), "||");
        parser.match(SmalltalkTokenTypes.EOF);
    }

    @Test
    public void testParseVariablename() {
    	parserTest(VARIABLE_NAMES, new ParserTest(){
            public void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException {
                pParser.variableName();
            }
        });
    }

    @Test
    public void testParseSymbol() {
    	parserTest(SYMBOLS,  new ParserTest(){
            public void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException {
                pParser.symbol();
            }
        });
    }

    @Test
    public void testBlockConstructor() {
    	parserTest(BLOCK_CONSTRUCTORS,  new ParserTest(){
            public void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException {
                pParser.pushBlockScope(new MethodDescription("dummy", "dummy", pParser.getCurrentClass()));
                pParser.blockConstructor();
            }
        });
    }

    @Test
    public void testStatement() {
    	parserTest(STATEMENTS,  new ParserTest(){
            public void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException {
                pParser.pushBlockScope(new MethodDescription("dummy", "dummy", pParser.getCurrentClass()));
                pParser.statements(new SmileBuilder(), null);
            }
        });
    }

    @Test
    public void testMethod() {
        parserTest(METHODS, new ParserTest(){
            public void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException {
                pParser.method();
            }
        });
    }

    private void parserTest(String[] pStringsToParse, ParserTest pTest) {
    	for (String stringToParse : pStringsToParse) {
        	parserTest(pTest, stringToParse);
		}
    }

    private void parserTest(ParserTest pTest, String pStringToParse) {
        try {
            SmalltalkParser parser = setupParser(pStringToParse);

            pTest.run(parser);
        	parser.match(SmalltalkTokenTypes.EOF);
        } catch (Throwable thrown) {
        	LOG.error("Da ging was tierisch schief");
        	fail("'" + pStringToParse + "' konnte nicht geparsed werden" , thrown);
        }
    }

    private SmalltalkParser setupParser(String pStringToParse) {
        Universe            universe    = new JavassistUniverse();
        SmalltalkParser     parser      = ParserUtil.getParser(pStringToParse);
        ClassDescription    smallClass  = universe.getBaseClass().subclass("ParserTest");
        parser.setCurrentClass(smallClass);
        return parser;
    }

    private static interface ParserTest {
        void run(SmalltalkParser pParser) throws RecognitionException, TokenStreamException;
    }
}
