package edu.uvg;

public interface ScriptInterpreter {
    public boolean evaluate(String scriptSig, String scriptPubKey);
}
