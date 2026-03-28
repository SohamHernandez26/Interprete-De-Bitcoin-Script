package edu.uvg;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import edu.uvg.interpreter.ScriptEngine;

public class ConditionalScriptTest {

    private ScriptEngine engine;

    @Before
    public void setUp() {
        engine = new ScriptEngine();
    }

    @Test
    public void testIfBranchTrue() {
        boolean result = engine.evaluate("OP_1", "OP_IF OP_1 OP_ELSE OP_0 OP_ENDIF");
        assertTrue(result);
    }

    @Test
    public void testIfBranchFalse() {
        boolean result = engine.evaluate("OP_0", "OP_IF OP_1 OP_ELSE OP_0 OP_ENDIF");
        assertFalse(result);
    }
}
