package edu.uvg.service;

import java.util.List;
/**
 * Simulación simple de validación de firmas.
 */
public class MockSignatureChecker implements SignatureChecker {

    @Override
    public boolean checkSignature(byte[] signature, byte[] publicKey) {
        return signature != null && publicKey != null
                && signature.length > 0 && publicKey.length > 0;
    }

    @Override
    public boolean checkMultiSig(List<byte[]> signatures, List<byte[]> publicKeys, int required) {
        if (signatures == null || publicKeys == null || required <= 0) {
            return false;
        }
        int valid = Math.min(signatures.size(), publicKeys.size());
        return valid >= required;
    }
}
