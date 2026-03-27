package edu.uvg.tokenizer;

import java.util.ArrayList;
import java.util.List;

import edu.uvg.opcode.OpCode;

public class ScriptTokenizer {
    public List<Token> tokenize(String scriptText) {
        List<Token> tokens = new ArrayList<>();

        if (scriptText == null || scriptText.trim().isEmpty()) {
            return tokens;
        }

        String[] parts = scriptText.trim().split("\\s+");

        for (String part : parts) {
            if (part.startsWith("OP_")) {
                OpCode code = OpCode.valueOf(part);
                tokens.add(new Token(TokenType.OPCODE, code, null));
            } else {
                byte[] data = part.getBytes();
                tokens.add(new Token(TokenType.DATA, null, data));
            }
        }

        return tokens;
    }
}
