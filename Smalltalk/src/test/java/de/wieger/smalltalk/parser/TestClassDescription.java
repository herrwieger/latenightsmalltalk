package de.wieger.smalltalk.parser;

import static org.testng.Assert.*;

import java.util.List;

import org.testng.annotations.Test;

import de.wieger.smalltalk.smile.ClassDescription;

public class TestClassDescription {
    @Test
    public void testAsList() {
        List    names   = ClassDescription.asList("tick trick track");
        assertEquals(names.size(), 3);
        assertTrue(names.contains("tick"));
        assertTrue(names.contains("trick"));
        assertTrue(names.contains("track"));

        names   = ClassDescription.asList("");
        assertTrue(names.isEmpty());

        names   = ClassDescription.asList("t");
        assertEquals(names.size(), 1);
        assertTrue(names.contains("t"));
    }
}
