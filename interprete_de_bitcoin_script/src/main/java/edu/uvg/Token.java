package edu.uvg;

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

    public OpCode getOpCode() {
        return opCode;
    }

    public byte[] getData() {
        return data;
    }

    OpCode getOpcode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

}