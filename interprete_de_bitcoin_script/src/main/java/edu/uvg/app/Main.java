package edu.uvg.app;

import edu.uvg.interpreter.ExecutionResult;
import edu.uvg.interpreter.ScriptEngine;
public class Main {
    public static void main(String[] args) {
        ScriptEngine engine = new ScriptEngine();

        String scriptSig = "firma pubKey";
        String scriptPubKey = "OP_DUP OP_HASH160 pubKey OP_EQUALVERIFY OP_CHECKSIG";

        ExecutionResult result = engine.evaluateDetailed(scriptSig, scriptPubKey);

        System.out.println("Resultado: " + result.isSuccess());
        if (result.getErrorMessage() != null) {
            System.out.println("Error: " + result.getErrorMessage());
        }
        System.out.println("Trace:");
        for (String line : result.getTraceLog()) {
            System.out.println(" - " + line);
        }
    }
}
