package edu.uvg;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ScriptEngineTest {

    private ScriptEngine engine;

    @Before
    public void setUp() {
        engine = new ScriptEngine(); 

    }

   
    @Test
    public void testPushData() {
        List<Token> tokens = new ArrayList<>();

        
        tokens.add(new Token(
                TokenType.DATA,
                null,
                new byte[]{1}
        ));

        boolean result = engine.evaluate(tokens);

        
        assertTrue(result);
    }

    
    @Test
    public void testOpDup() {
        List<Token> tokens = new ArrayList<>();

        tokens.add(new Token(TokenType.DATA, null, new byte[]{1}));
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_DUP, null));
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_EQUAL, null));

        boolean result = engine.evaluate(tokens);

        assertTrue(result);
    }

    
    @Test
    public void testOpEqualTrue() {
        List<Token> tokens = new ArrayList<>();

        tokens.add(new Token(TokenType.DATA, null, new byte[]{5}));
        tokens.add(new Token(TokenType.DATA, null, new byte[]{5}));
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_EQUAL, null));

        boolean result = engine.evaluate(tokens);

        assertTrue(result);
    }

    @Test
    public void testOpEqualFalse() {
        List<Token> tokens = new ArrayList<>();

        tokens.add(new Token(TokenType.DATA, null, new byte[]{5}));
        tokens.add(new Token(TokenType.DATA, null, new byte[]{9}));
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_EQUAL, null));

        boolean result = engine.evaluate(tokens);

        assertFalse(result);
    }

    
    @Test
    public void testOpDupFailsWithEmptyStack() {
        List<Token> tokens = new ArrayList<>();

        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_DUP, null));

        boolean result = engine.evaluate(tokens);

        assertFalse(result);
    }

    @Test
    public void testP2PKHValidSimulation() {
        List<Token> tokens = new ArrayList<>();

        // scriptSig
        tokens.add(new Token(TokenType.DATA, null, new byte[]{10})); // signature
        tokens.add(new Token(TokenType.DATA, null, new byte[]{20})); // pubKey

        // scriptPubKey
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_DUP, null));
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_HASH160, null));

        // hash esperado
        tokens.add(new Token(TokenType.DATA, null,
                sha256(new byte[]{20})
        ));

        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_EQUALVERIFY, null));
        tokens.add(new Token(TokenType.OPCODE, OpCode.OP_CHECKSIG, null));

        boolean result = engine.evaluate(tokens);

        assertTrue(result);
    }
x   
    
    private byte[] sha256(byte[] input) {
        try {
            java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (Exception e) {
            return new byte[]{0};
        }
    }
}