package edu.uvg.service;

import java.util.List;

public interface SignatureChecker {
    boolean checkSignature(byte[] signature, byte[] publicKey);
    boolean checkMultiSig(List<byte[]> signatures, List<byte[]> publicKeys, int required);
}
