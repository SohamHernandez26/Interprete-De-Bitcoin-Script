package edu.uvg.tokenizer;
import edu.uvg.opcode.OpCode;
public class Token {
    private final TokenType type;
    private final OpCode opCode;
    private final byte[] data;

    public Token(TokenType type, OpCode opCode, byte[] data) {
        this.type = type;
        this.opCode = opCode;
        this.data = data;
    }

    public boolean isOpCode() {
        return type == TokenType.OPCODE;
    }

    public boolean isData() {
        return type == TokenType.DATA;
    }

    public TokenType getType() {
        return type;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public OpCode getOpcode() {
        return opCode;
    }

    public byte[] getData() {
        return data;
    }
}
