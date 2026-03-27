package edu.uvg.opcode;

import java.util.HashMap;
import java.util.Map;

/**
 * Registro simple de opcodes válidos.
 */
public class OpcodeFactory {
    private final Map<String, OpCode> opcodeMap;

    public OpcodeFactory() {
        opcodeMap = new HashMap<>();
        for (OpCode code : OpCode.values()) {
            opcodeMap.put(code.name(), code);
        }
    }

    public OpCode getOpcode(String opcodeName) {
        return opcodeMap.get(opcodeName);
    }
}
