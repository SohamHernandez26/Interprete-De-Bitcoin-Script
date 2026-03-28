package edu.uvg.interpreter;

import java.util.List;
import java.util.Stack;

import edu.uvg.model.StackItem;


// Resultado final de la evaluación de un script.//
public class ExecutionResult {
    private final boolean success;
    private final String errorMessage;
    private final List<String> traceLog;
    private final Stack<StackItem> finalStack;

    public ExecutionResult(boolean success, String errorMessage, List<String> traceLog, Stack<StackItem> finalStack) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.traceLog = traceLog;
        this.finalStack = finalStack;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getTraceLog() {
        return traceLog;
    }

    public Stack<StackItem> getFinalStack() {
        return finalStack;
    }
}
