package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import java.util.Arrays;

import org.testng.annotations.Test;

import de.wieger.commons.lang.StringUtil;


public class TestAbstractSmalltalkParser {
    @Test
    public void testGetMethodName() {
        final String[] METHOD1_KEYWORDS = {"baby:", "do:", "it:", "one:", "more:", "time:"};
        assertEquals(AbstractSmalltalkParser.getMethodNameFromKeywords(Arrays.asList(METHOD1_KEYWORDS)), "babyDoItOneMoreTime");

        final String[] METHOD2_KEYWORDS = {"baby:"};
        assertEquals(AbstractSmalltalkParser.getMethodNameFromKeywords(Arrays.asList(METHOD2_KEYWORDS)), "baby");
    }

    @Test
    public void testGetContentFromStringLiteral() {
        assertEquals(AbstractSmalltalkParser.getContentFromStringLiteral("'Yuppa'"), "Yuppa");
        assertEquals(AbstractSmalltalkParser.getContentFromStringLiteral("''"), "");
    }

    @Test
    public void testGetVarNameFromKeyword() {
        assertEquals(AbstractSmalltalkParser.getVarNameFromKeyword("keyword:"), "keyword");
    }

    @Test
    public void testGetStringWithoutLastChar() {
        StringBuilder builder = new StringBuilder("yuppa");
        StringUtil.removeLastChar(builder);
        assertEquals(builder.toString(), "yupp");
    }
}
