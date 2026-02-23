package edu.uvg;
import java.util.ArrayList;
import java.util.List;

public class ScriptTokenizer {
    public List<Token> tokenize(String scriptText) {
        List<Token> tokens = new ArrayList<>();

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