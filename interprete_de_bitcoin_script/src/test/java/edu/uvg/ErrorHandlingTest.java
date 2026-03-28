package edu.uvg;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import edu.uvg.interpreter.ExecutionResult;
import edu.uvg.interpreter.ScriptEngine;

public class ErrorHandlingTest {

    private ScriptEngine engine;

    @Before
    public void setUp() {
        engine = new ScriptEngine();
    }

    @Test
    public void testElseWithoutIfFails() {
        ExecutionResult result = engine.evaluateDetailed("", "OP_ELSE");
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void testVerifyFailsWithFalse() {
        ExecutionResult result = engine.evaluateDetailed("", "OP_0 OP_VERIFY");
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }
}
