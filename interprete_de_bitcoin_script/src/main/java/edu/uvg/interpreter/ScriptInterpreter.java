package edu.uvg.interpreter;

public interface ScriptInterpreter {
    boolean evaluate(String scriptSig, String scriptPubKey);
}
