package edu.uvg;
import java.security.MessageDigest;
import java.util.List;
import java.util.Stack;

public class ScriptEngine {
    private Stack<StackItem> stack;
    private ScriptTokenizer scriptTokenizer;

    public ScriptEngine() {
    this.stack = new Stack<>();
    this.scriptTokenizer = new ScriptTokenizer();
}

    public boolean evaluate(List<Token> tokens) {
        Stack<byte[]> stack = new Stack<>();

        try{
            for (Token token : tokens) {

                if (token.isData()) {
                    stack.push(token.getData());
                    continue;

                }// Lógica de cada opcode
                 OpCode op = token.getOpcode();    
                
                switch (op) {
                        case OP_DUP:
                            if (stack.isEmpty()) return false;

                            byte[] top = stack.peek();
                            stack.push(top);
                            break;
                            case OP_DROP: {
                        if (stack.isEmpty()) return false;

                        stack.pop();

                        break;
                        }
                        case OP_EQUAL: {
                        if (stack.size() < 2) return false;

                        byte[] a = stack.pop(); // primero que sale
                        byte[] b = stack.pop(); // segundo

                        boolean equal = java.util.Arrays.equals(a, b);

                        stack.push(equal ? new byte[]{1} : new byte[]{0});

                        break;
                    }
                    case OP_EQUALVERIFY: {
                        if (stack.size() < 2) return false;

                        byte[] a = stack.pop();
                        byte[] b = stack.pop();

                        if (!java.util.Arrays.equals(a, b)) {
                            return false; 
                        }
                        break;
                    }
                    case OP_HASH160: {
                        if (stack.isEmpty()) return false;

                        byte[] data = stack.pop();

                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hash = digest.digest(data);

                        stack.push(hash);

                        break;                   
                        }
                        case OP_CHECKSIG: {
                        if (stack.size() < 2) return false;

                        byte[] pubKey = stack.pop();   // clave pública
                        byte[] signature = stack.pop(); // firma

                        boolean valid = simulateCheckSig(signature, pubKey);

                        stack.push(valid ? new byte[]{1} : new byte[]{0});

                        break;
                    }

                    default:
                        return false; 
                        }

                    
                }
                if (stack.isEmpty()) return false;

            byte[] result = stack.pop();

            return result.length > 0 && result[0] != 0;

        } catch (Exception e) {
            return false;
        }
    }
    private boolean simulateCheckSig(byte[] signature, byte[] pubKey) {

        return signature.length > 0 && pubKey.length > 0;
    }
            
        } 
