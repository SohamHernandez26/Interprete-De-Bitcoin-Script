package edu.uvg.interpreter;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.uvg.model.StackItem;

// Mantiene el estado de ejecución del script.//
public class ExecutionContext {
    private final Stack<StackItem> stack;
    private final Stack<Boolean> conditionStack;
    private final LinkedList<String> traceLog;
    private String errorMessage;
    private boolean halted;

    public ExecutionContext() {
        this.stack = new Stack<>();
        this.conditionStack = new Stack<>();
        this.traceLog = new LinkedList<>();
        this.errorMessage = null;
        this.halted = false;
    }

    public void push(StackItem item) {
        stack.push(item);
    }

    public StackItem pop() {
        return stack.pop();
    }

    public StackItem peek() {
        return stack.peek();
    }

    public int stackSize() {
        return stack.size();
    }

    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    public Stack<StackItem> copyStack() {
        Stack<StackItem> copy = new Stack<>();
        copy.addAll(stack);
        return copy;
    }

    public void pushCondition(boolean value) {
        conditionStack.push(value);
    }

    public boolean popCondition() {
        return conditionStack.pop();
    }

    public boolean hasOpenConditions() {
        return !conditionStack.isEmpty();
    }

    public boolean currentCondition() {
        return conditionStack.isEmpty() || conditionStack.peek();
    }

    public void invertCurrentCondition() {
        if (!conditionStack.isEmpty()) {
            conditionStack.push(!conditionStack.pop());
        }
    }

    public void addTrace(String message) {
        traceLog.add(message);
    }

    public List<String> getTraceLog() {
        return new LinkedList<>(traceLog);
    }

    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null;
    }

    public void halt() {
        this.halted = true;
    }

    public boolean isHalted() {
        return halted;
    }
}
