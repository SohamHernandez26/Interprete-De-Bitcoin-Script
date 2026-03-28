package edu.uvg.interpreter;

import edu.uvg.model.StackItem;
import edu.uvg.opcode.OpCode;
import edu.uvg.opcode.OpcodeFactory;
import edu.uvg.service.HashService;
import edu.uvg.service.MockSignatureChecker;
import edu.uvg.service.SignatureChecker;
import edu.uvg.service.SimpleHashService;
import edu.uvg.tokenizer.ScriptTokenizer;
import edu.uvg.tokenizer.Token;
import java.util.ArrayList;
import java.util.List;

public class ScriptEngine implements ScriptInterpreter {
    private final ScriptTokenizer scriptTokenizer;
    private final SignatureChecker signatureChecker;
    private final HashService hashService;
    private final OpcodeFactory opcodeFactory;

    public ScriptEngine() {
        this.scriptTokenizer = new ScriptTokenizer();
        this.signatureChecker = new MockSignatureChecker();
        this.hashService = new SimpleHashService();
        this.opcodeFactory = new OpcodeFactory();
    }

    @Override
    public boolean evaluate(String scriptSig, String scriptPubKey) {
        String fullScript = (scriptSig == null ? "" : scriptSig.trim())
                + " "
                + (scriptPubKey == null ? "" : scriptPubKey.trim());
        List<Token> tokens = scriptTokenizer.tokenize(fullScript.trim());
        return evaluate(tokens);
    }

    public boolean evaluate(List<Token> tokens) {
        return evaluateDetailed(tokens).isSuccess();
    }

    public ExecutionResult evaluateDetailed(String scriptSig, String scriptPubKey) {
        String fullScript = (scriptSig == null ? "" : scriptSig.trim())
                + " "
                + (scriptPubKey == null ? "" : scriptPubKey.trim());
        List<Token> tokens = scriptTokenizer.tokenize(fullScript.trim());
        return evaluateDetailed(tokens);
    }

    public ExecutionResult evaluateDetailed(List<Token> tokens) {
        ExecutionContext context = new ExecutionContext();

        try {
            for (Token token : tokens) {
                if (context.hasError() || context.isHalted()) {
                    break;
                }

                if (token.isOpCode() && token.getOpcode() == OpCode.OP_IF) {
                    executeIf(token, context);
                    continue;
                }
                if (token.isOpCode() && token.getOpcode() == OpCode.OP_ELSE) {
                    executeElse(context);
                    continue;
                }
                if (token.isOpCode() && token.getOpcode() == OpCode.OP_ENDIF) {
                    executeEndIf(context);
                    continue;
                }

                if (!context.currentCondition()) {
                    context.addTrace("SKIP: " + describeToken(token));
                    continue;
                }

                if (token.isData()) {
                    context.push(new StackItem(token.getData()));
                    context.addTrace("PUSH DATA");
                    continue;
                }

                executeOpcode(token.getOpcode(), context);
            }

            if (!context.hasError() && context.hasOpenConditions()) {
                context.setError("Condicional sin cerrar con OP_ENDIF");
            }

            boolean success = buildFinalResult(context);
            return new ExecutionResult(success, context.getErrorMessage(), context.getTraceLog(), context.copyStack());
        } catch (Exception e) {
            context.setError("Error inesperado: " + e.getMessage());
            return new ExecutionResult(false, context.getErrorMessage(), context.getTraceLog(), context.copyStack());
        }
    }

    private boolean buildFinalResult(ExecutionContext context) {
        if (context.hasError() || context.isStackEmpty()) {
            return false;
        }
        return context.peek().asBoolean();
    }

    private void executeIf(Token token, ExecutionContext context) {
        if (!context.currentCondition()) {
            context.pushCondition(false);
            context.addTrace("OP_IF (bloque externo falso)");
            return;
        }

        if (context.isStackEmpty()) {
            context.setError("OP_IF requiere un elemento en la pila");
            return;
        }

        boolean condition = context.pop().asBoolean();
        context.pushCondition(condition);
        context.addTrace("OP_IF -> " + condition);
    }

    private void executeElse(ExecutionContext context) {
        if (!context.hasOpenConditions()) {
            context.setError("OP_ELSE sin OP_IF previo");
            return;
        }
        context.invertCurrentCondition();
        context.addTrace("OP_ELSE");
    }

    private void executeEndIf(ExecutionContext context) {
        if (!context.hasOpenConditions()) {
            context.setError("OP_ENDIF sin OP_IF previo");
            return;
        }
        context.popCondition();
        context.addTrace("OP_ENDIF");
    }

    private void executeOpcode(OpCode op, ExecutionContext context) {
        if (op == null) {
            context.setError("Opcode nulo");
            return;
        }

        switch (op) {
            case OP_0, OP_1, OP_2, OP_3, OP_4, OP_5, OP_6, OP_7,
                    OP_8, OP_9, OP_10, OP_11, OP_12, OP_13, OP_14, OP_15, OP_16 -> {
                int number = opcodeToNumber(op);
                context.push(new StackItem(new byte[]{(byte) number}));
                context.addTrace(op.name());
            }
            case OP_DUP -> executeDup(context);
            case OP_DROP -> executeDrop(context);
            case OP_EQUAL -> executeEqual(context);
            case OP_EQUALVERIFY -> executeEqualVerify(context);
            case OP_HASH160 -> executeHash160(context);
            case OP_CHECKSIG -> executeCheckSig(context);
            case OP_VERIFY -> executeVerify(context);
            case OP_RETURN -> {
                context.addTrace("OP_RETURN");
                context.setError("Script detenido por OP_RETURN");
                context.halt();
            }
            case OP_CHECKMULTISIG -> executeCheckMultiSig(context);
            case PUSHDATA -> context.addTrace("PUSHDATA (el dato ya fue tokenizado como DATA)");
            default -> context.setError("Opcode no soportado: " + op);
        }
    }

    private int opcodeToNumber(OpCode op) {
        if (op == OpCode.OP_0) {
            return 0;
        }
        return Integer.parseInt(op.name().replace("OP_", ""));
    }

    private void executeDup(ExecutionContext context) {
        if (context.isStackEmpty()) {
            context.setError("OP_DUP requiere un elemento en la pila");
            return;
        }
        StackItem top = context.peek();
        context.push(new StackItem(top.getData().clone()));
        context.addTrace("OP_DUP");
    }

    private void executeDrop(ExecutionContext context) {
        if (context.isStackEmpty()) {
            context.setError("OP_DROP requiere un elemento en la pila");
            return;
        }
        context.pop();
        context.addTrace("OP_DROP");
    }

    private void executeEqual(ExecutionContext context) {
        if (context.stackSize() < 2) {
            context.setError("OP_EQUAL requiere dos elementos en la pila");
            return;
        }
        StackItem first = context.pop();
        StackItem second = context.pop();
        boolean equal = first.equals(second);
        context.push(new StackItem(equal ? new byte[]{1} : new byte[]{0}));
        context.addTrace("OP_EQUAL -> " + equal);
    }

    private void executeEqualVerify(ExecutionContext context) {
        if (context.stackSize() < 2) {
            context.setError("OP_EQUALVERIFY requiere dos elementos en la pila");
            return;
        }
        StackItem first = context.pop();
        StackItem second = context.pop();
        boolean equal = first.equals(second);
        context.addTrace("OP_EQUALVERIFY -> " + equal);
        if (!equal) {
            context.setError("OP_EQUALVERIFY falló");
        }
    }

    private void executeHash160(ExecutionContext context) {
        if (context.isStackEmpty()) {
            context.setError("OP_HASH160 requiere un elemento en la pila");
            return;
        }
        StackItem data = context.pop();
        context.push(new StackItem(hashService.hash160(data.getData())));
        context.addTrace("OP_HASH160");
    }

    private void executeCheckSig(ExecutionContext context) {
        if (context.stackSize() < 2) {
            context.setError("OP_CHECKSIG requiere firma y clave pública");
            return;
        }
        StackItem pubKey = context.pop();
        StackItem signature = context.pop();
        boolean valid = signatureChecker.checkSignature(signature.getData(), pubKey.getData());
        context.push(new StackItem(valid ? new byte[]{1} : new byte[]{0}));
        context.addTrace("OP_CHECKSIG -> " + valid);
    }

    private void executeVerify(ExecutionContext context) {
        if (context.isStackEmpty()) {
            context.setError("OP_VERIFY requiere un elemento en la pila");
            return;
        }
        boolean valid = context.pop().asBoolean();
        context.addTrace("OP_VERIFY -> " + valid);
        if (!valid) {
            context.setError("OP_VERIFY falló");
        }
    }

    private void executeCheckMultiSig(ExecutionContext context) {
        if (context.stackSize() < 1) {
            context.setError("OP_CHECKMULTISIG requiere datos en la pila");
            return;
        }

        StackItem requiredItem = context.pop();
        int required = requiredItem.getData().length == 0 ? 0 : requiredItem.getData()[0];
        if (required <= 0) {
            context.setError("OP_CHECKMULTISIG requiere un número válido de firmas");
            return;
        }

        List<byte[]> signatures = new ArrayList<>();
        List<byte[]> publicKeys = new ArrayList<>();

        while (context.stackSize() >= 2) {
            byte[] publicKey = context.pop().getData();
            byte[] signature = context.pop().getData();
            publicKeys.add(0, publicKey);
            signatures.add(0, signature);
        }

        boolean valid = signatureChecker.checkMultiSig(signatures, publicKeys, required);
        context.push(new StackItem(valid ? new byte[]{1} : new byte[]{0}));
        context.addTrace("OP_CHECKMULTISIG -> " + valid);
    }

    private String describeToken(Token token) {
        if (token.isData()) {
            return "DATA";
        }
        return token.getOpcode().name();
    }
}
